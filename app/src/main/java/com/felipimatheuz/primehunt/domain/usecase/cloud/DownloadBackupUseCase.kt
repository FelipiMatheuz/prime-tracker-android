package com.felipimatheuz.primehunt.domain.usecase.cloud

import com.felipimatheuz.primehunt.data.repository.CloudRepository
import com.felipimatheuz.primehunt.data.cloud.Firestore
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudActionResult
import javax.inject.Inject

class DownloadBackupUseCase @Inject constructor(
    private val firestore: Firestore,
    private val cloudRepository: CloudRepository
) {
    suspend operator fun invoke(userId: String): CloudActionResult {
        return when (val result = firestore.downloadBackup(userId)) {
            is Firestore.BackupResult.Success -> {
                try {
                    cloudRepository.restoreBackup(result.inventory, result.goals, result.tags)
                    CloudActionResult.SuccessBackupDownload
                } catch (e: Exception) {
                    CloudActionResult.Error(e.message ?: "Restore failed")
                }
            }
            is Firestore.BackupResult.Error -> {
                CloudActionResult.Error(result.message)
            }
        }
    }
}
