package com.felipimatheuz.primehunt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalTagDao {

    @Query(
        """
        SELECT *
        FROM goal_tag
        ORDER BY name
    """
    )
    fun observeAll(): Flow<List<GoalTagEntity>>

    @Query(
        """
        SELECT *
        FROM goal_tag
        WHERE id = :id
    """
    )
    suspend fun get(id: Long): GoalTagEntity?

    @Upsert
    suspend fun upsert(category: GoalTagEntity)

    @Upsert
    suspend fun upsert(categories: List<GoalTagEntity>)

    @Delete
    suspend fun delete(category: GoalTagEntity)

    @Query("DELETE FROM goal_tag")
    suspend fun clearAll()
}