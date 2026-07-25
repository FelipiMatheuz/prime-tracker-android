package com.felipimatheuz.primehunt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.data.repository.PrimeRepository
import com.felipimatheuz.primehunt.domain.model.PrimeCollection
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.ui.mvi.MviIntent
import com.felipimatheuz.primehunt.ui.mvi.MviState
import com.felipimatheuz.primehunt.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

enum class ProgressFilter { ALL, COMPLETE, INCOMPLETE, NOT_STARTED }

data class PrimeSetFilters(
    val progress: ProgressFilter = ProgressFilter.ALL,
    val categories: Set<PrimeType> = emptySet(),
    val availabilities: Set<RelicSource> = emptySet()
)

data class PrimeSetState(
    val collections: List<PrimeCollection> = emptyList(),
    val groupedSets: Map<String, List<PrimeSetDomain>> = emptyMap(),
    val queryFilter: String = "",
    val activeFilters: PrimeSetFilters = PrimeSetFilters(),
    val isLoading: Boolean = true
) : MviState

sealed class PrimeSetIntent : MviIntent {
    data class Search(val query: String) : PrimeSetIntent()
    object ClearSearch : PrimeSetIntent()
    data class UpdateFilters(val filters: PrimeSetFilters) : PrimeSetIntent()
}

@HiltViewModel
class PrimeSetViewModel @Inject constructor(
    repository: PrimeRepository
) : ViewModel(), MviViewModel<PrimeSetState, PrimeSetIntent> {

    private val _searchText = MutableStateFlow("")
    private val _filters = MutableStateFlow(PrimeSetFilters())

    override val state: StateFlow<PrimeSetState> = combine(
        repository.observeCollections(),
        repository.observeWithoutCollection(),
        repository.observeSetsGroupedByCategory(),
        _searchText,
        _filters
    ) { collections, withoutCollection, groupedByCategory, query, filters ->
        
        val allCollections = collections + withoutCollection
        
        val filteredCollections = allCollections.map { coll ->
            coll.copy(sets = applyFilters(coll.sets, query, filters))
        }.filter { it.sets.isNotEmpty() || (query.isEmpty() && filters == PrimeSetFilters()) }

        val filteredGrouped = groupedByCategory.mapValues { (_, sets) ->
            applyFilters(sets, query, filters)
        }.filterValues { it.isNotEmpty() }.mapKeys { it.key.name }

        PrimeSetState(
            collections = filteredCollections,
            groupedSets = filteredGrouped,
            queryFilter = query,
            activeFilters = filters,
            isLoading = false
        )
    }.stateIn(
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
                ProgressFilter.NOT_STARTED -> set.ownedPieces == 0
            }
            
            val matchesCategory = filters.categories.isEmpty() || filters.categories.contains(set.type)
            
            val matchesAvailability = filters.availabilities.isEmpty() || filters.availabilities.contains(set.availability)
            
            matchesQuery && matchesProgress && matchesCategory && matchesAvailability
        }
    }

    override fun onIntent(intent: PrimeSetIntent) {
        when (intent) {
            is PrimeSetIntent.Search -> _searchText.value = intent.query
            is PrimeSetIntent.ClearSearch -> _searchText.value = ""
            is PrimeSetIntent.UpdateFilters -> _filters.value = intent.filters
        }
    }
}
