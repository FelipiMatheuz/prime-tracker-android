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
        val result = firestore.downloadBackup(userId)
        return when (result) {
            is Firestore.BackupResult.Success -> {
                try {
                    cloudRepository.restoreInventory(result.inventory)
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
