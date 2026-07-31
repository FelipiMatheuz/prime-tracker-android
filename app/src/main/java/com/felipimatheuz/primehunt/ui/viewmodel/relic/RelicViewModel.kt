package com.felipimatheuz.primehunt.ui.viewmodel.relic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.data.remote.enums.RelicEra
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.domain.model.RelicDomain
import com.felipimatheuz.primehunt.domain.usecase.relic.GetRelicsUseCase
import com.felipimatheuz.primehunt.ui.mvi.MviViewModel
import com.felipimatheuz.primehunt.ui.viewmodel.primeset.ProgressFilter
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
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class RelicViewModel @Inject constructor(
    getRelicsUseCase: GetRelicsUseCase
) : ViewModel(), MviViewModel<RelicState, RelicIntent> {

    private val _searchText = MutableStateFlow("")
    private val _filters = MutableStateFlow(RelicFilters())
    private val _selectedView = MutableStateFlow(RelicsView.ERA)

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

            val matchesProgress = when (filters.progress) {
                ProgressFilter.ALL -> true
                ProgressFilter.COMPLETE -> relic.isCompleted
                ProgressFilter.INCOMPLETE -> !relic.isCompleted
                ProgressFilter.IN_PROGRESS -> !relic.isCompleted && relic.missingCount < relic.rewards.size
                ProgressFilter.NOT_STARTED -> relic.missingCount == relic.rewards.count { !it.isForma }
            }

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
            is RelicIntent.UpdateFilters -> _filters.value = intent.filters
            is RelicIntent.ChangeView -> _selectedView.value = intent.view
        }
    }
}
