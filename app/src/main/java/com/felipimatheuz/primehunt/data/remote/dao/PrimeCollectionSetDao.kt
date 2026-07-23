package com.felipimatheuz.primehunt.data.remote.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.remote.entity.PrimeCollectionSetCrossRef

@Dao
interface PrimeCollectionSetDao {

    @Upsert
    suspend fun upsertAll(relations: List<PrimeCollectionSetCrossRef>)
}