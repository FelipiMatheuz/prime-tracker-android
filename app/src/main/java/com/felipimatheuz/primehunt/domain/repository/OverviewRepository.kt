package com.felipimatheuz.primehunt.domain.repository

import com.felipimatheuz.primehunt.data.local.entity.GoalWithTag
import com.felipimatheuz.primehunt.data.remote.entity.LocalManifest
import com.felipimatheuz.primehunt.data.repository.DatabaseSummary
import com.felipimatheuz.primehunt.data.repository.GoalSummary
import com.felipimatheuz.primehunt.data.repository.RelicSummary
import kotlinx.coroutines.flow.Flow

interface OverviewRepository {
    fun getDatabaseSummary(): Flow<DatabaseSummary>
    fun getRelicSummary(): Flow<RelicSummary>
    fun getGoalSummary(): Flow<GoalSummary>
    fun observeManifest(): Flow<LocalManifest?>
    fun observeGoalsWithTags(): Flow<List<GoalWithTag>>
}
