package com.felipimatheuz.primehunt.ui.viewmodel.relic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.data.local.enums.RelicsView
import com.felipimatheuz.primehunt.data.local.preferences.RelicUiPrefs
import com.felipimatheuz.primehunt.data.remote.enums.RelicEra
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.data.repository.UiPreferencesRepository
import com.felipimatheuz.primehunt.domain.model.RelicDomain
import com.felipimatheuz.primehunt.domain.usecase.relic.GetRelicsUseCase
import com.felipimatheuz.primehunt.domain.usecase.util.ProgressFilterUseCase
import com.felipimatheuz.primehunt.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class RelicViewModel @Inject constructor(
    getRelicsUseCase: GetRelicsUseCase,
    private val progressFilterUseCase: ProgressFilterUseCase,
    private val uiPreferencesRepository: UiPreferencesRepository
) : ViewModel(), MviViewModel<RelicState, RelicIntent> {

    private val _searchText = MutableStateFlow("")
    private val _filters = MutableStateFlow(RelicFilters())
    private val _selectedView = MutableStateFlow(RelicsView.ERA)

    init {
        viewModelScope.launch {
            uiPreferencesRepository.relicPrefs.take(1).collect { prefs ->
                _filters.value = RelicFilters(
                    eras = prefs.eras,
                    availabilities = prefs.availabilities,
                    progress = prefs.progress
                )
                _selectedView.value = prefs.selectedView
            }
        }
    }

    @OptIn(FlowPreview::class)
    override val state: StateFlow<RelicState> = combine(
        getRelicsUseCase().distinctUntilChanged(),
        _searchText.debounce(300.milliseconds).distinctUntilChanged(),
        _filters,
        _selectedView
    ) { relics, query, filters, selectedView ->
        val filteredRelics = applyFilters(relics, query, filters)
        val grouped = groupRelics(filteredRelics, selectedView)

        RelicState(
            relics = filteredRelics,
            groupedRelics = grouped,
            queryFilter = query,
            activeFilters = filters,
            selectedView = selectedView,
            isLoading = false
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = RelicState()
        )

    private fun applyFilters(
        relics: List<RelicDomain>,
        query: String,
        filters: RelicFilters
    ): List<RelicDomain> {
        return relics.filter { relic ->
            val matchesQuery = relic.name.contains(query, ignoreCase = true) ||
                    relic.rewards.any { it.name.contains(query, ignoreCase = true) }

            val matchesEra = filters.eras.isEmpty() || filters.eras.contains(relic.era)

            val matchesAvailability = filters.availabilities.isEmpty() ||
                    filters.availabilities.contains(relic.source)

            val matchesProgress = progressFilterUseCase.matches(relic, filters.progress)

            matchesQuery && matchesEra && matchesAvailability && matchesProgress
        }
    }

    private fun groupRelics(
        relics: List<RelicDomain>,
        view: RelicsView
    ): Map<RelicGroup, List<RelicDomain>> {
        return when (view) {
            RelicsView.ERA -> {
                relics.groupBy {
                    when (it.era) {
                        RelicEra.LITH -> RelicGroup.Lith
                        RelicEra.MESO -> RelicGroup.Meso
                        RelicEra.NEO -> RelicGroup.Neo
                        RelicEra.AXI -> RelicGroup.Axi
                    }
                }
                    .toSortedMap(compareBy {
                        when (it) {
                            RelicGroup.Lith -> 0
                            RelicGroup.Meso -> 1
                            RelicGroup.Neo -> 2
                            else -> 4
                        }
                    })
            }

            RelicsView.AVAILABILITY -> {
                relics.groupBy {
                    when (it.source) {
                        RelicSource.MISSION -> RelicGroup.Available
                        RelicSource.RESURGENCE, RelicSource.BARO -> RelicGroup.Events
                        RelicSource.VAULT -> RelicGroup.Vaulted
                    }
                }.toSortedMap(compareBy {
                    when (it) {
                        is RelicGroup.Available -> 0
                        is RelicGroup.Events -> 1
                        else -> 2
                    }
                })
            }

            RelicsView.PROGRESS -> {
                relics.groupBy {
                    when {
                        it.isCompleted -> RelicGroup.Completed
                        it.missingCount in 1..3 -> RelicGroup.Missing1to3
                        else -> RelicGroup.Missing4Plus
                    }
                }.toSortedMap(compareBy {
                    when (it) {
                        is RelicGroup.Missing4Plus -> 0
                        is RelicGroup.Missing1to3 -> 1
                        else -> 2
                    }
                })
            }
        }
    }

    override fun onIntent(intent: RelicIntent) {
        when (intent) {
            is RelicIntent.Search -> _searchText.value = intent.query
            is RelicIntent.ClearSearch -> _searchText.value = ""
            is RelicIntent.UpdateFilters -> {
                _filters.value = intent.filters
                savePrefs()
            }

            is RelicIntent.ChangeView -> {
                _selectedView.value = intent.view
                savePrefs()
            }
        }
    }

    private fun savePrefs() {
        viewModelScope.launch {
            uiPreferencesRepository.updateRelicPrefs(
                RelicUiPrefs(
                    selectedView = _selectedView.value,
                    eras = _filters.value.eras,
                    availabilities = _filters.value.availabilities,
                    progress = _filters.value.progress
                )
            )
        }
    }
}
