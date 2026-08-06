package com.felipimatheuz.primehunt.domain.repository

import com.felipimatheuz.primehunt.data.local.entity.LocalManifest
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import kotlinx.coroutines.flow.Flow

interface OverviewRepository {
    fun getDatabaseSummary(): Flow<DatabaseSummary>
    fun getRelicSummary(): Flow<RelicSummary>
    fun getGoalSummary(): Flow<GoalSummary>
    fun observeManifest(): Flow<LocalManifest?>
    fun observeGoalsWithTags(): Flow<List<GoalDomain>>
}

data class DatabaseSummary(
    val collections: Int,
    val sets: Int,
    val parts: Int,
    val relics: Int
)

data class RelicSummary(
    val available: Int,
    val vaulted: Int,
    val resurgence: Int,
    val baro: Int
)

data class GoalSummary(
    val active: Int,
    val completed: Int
)
