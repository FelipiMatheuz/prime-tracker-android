package com.felipimatheuz.primehunt.domain.repository

import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.domain.model.enums.GoalStatus
import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    suspend fun deleteGoal(id: Long)
    suspend fun saveGoal(
        targetId: String,
        targetType: GoalTargetType,
        desiredQuantity: Int,
        tagId: Long,
        note: String?,
        manualCurrentQuantity: Int? = null
    )
    suspend fun updateGoal(
        id: Long,
        desiredQuantity: Int,
        tagId: Long,
        note: String?,
        manualCurrentQuantity: Int? = null
    )
    suspend fun completeGoal(id: Long)
    suspend fun saveTag(tag: GoalTagDomain)
    fun observeAllWithTags(): Flow<List<GoalDomain>>
    fun observeByIdWithTag(id: Long): Flow<GoalDomain?>
    fun observeAllTags(): Flow<List<GoalTagDomain>>
    fun countByStatus(status: GoalStatus): Flow<Int>
}
