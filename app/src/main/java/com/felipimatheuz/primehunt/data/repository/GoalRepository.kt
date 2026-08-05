package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.GoalTagDao
import com.felipimatheuz.primehunt.data.local.entity.GoalEntity
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.domain.repository.GoalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    private val goalTagDao: GoalTagDao
) : GoalRepository {

    override suspend fun deleteGoal(id: Long) {
        val goal = goalDao.getById(id)
        goal?.let { goalDao.delete(it) }
    }

    override suspend fun saveGoal(goal: GoalEntity) {
        goalDao.upsert(goal)
    }

    override suspend fun completeGoal(id: Long) {
        goalDao.updateStatus(id, GoalStatus.COMPLETED, System.currentTimeMillis())
    }

    override suspend fun saveTag(tag: GoalTagEntity) {
        goalTagDao.upsert(tag)
    }
}
