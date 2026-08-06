package com.felipimatheuz.primehunt.domain.repository

import com.felipimatheuz.primehunt.data.remote.enums.DropRarity
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import com.felipimatheuz.primehunt.domain.model.PrimeCollection
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.domain.model.RelicDomain
import kotlinx.coroutines.flow.Flow

interface PrimeRepository {
    // Domain Models
    fun observeAllSets(): Flow<List<PrimeSetDomain>>
    fun observeCollections(): Flow<List<PrimeCollection>>
    fun observeWithoutCollection(): Flow<PrimeCollection>
    fun observeAllRelics(): Flow<List<RelicDomain>>
    
    // Summary Data for Overview
    fun getDatabaseCounts(): Flow<DatabaseCounts>
    fun getRelicCounts(): Flow<RelicCounts>

    // Raw/Base Data (For Migration/Infrastructure use)
    suspend fun getAllPartsSync(): List<SyncPart>
    suspend fun getAllSetsSync(): List<SyncSet>
    suspend fun getPartsBySetSync(setId: String): List<SyncPart>
    suspend fun getAllComponentsSync(): List<SyncComponent>
}

data class SyncPart(val id: String, val primeSetId: String, val part: PrimePartType, val quantity: Int)
data class SyncSet(val id: String, val name: String, val type: PrimeType, val image: String)
data class SyncComponent(val id: String, val relicId: String, val primePartId: String, val rarity: DropRarity)

data class DatabaseCounts(
    val collections: Int,
    val sets: Int,
    val parts: Int,
    val relics: Int
)

data class RelicCounts(
    val available: Int,
    val vaulted: Int,
    val resurgence: Int,
    val baro: Int
)
