package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.preferences.UiPreferencesDataSource
import com.felipimatheuz.primehunt.domain.model.prefs.*
import com.felipimatheuz.primehunt.domain.repository.UiPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiPreferencesRepositoryImpl @Inject constructor(
    private val dataSource: UiPreferencesDataSource
) : UiPreferencesRepository {
    override val primeSetPrefs: Flow<PrimeSetUiPrefs> = dataSource.primeSetPrefs
    override val relicPrefs: Flow<RelicUiPrefs> = dataSource.relicPrefs
    override val goalPrefs: Flow<GoalUiPrefs> = dataSource.goalPrefs
    override val cloudPrefs: Flow<CloudUiPrefs> = dataSource.cloudPrefs

    override suspend fun updatePrimeSetPrefs(prefs: PrimeSetUiPrefs) {
        dataSource.updatePrimeSetPrefs(prefs)
    }

    override suspend fun updateRelicPrefs(prefs: RelicUiPrefs) {
        dataSource.updateRelicPrefs(prefs)
    }

    override suspend fun updateGoalPrefs(prefs: GoalUiPrefs) {
        dataSource.updateGoalPrefs(prefs)
    }

    override suspend fun updateCloudPrefs(prefs: CloudUiPrefs) {
        dataSource.updateCloudPrefs(prefs)
    }
}
