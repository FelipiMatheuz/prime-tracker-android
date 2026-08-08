package com.felipimatheuz.primehunt.domain.repository

import com.felipimatheuz.primehunt.domain.model.prefs.*
import kotlinx.coroutines.flow.Flow

interface UiPreferencesRepository {
    val primeSetPrefs: Flow<PrimeSetUiPrefs>
    val relicPrefs: Flow<RelicUiPrefs>
    val goalPrefs: Flow<GoalUiPrefs>
    val cloudPrefs: Flow<CloudUiPrefs>

    suspend fun updatePrimeSetPrefs(prefs: PrimeSetUiPrefs)
    suspend fun updateRelicPrefs(prefs: RelicUiPrefs)
    suspend fun updateGoalPrefs(prefs: GoalUiPrefs)
    suspend fun updateCloudPrefs(prefs: CloudUiPrefs)
}
