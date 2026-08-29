package com.felipimatheuz.primehunt.domain.repository

import com.felipimatheuz.primehunt.domain.model.PrimeCollection
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import kotlinx.coroutines.flow.Flow

interface PrimeRepository {
    fun observeAllSets(): Flow<List<PrimeSetDomain>>
    fun observeCollections(): Flow<List<PrimeCollection>>
    fun observeWithoutCollection(): Flow<PrimeCollection>
    fun observeSetById(id: String): Flow<PrimeSetDomain?>
    fun getDatabaseCounts(): Flow<DatabaseCounts>
}

data class DatabaseCounts(
    val collections: Int,
    val sets: Int,
    val parts: Int,
    val relics: Int
)
