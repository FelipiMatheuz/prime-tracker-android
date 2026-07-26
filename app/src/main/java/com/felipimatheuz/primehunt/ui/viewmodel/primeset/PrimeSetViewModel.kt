package com.felipimatheuz.primehunt.ui.viewmodel.primeset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.data.repository.PrimeRepository
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PrimeSetViewModel @Inject constructor(
    repository: PrimeRepository
) : ViewModel(), MviViewModel<PrimeSetState, PrimeSetIntent> {

    private val _searchText = MutableStateFlow("")
    private val _filters = MutableStateFlow(PrimeSetFilters())
    private val _selectedView = MutableStateFlow(0)

    override val state: StateFlow<PrimeSetState> = combine(
        combine(
            repository.observeCollections().distinctUntilChanged(),
            repository.observeWithoutCollection().distinctUntilChanged(),
            repository.observeSetsGroupedByCategory().distinctUntilChanged(),
            ::Triple
        ),
        _searchText,
        _filters,
        _selectedView
    ) { repoData, query, filters, selectedView ->
        val (collections, withoutCollection, groupedByCategory) = repoData

        val allCollections = collections + withoutCollection

        val filteredCollections = allCollections.map { coll ->
            coll.copy(sets = applyFilters(coll.sets, query, filters))
        }.filter { it.sets.isNotEmpty() || (query.isEmpty() && filters == PrimeSetFilters()) }

        val filteredGrouped = groupedByCategory.mapValues { (_, sets) ->
            applyFilters(sets, query, filters)
        }.filterValues { it.isNotEmpty() }

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

            val matchesProgress = when (filters.progress) {
                ProgressFilter.ALL -> true
                ProgressFilter.COMPLETE -> set.ownedPieces == set.totalPieces && set.totalPieces > 0
                ProgressFilter.INCOMPLETE -> set.ownedPieces < set.totalPieces && set.ownedPieces > 0
                ProgressFilter.IN_PROGRESS -> set.ownedPieces < set.totalPieces && set.ownedPieces > 0
                ProgressFilter.NOT_STARTED -> set.ownedPieces == 0
            }

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
