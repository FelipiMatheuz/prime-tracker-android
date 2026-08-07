package com.felipimatheuz.primehunt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.local.entity.GoalEntity
import com.felipimatheuz.primehunt.data.local.entity.GoalWithTag
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

    @androidx.room.Transaction
    @Query("SELECT * FROM goal")
    fun observeAllWithTags(): Flow<List<GoalWithTag>>

    @androidx.room.Transaction
    @Query("SELECT * FROM goal WHERE id = :id")
    fun observeByIdWithTag(id: Long): Flow<GoalWithTag?>

    @Query("""
        SELECT *
        FROM goal
        WHERE tagId = :tagId
        ORDER BY desiredQuantity ASC, createdAt ASC
    """)
    fun observeByTag(
        tagId: Long
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

    @Query("SELECT * FROM goal WHERE id = :id")
    suspend fun getById(id: Long): GoalEntity?

    @Query("UPDATE goal SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: com.felipimatheuz.primehunt.data.local.enums.GoalStatus, completedAt: Long?)

    @Query("SELECT COUNT(*) FROM goal WHERE status = :status")
    fun countByStatus(status: com.felipimatheuz.primehunt.data.local.enums.GoalStatus): Flow<Int>

    @Upsert
    suspend fun upsert(
        goal: GoalEntity
    )

    @Upsert
    suspend fun upsert(
        goals: List<GoalEntity>
    )

    @Delete
    suspend fun delete(
        goal: GoalEntity
    )

    @Query("DELETE FROM goal")
    suspend fun clearAll()
}