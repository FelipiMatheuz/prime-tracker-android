package com.felipimatheuz.primehunt.data.remote.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.remote.entity.PrimeCollectionSetCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface PrimeCollectionSetDao {

    @Query("SELECT * FROM prime_collection_set")
    fun getAll(): Flow<List<PrimeCollectionSetCrossRef>>

    @Upsert
    suspend fun upsertAll(relations: List<PrimeCollectionSetCrossRef>)
}