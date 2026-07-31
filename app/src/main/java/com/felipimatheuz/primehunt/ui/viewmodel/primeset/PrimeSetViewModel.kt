package com.felipimatheuz.primehunt.ui.viewmodel.primeset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.domain.usecase.primeset.GetPrimeSetsUseCase
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
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class PrimeSetViewModel @Inject constructor(
    getPrimeSetsUseCase: GetPrimeSetsUseCase,
    private val progressFilterUseCase: ProgressFilterUseCase
) : ViewModel(), MviViewModel<PrimeSetState, PrimeSetIntent> {

    private val _searchText = MutableStateFlow("")
    private val _filters = MutableStateFlow(PrimeSetFilters())
    private val _selectedView = MutableStateFlow(0)

    @OptIn(FlowPreview::class)
    override val state: StateFlow<PrimeSetState> = combine(
        getPrimeSetsUseCase.observeCollections().distinctUntilChanged(),
        getPrimeSetsUseCase.observeWithoutCollection().distinctUntilChanged(),
        _searchText.debounce(300.milliseconds).distinctUntilChanged(),
        _filters,
        _selectedView
    ) { collections, withoutCollection, query, filters, selectedView ->
        val allCollections = collections + withoutCollection

        val filteredCollections = allCollections.map { coll ->
            coll.copy(sets = applyFilters(coll.sets, query, filters))
        }.filter { it.sets.isNotEmpty() || (query.isEmpty() && filters == PrimeSetFilters()) }

        val allSets = allCollections.flatMap { it.sets }
        val filteredGrouped = applyFilters(allSets, query, filters)
            .groupBy { it.type }
            .filterValues { it.isNotEmpty() }

        PrimeSetState(
            collections = filteredCollections,
            groupedSets = filteredGrouped,
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
            initialValue = PrimeSetState()
        )

    private fun applyFilters(
        sets: List<PrimeSetDomain>,
        query: String,
        filters: PrimeSetFilters
    ): List<PrimeSetDomain> {
        return sets.filter { set ->
            val matchesQuery = set.name.contains(query, ignoreCase = true)

            val matchesProgress = progressFilterUseCase.matches(set, filters.progress)

            val matchesCategory =
                filters.categories.isEmpty() || filters.categories.contains(set.type)

            val matchesAvailability =
                filters.availabilities.isEmpty() || filters.availabilities.contains(set.availability)

            matchesQuery && matchesProgress && matchesCategory && matchesAvailability
        }
    }

    override fun onIntent(intent: PrimeSetIntent) {
        when (intent) {
            is PrimeSetIntent.Search -> _searchText.value = intent.query
            is PrimeSetIntent.ClearSearch -> _searchText.value = ""
            is PrimeSetIntent.UpdateFilters -> _filters.value = intent.filters
            is PrimeSetIntent.ChangeView -> _selectedView.value = intent.index
        }
    }
}
