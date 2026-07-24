package com.felipimatheuz.primehunt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.local.entity.TrackingEntity
import com.felipimatheuz.primehunt.data.local.enums.TrackingTargetType
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackingDao {

    @Query("""
        SELECT *
        FROM tracking
        ORDER BY priority ASC, createdAt ASC
    """)
    fun observeAll(): Flow<List<TrackingEntity>>

    @Query("""
        SELECT *
        FROM tracking
        WHERE categoryId = :categoryId
        ORDER BY priority ASC, createdAt ASC
    """)
    fun observeByCategory(
        categoryId: Long
    ): Flow<List<TrackingEntity>>

    @Query("""
        SELECT *
        FROM tracking
        WHERE targetType = :targetType
    """)
    fun observeByTargetType(
        targetType: TrackingTargetType
    ): Flow<List<TrackingEntity>>

    @Query("""
        SELECT *
        FROM tracking
        WHERE targetId = :targetId
    """)
    suspend fun getByTarget(
        targetId: String
    ): TrackingEntity?

    @Upsert
    suspend fun upsert(
        tracking: TrackingEntity
    )

    @Delete
    suspend fun delete(
        tracking: TrackingEntity
    )
}