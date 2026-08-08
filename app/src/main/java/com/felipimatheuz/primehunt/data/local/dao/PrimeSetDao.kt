package com.felipimatheuz.primehunt.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.local.entity.PrimeSetEntity
import com.felipimatheuz.primehunt.domain.model.enums.PrimeType
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
    fun observeById(id: String): Flow<PrimeSetEntity?>

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

    @Query("""
        SELECT *
        FROM prime_set
        WHERE id IN (SELECT primeSetId FROM prime_collection_set WHERE collectionId = :collectionId)
        ORDER BY name
    """)
    fun getByCollection(collectionId: String): Flow<List<PrimeSetEntity>>

    @Query("""
        SELECT *
        FROM prime_set
        WHERE id NOT IN (SELECT primeSetId FROM prime_collection_set)
        ORDER BY name
    """)
    fun getWithoutCollection(): Flow<List<PrimeSetEntity>>

    @Query("""
        SELECT *
        FROM prime_set
        WHERE id = :id
    """)
    suspend fun getByIdSync(id: String): PrimeSetEntity?

    @Query("SELECT * FROM prime_set")
    suspend fun getAllSync(): List<PrimeSetEntity>

    @Query("SELECT COUNT(*) FROM prime_set")
    fun count(): Flow<Int>

    @Upsert
    suspend fun upsertAll(items: List<PrimeSetEntity>)
}