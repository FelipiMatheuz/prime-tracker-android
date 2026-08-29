package com.felipimatheuz.primehunt.domain.usecase.cloud

import com.felipimatheuz.primehunt.domain.repository.CloudRepository
import com.felipimatheuz.primehunt.domain.repository.CloudRemoteDataSource
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudActionResult
import javax.inject.Inject

class DownloadBackupUseCase @Inject constructor(
    private val remoteDataSource: CloudRemoteDataSource,
    private val cloudRepository: CloudRepository
) {
    suspend operator fun invoke(userId: String): CloudActionResult {
        val data = remoteDataSource.downloadBackup(userId)
            ?: return CloudActionResult.Error("No backup found or error occurred")
        
        return try {
            @Suppress("UNCHECKED_CAST")
            val inventory = data["inventory"] as? Map<String, Int> ?: emptyMap()
            @Suppress("UNCHECKED_CAST")
            val goals = data["goals"] as? List<Map<String, Any?>> ?: emptyList()
            @Suppress("UNCHECKED_CAST")
            val tags = data["tags"] as? List<Map<String, Any?>> ?: emptyList()
            
            cloudRepository.restoreBackup(inventory, goals, tags)
            CloudActionResult.SuccessBackupDownload
        } catch (e: Exception) {
            CloudActionResult.Error(e.message ?: "Restore failed")
        }
    }
}
