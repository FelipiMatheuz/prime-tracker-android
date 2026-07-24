package com.felipimatheuz.primehunt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.local.entity.TrackingCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackingCategoryDao {

    @Query(
        """
        SELECT *
        FROM tracking_category
        ORDER BY name
    """
    )
    fun observeAll(): Flow<List<TrackingCategoryEntity>>

    @Query(
        """
        SELECT *
        FROM tracking_category
        WHERE id = :id
    """
    )
    suspend fun get(id: Long): TrackingCategoryEntity?

    @Upsert
    suspend fun upsert(category: TrackingCategoryEntity)

    @Upsert
    suspend fun upsert(categories: List<TrackingCategoryEntity>)

    @Delete
    suspend fun delete(category: TrackingCategoryEntity)
}