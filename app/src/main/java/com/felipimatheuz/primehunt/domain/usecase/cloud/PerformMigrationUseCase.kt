package com.felipimatheuz.primehunt.domain.usecase.cloud

import com.felipimatheuz.primehunt.domain.repository.CloudRepository
import com.felipimatheuz.primehunt.domain.repository.UiPreferencesRepository
import com.felipimatheuz.primehunt.domain.repository.CloudRemoteDataSource
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudActionResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PerformMigrationUseCase @Inject constructor(
    private val remoteDataSource: CloudRemoteDataSource,
    private val cloudRepository: CloudRepository,
    private val uiPreferencesRepository: UiPreferencesRepository
) {
    suspend operator fun invoke(userId: String): CloudActionResult {
        val data = when (val readResult = remoteDataSource.readLegacyChecklist(userId)) {
            is CloudRemoteDataSource.LegacyChecklistResult.Success -> readResult.data
            is CloudRemoteDataSource.LegacyChecklistResult.Error -> return CloudActionResult.Error(readResult.message)
        }

        @Suppress("UNCHECKED_CAST")
        val mapSet = data["SET"] as? Map<String, Any> ?: emptyMap()
        @Suppress("UNCHECKED_CAST")
        val mapOther = data["OTHER"] as? Map<String, Any> ?: emptyMap()

        val migrationResult = cloudRepository.performMigration(mapSet, mapOther)

        return if (migrationResult.itemsInserted > 0) {
            val deleted = remoteDataSource.deleteLegacyChecklist(userId)
            if (deleted) {
                val currentPrefs = uiPreferencesRepository.cloudPrefs.first()
                uiPreferencesRepository.updateCloudPrefs(currentPrefs.copy(isMigrationSuccess = true))
                CloudActionResult.SuccessMigration
            } else {
                CloudActionResult.Error("Failed to remove legacy data from cloud")
            }
        } else {
            if (migrationResult.ignoredKeys.isNotEmpty()) {
                CloudActionResult.Error("Migration ignored some items: ${migrationResult.ignoredKeys.joinToString(", ")}")
            } else {
                CloudActionResult.Error("No matching items found for migration")
            }
        }
    }
}
