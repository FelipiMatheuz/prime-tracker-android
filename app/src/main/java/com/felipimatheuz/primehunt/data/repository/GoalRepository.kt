package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.GoalTagDao
import com.felipimatheuz.primehunt.data.local.entity.GoalEntity
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao,
    private val goalTagDao: GoalTagDao
) {

    suspend fun deleteGoal(id: Long) {
        val goal = goalDao.getById(id)
        goal?.let { goalDao.delete(it) }
    }

    suspend fun saveGoal(goal: GoalEntity) {
        goalDao.upsert(goal)
    }

    suspend fun completeGoal(id: Long) {
        goalDao.updateStatus(id, GoalStatus.COMPLETED, System.currentTimeMillis())
    }

    suspend fun saveTag(tag: GoalTagEntity) {
        goalTagDao.upsert(tag)
    }
}
