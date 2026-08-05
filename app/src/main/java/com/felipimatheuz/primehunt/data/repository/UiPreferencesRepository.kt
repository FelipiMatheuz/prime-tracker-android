package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.preferences.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiPreferencesRepository @Inject constructor(
    private val dataSource: UiPreferencesDataSource
) {
    val primeSetPrefs: Flow<PrimeSetUiPrefs> = dataSource.primeSetPrefs
    val relicPrefs: Flow<RelicUiPrefs> = dataSource.relicPrefs
    val goalPrefs: Flow<GoalUiPrefs> = dataSource.goalPrefs
    val cloudPrefs: Flow<CloudUiPrefs> = dataSource.cloudPrefs

    suspend fun updatePrimeSetPrefs(prefs: PrimeSetUiPrefs) {
        dataSource.updatePrimeSetPrefs(prefs)
    }

    suspend fun updateRelicPrefs(prefs: RelicUiPrefs) {
        dataSource.updateRelicPrefs(prefs)
    }

    suspend fun updateGoalPrefs(prefs: GoalUiPrefs) {
        dataSource.updateGoalPrefs(prefs)
    }

    suspend fun updateCloudPrefs(prefs: CloudUiPrefs) {
        dataSource.updateCloudPrefs(prefs)
    }
}
