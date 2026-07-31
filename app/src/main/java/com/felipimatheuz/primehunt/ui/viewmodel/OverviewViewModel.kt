package com.felipimatheuz.primehunt.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.domain.usecase.primeset.GetPrimeSetsUseCase
import com.felipimatheuz.primehunt.model.ItemComponent
import com.felipimatheuz.primehunt.model.ItemPart
import com.felipimatheuz.primehunt.model.PrimeItem
import com.felipimatheuz.primehunt.model.PrimeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val getPrimeSetsUseCase: GetPrimeSetsUseCase
) : ViewModel() {
    
    suspend fun loadSet(update: Boolean): List<PrimeItem> {
        // Using first() as identified in performance analysis to avoid infinite suspension
        val primeData = getPrimeSetsUseCase.observeAllSets().first()
        return primeData.map { it.toLegacyPrimeItem() }
    }

    fun loadOther(update: Boolean): List<PrimeItem> {
        // Since we are refactoring, we should eventually move 'other' data to UseCase too.
        // For now, returning empty or migrating it if possible.
        // Actually, the new UseCase includes all sets from PrimeDataStore.
        return emptyList() 
    }

    private fun PrimeSetDomain.toLegacyPrimeItem(): PrimeItem {
        val legacyType = when (this.type) {
            com.felipimatheuz.primehunt.data.remote.enums.PrimeType.WARFRAME -> PrimeType.WARFRAME
            com.felipimatheuz.primehunt.data.remote.enums.PrimeType.PRIMARY -> PrimeType.PRIMARY
            com.felipimatheuz.primehunt.data.remote.enums.PrimeType.SECONDARY -> PrimeType.SECONDARY
            com.felipimatheuz.primehunt.data.remote.enums.PrimeType.MELEE -> PrimeType.MELEE
            else -> PrimeType.OTHER
        }

        return PrimeItem(
            name = this.name,
            type = legacyType,
            components = this.parts.map { part ->
                ItemComponent(
                    part = ItemPart.entries.find { it.name == part.name.name } ?: ItemPart.NEUROPTICS,
                    obtained = part.ownedQuantity >= part.neededQuantity
                )
            },
            blueprint = this.ownedPieces >= this.totalPieces // Simplified for legacy chart
        )
    }
}
