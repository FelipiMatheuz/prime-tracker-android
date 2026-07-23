package com.felipimatheuz.primehunt.data.remote.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.remote.entity.PrimeComponentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrimeComponentDao {

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

    @Upsert
    suspend fun upsertAll(components: List<PrimeComponentEntity>)
}