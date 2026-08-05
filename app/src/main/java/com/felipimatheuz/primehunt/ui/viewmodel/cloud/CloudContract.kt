package com.felipimatheuz.primehunt.ui.viewmodel.cloud

import com.felipimatheuz.primehunt.ui.mvi.MviIntent
import com.felipimatheuz.primehunt.ui.mvi.MviState

data class CloudState(
    val isAuthenticated: Boolean = false,
    val userEmail: String? = null,
    val userName: String? = null,
    val isLoading: Boolean = false,
    val isMigrationSuccess: Boolean = false,
    val isUploading: Boolean = false,
    val isDownloading: Boolean = false,
    val actionResult: CloudActionResult = CloudActionResult.None
) : MviState

sealed class CloudActionResult {
    data object SuccessSignIn : CloudActionResult()
    data object SuccessSignOut : CloudActionResult()
    data object SuccessBackupUpload : CloudActionResult()
    data object SuccessBackupDownload : CloudActionResult()
    data object SuccessMigration : CloudActionResult()
    data object SuccessClearData : CloudActionResult()
    data class Error(val message: String) : CloudActionResult()
    data object None : CloudActionResult()
}

sealed interface CloudIntent : MviIntent {
    data object SignIn : CloudIntent
    data object SignOut : CloudIntent
    data object UploadBackup : CloudIntent
    data object DownloadBackup : CloudIntent
    data object StartMigration : CloudIntent
    data object SendLogs : CloudIntent
    data object ClearCloudData : CloudIntent
    data object ResetResult : CloudIntent
}

sealed interface CloudSideEffect {
    data class ShareLogFile(val logContent: String) : CloudSideEffect
}
