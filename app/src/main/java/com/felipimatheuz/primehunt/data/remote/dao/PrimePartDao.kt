package com.felipimatheuz.primehunt.data.remote.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.remote.entity.PrimePartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrimePartDao {

    @Query("""
        SELECT *
        FROM prime_part
        WHERE primeSetId = :setId
    """)
    fun getByPrimeSet(setId: String): Flow<List<PrimePartEntity>>

    @Query("""
        SELECT *
        FROM prime_part
        WHERE id = :id
    """)
    suspend fun getById(id: String): PrimePartEntity?

    @Query("""
        SELECT *
        FROM prime_part
        WHERE primeSetId = :setId
    """)
    suspend fun getByPrimeSetSync(setId: String): List<PrimePartEntity>

    @Upsert
    suspend fun upsertAll(parts: List<PrimePartEntity>)
}