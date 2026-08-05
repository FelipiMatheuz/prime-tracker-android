package com.felipimatheuz.primehunt.domain.repository

import com.felipimatheuz.primehunt.data.local.entity.GoalEntity
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity

interface GoalRepository {
    suspend fun deleteGoal(id: Long)
    suspend fun saveGoal(goal: GoalEntity)
    suspend fun completeGoal(id: Long)
    suspend fun saveTag(tag: GoalTagEntity)
}
