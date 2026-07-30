package com.felipimatheuz.primehunt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.local.entity.GoalEntity
import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("""
        SELECT *
        FROM goal
        ORDER BY desiredQuantity ASC, createdAt ASC
    """)
    fun observeAll(): Flow<List<GoalEntity>>

    @Query("""
        SELECT *
        FROM goal
        WHERE categoryId = :categoryId
        ORDER BY desiredQuantity ASC, createdAt ASC
    """)
    fun observeByCategory(
        categoryId: Long
    ): Flow<List<GoalEntity>>

    @Query("""
        SELECT *
        FROM goal
        WHERE targetType = :targetType
    """)
    fun observeByTargetType(
        targetType: GoalTargetType
    ): Flow<List<GoalEntity>>

    @Query("""
        SELECT *
        FROM goal
        WHERE targetId = :targetId
    """)
    suspend fun getByTarget(
        targetId: String
    ): GoalEntity?

    @Upsert
    suspend fun upsert(
        goal: GoalEntity
    )

    @Delete
    suspend fun delete(
        goal: GoalEntity
    )
}