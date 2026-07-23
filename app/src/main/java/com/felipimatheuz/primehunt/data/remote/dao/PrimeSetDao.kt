package com.felipimatheuz.primehunt.data.remote.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.remote.entity.PrimeSetEntity
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import kotlinx.coroutines.flow.Flow

@Dao
interface PrimeSetDao {

    @Query("""
        SELECT *
        FROM prime_set
        ORDER BY name
    """)
    fun getAll(): Flow<List<PrimeSetEntity>>

    @Query("""
        SELECT *
        FROM prime_set
        WHERE id = :id
    """)
    suspend fun getById(id: String): PrimeSetEntity?

    @Query("""
        SELECT *
        FROM prime_set
        WHERE type = :type
        ORDER BY name
    """)
    fun getByType(type: PrimeType): Flow<List<PrimeSetEntity>>

    @Query("""
        SELECT *
        FROM prime_set
        WHERE name LIKE '%' || :query || '%'
        ORDER BY name
    """)
    fun search(query: String): Flow<List<PrimeSetEntity>>

    @Upsert
    suspend fun upsertAll(items: List<PrimeSetEntity>)
}