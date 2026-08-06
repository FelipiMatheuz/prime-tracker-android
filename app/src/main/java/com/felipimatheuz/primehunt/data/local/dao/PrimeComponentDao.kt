package com.felipimatheuz.primehunt.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.local.entity.PrimeComponentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrimeComponentDao {

    @Query("SELECT * FROM prime_component")
    fun getAll(): Flow<List<PrimeComponentEntity>>

    @Query("SELECT * FROM prime_component")
    suspend fun getAllSync(): List<PrimeComponentEntity>

    @Query("""
        SELECT *
        FROM prime_component
        WHERE relicId = :relicId
    """)
    fun getByRelic(relicId: String): Flow<List<PrimeComponentEntity>>

    @Query("""
        SELECT *
        FROM prime_component
        WHERE primePartId = :partId
    """)
    fun getByPrimePart(partId: String): Flow<List<PrimeComponentEntity>>

    @Query("""
        SELECT *
        FROM prime_component
        WHERE primePartId = :partId
    """)
    suspend fun getByPrimePartSync(partId: String): List<PrimeComponentEntity>

    @Upsert
    suspend fun upsertAll(components: List<PrimeComponentEntity>)
}