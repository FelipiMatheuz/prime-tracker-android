package com.felipimatheuz.primehunt.domain.repository

import com.felipimatheuz.primehunt.domain.model.enums.DropRarity
import com.felipimatheuz.primehunt.domain.model.enums.PrimePartType
import com.felipimatheuz.primehunt.domain.model.enums.PrimeType

interface InventoryRepository {
    suspend fun getAllPartsSync(): List<SyncPart>
    suspend fun getAllSetsSync(): List<SyncSet>
    suspend fun getPartsBySetSync(setId: String): List<SyncPart>
    suspend fun getAllComponentsSync(): List<SyncComponent>
}

data class SyncPart(val id: String, val primeSetId: String, val part: PrimePartType, val quantity: Int)
data class SyncSet(val id: String, val name: String, val type: PrimeType, val image: String)
data class SyncComponent(val id: String, val relicId: String, val primePartId: String, val rarity: DropRarity)
