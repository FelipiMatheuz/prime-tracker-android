package com.felipimatheuz.primehunt.ui.viewmodel.primeset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.domain.model.enums.PrimeType
import com.felipimatheuz.primehunt.domain.model.matches
import com.felipimatheuz.primehunt.domain.model.prefs.PrimeSetUiPrefs
import com.felipimatheuz.primehunt.domain.repository.UiPreferencesRepository
import com.felipimatheuz.primehunt.domain.usecase.primeset.GetPrimeSetsUseCase
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
class PrimeSetViewModel @Inject constructor(
    getPrimeSetsUseCase: GetPrimeSetsUseCase,
    private val uiPreferencesRepository: UiPreferencesRepository
) : ViewModel(), MviViewModel<PrimeSetState, PrimeSetIntent> {

    private val _searchText = MutableStateFlow("")
    private val _filters = MutableStateFlow(PrimeSetFilters())
    private val _selectedView = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            uiPreferencesRepository.primeSetPrefs.take(1).collect { prefs ->
                _filters.value = PrimeSetFilters(
                    progress = prefs.progress,
                    categories = prefs.categories,
                    availabilities = prefs.availabilities
                )
                _selectedView.value = prefs.selectedView
            }
        }
    }

    @OptIn(FlowPreview::class)
    override val state: StateFlow<PrimeSetState> = combine(
        getPrimeSetsUseCase.observeAllSets(),
        getPrimeSetsUseCase.observeCollections(),
        _searchText.debounce(300.milliseconds).distinctUntilChanged(),
        _filters,
        _selectedView
    ) { sets, collections, query, filters, selectedView ->
        val filtered = sets.filter { set ->
            val matchesQuery = set.name.contains(query, ignoreCase = true) ||
                    set.parts.any { it.name.name.contains(query, ignoreCase = true) }

            val matchesCategory = filters.categories.isEmpty() || filters.categories.contains(set.type)
            val matchesAvailability = filters.availabilities.isEmpty() || filters.availabilities.contains(set.availability)
            val matchesProgress = set.matches(filters.progress)

            matchesQuery && matchesCategory && matchesAvailability && matchesProgress
        }

        val grouped = filtered.groupBy { 
            when (it.type) {
                PrimeType.ARCH_GUN, PrimeType.ARCHWING -> PrimeType.COMPANION
                else -> it.type
            }
        }

        PrimeSetState(
            collections = collections,
            groupedSets = grouped,
            isLoading = false,
            queryFilter = query,
            activeFilters = filters,
            selectedView = selectedView
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = PrimeSetState()
        )

    override fun onIntent(intent: PrimeSetIntent) {
        when (intent) {
            is PrimeSetIntent.Search -> _searchText.value = intent.query
            is PrimeSetIntent.UpdateFilters -> {
                _filters.value = intent.filters
                savePrefs()
            }
            PrimeSetIntent.ClearSearch -> _searchText.value = ""
            is PrimeSetIntent.ChangeView -> {
                _selectedView.value = intent.index
                savePrefs()
            }
        }
    }

    private fun savePrefs() {
        viewModelScope.launch {
            uiPreferencesRepository.updatePrimeSetPrefs(
                PrimeSetUiPrefs(
                    selectedView = _selectedView.value,
                    progress = _filters.value.progress,
                    categories = _filters.value.categories,
                    availabilities = _filters.value.availabilities
                )
            )
        }
    }
}
