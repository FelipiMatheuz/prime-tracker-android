package com.felipimatheuz.primehunt.domain.usecase.cloud

import com.felipimatheuz.primehunt.domain.repository.CloudRemoteDataSource
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudActionResult
import javax.inject.Inject

class ClearCloudDataUseCase @Inject constructor(
    private val remoteDataSource: CloudRemoteDataSource
) {
    suspend operator fun invoke(userId: String): CloudActionResult {
        val success = remoteDataSource.deleteBackup(userId)
        return if (success) CloudActionResult.SuccessClearData else CloudActionResult.Error("Failed to delete cloud data")
    }
}
