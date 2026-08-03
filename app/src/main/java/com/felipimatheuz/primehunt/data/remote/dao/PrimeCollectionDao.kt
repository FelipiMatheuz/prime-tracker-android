package com.felipimatheuz.primehunt.data.remote.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.remote.entity.PrimeCollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrimeCollectionDao {

    @Query("SELECT * FROM prime_collection ORDER BY released DESC")
    fun getAll(): Flow<List<PrimeCollectionEntity>>

    @Query("SELECT * FROM prime_collection WHERE id = :id")
    suspend fun getById(id: String): PrimeCollectionEntity?

    @Query("SELECT COUNT(*) FROM prime_collection")
    fun count(): Flow<Int>

    @Upsert
    suspend fun upsertAll(collections: List<PrimeCollectionEntity>)
}