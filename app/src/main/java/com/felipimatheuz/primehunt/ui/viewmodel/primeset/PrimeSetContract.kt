package com.felipimatheuz.primehunt.ui.viewmodel.primeset

import com.felipimatheuz.primehunt.domain.model.enums.ProgressFilter
import com.felipimatheuz.primehunt.domain.model.enums.PrimeType
import com.felipimatheuz.primehunt.domain.model.enums.RelicSource
import com.felipimatheuz.primehunt.domain.model.PrimeCollection
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.ui.mvi.MviIntent
import com.felipimatheuz.primehunt.ui.mvi.MviState

data class PrimeSetFilters(
    val progress: ProgressFilter = ProgressFilter.ALL,
    val categories: Set<PrimeType> = emptySet(),
    val availabilities: Set<RelicSource> = emptySet()
) {
    val activeCount: Int
        get() {
            var count = 0
            if (progress != ProgressFilter.ALL) count++
            count += categories.size
            count += availabilities.size
            return count
        }
}

data class PrimeSetState(
    val collections: List<PrimeCollection> = emptyList(),
    val groupedSets: Map<PrimeType, List<PrimeSetDomain>> = emptyMap(),
    val queryFilter: String = "",
    val activeFilters: PrimeSetFilters = PrimeSetFilters(),
    val selectedView: Int = 0,
    val isLoading: Boolean = true
) : MviState

sealed class PrimeSetIntent : MviIntent {
    data class Search(val query: String) : PrimeSetIntent()
    object ClearSearch : PrimeSetIntent()
    data class UpdateFilters(val filters: PrimeSetFilters) : PrimeSetIntent()
    data class ChangeView(val index: Int) : PrimeSetIntent()
}
