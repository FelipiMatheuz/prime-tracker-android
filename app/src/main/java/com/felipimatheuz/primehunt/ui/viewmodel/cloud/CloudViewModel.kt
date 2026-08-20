package com.felipimatheuz.primehunt.ui.viewmodel.cloud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.core.logging.AppLogger
import com.felipimatheuz.primehunt.domain.repository.UiPreferencesRepository
import com.felipimatheuz.primehunt.domain.usecase.cloud.ClearCloudDataUseCase
import com.felipimatheuz.primehunt.domain.usecase.cloud.DownloadBackupUseCase
import com.felipimatheuz.primehunt.domain.usecase.cloud.PerformMigrationUseCase
import com.felipimatheuz.primehunt.domain.usecase.cloud.UploadBackupUseCase
import com.felipimatheuz.primehunt.domain.model.SignInResult
import com.felipimatheuz.primehunt.data.cloud.GoogleCredential
import com.felipimatheuz.primehunt.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CloudViewModel @Inject constructor(
    private val googleCredential: GoogleCredential,
    private val uiPreferencesRepository: UiPreferencesRepository,
    private val uploadBackupUseCase: UploadBackupUseCase,
    private val downloadBackupUseCase: DownloadBackupUseCase,
    private val performMigrationUseCase: PerformMigrationUseCase,
    private val clearCloudDataUseCase: ClearCloudDataUseCase,
    private val logger: AppLogger
) : ViewModel(), MviViewModel<CloudState, CloudIntent> {

    private val _state = MutableStateFlow(CloudState())
    override val state: StateFlow<CloudState> = _state.asStateFlow()

    private val _sideEffect = Channel<CloudSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        logger.log("CloudViewModel", "Initializing")
        updateUserInfo()
        observeMigrationStatus()
    }

    private fun updateUserInfo() {
        val user = googleCredential.getSignedInUser()
        _state.update {
            it.copy(
                isAuthenticated = user != null,
                userId = user?.userId,
                userEmail = user?.email,
                userName = user?.name
            )
        }
    }

    private fun observeMigrationStatus() {
        viewModelScope.launch {
            uiPreferencesRepository.cloudPrefs.collect { prefs ->
                _state.update { it.copy(isMigrationSuccess = prefs.isMigrationSuccess) }
            }
        }
    }

    override fun onIntent(intent: CloudIntent) {
        if (!_state.value.isAuthenticated && isProtectedIntent(intent)) return

        when (intent) {
            is CloudIntent.SignIn -> signIn()
            is CloudIntent.SignOut -> signOut()
            is CloudIntent.UploadBackup -> uploadBackup()
            is CloudIntent.DownloadBackup -> downloadBackup()
            is CloudIntent.StartMigration -> startMigration()
            is CloudIntent.SendLogs -> sendLogs()
            is CloudIntent.ClearCloudData -> clearCloudData()
            is CloudIntent.ResetResult -> resetResult()
        }
    }

    private fun isProtectedIntent(intent: CloudIntent): Boolean {
        return intent !is CloudIntent.SignIn && intent !is CloudIntent.ResetResult && intent !is CloudIntent.SendLogs
    }

    private fun signIn() {
        logger.log("CloudViewModel", "Intent: SignIn")
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = googleCredential.signUpCredentialManager()
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: SignInResult) {
        val user = result.data
        if (user != null) {
            logger.log("CloudViewModel", "SignIn Success: ${user.email}")
        } else {
            logger.log("CloudViewModel", "SignIn Error: ${result.errorMessage}")
        }
        _state.update {
            it.copy(
                isLoading = false,
                isAuthenticated = user != null,
                userId = user?.userId,
                userEmail = user?.email,
                userName = user?.name,
                actionResult = if (user != null) CloudActionResult.SuccessSignIn 
                              else CloudActionResult.Error(result.errorMessage ?: "")
            )
        }
        scheduleReset()
    }

    private fun signOut() {
        logger.log("CloudViewModel", "Intent: SignOut")
        if (_state.value.isUploading || _state.value.isDownloading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            googleCredential.signOut()
            _state.update {
                it.copy(
                    isLoading = false,
                    isAuthenticated = false,
                    userId = null,
                    userEmail = null,
                    userName = null,
                    actionResult = CloudActionResult.SuccessSignOut
                )
            }
            logger.log("CloudViewModel", "SignOut Success")
            scheduleReset()
        }
    }

    private fun uploadBackup() {
        logger.log("CloudViewModel", "Intent: UploadBackup")
        if (_state.value.isDownloading || _state.value.isLoading) return
        val userId = _state.value.userId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isUploading = true) }
            val result = uploadBackupUseCase(userId)
            logger.log("CloudViewModel", "UploadBackup Result: $result")
            _state.update { it.copy(isUploading = false, actionResult = result) }
            scheduleReset()
        }
    }

    private fun downloadBackup() {
        logger.log("CloudViewModel", "Intent: DownloadBackup")
        if (_state.value.isUploading || _state.value.isLoading) return
        val userId = _state.value.userId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDownloading = true) }
            val result = downloadBackupUseCase(userId)
            logger.log("CloudViewModel", "DownloadBackup Result: $result")
            _state.update {
                it.copy(
                    isDownloading = false,
                    actionResult = result
                )
            }
            scheduleReset()
        }
    }

    private fun startMigration() {
        logger.log("CloudViewModel", "Intent: StartMigration")
        if (_state.value.isMigrationSuccess) return
        val userId = _state.value.userId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = performMigrationUseCase(userId)
            logger.log("CloudViewModel", "Migration Result: $result")
            _state.update { it.copy(isLoading = false, actionResult = result) }
            scheduleReset()
        }
    }

    private fun sendLogs() {
        logger.log("CloudViewModel", "Intent: SendLogs")
        viewModelScope.launch {
            val logs = logger.getLogs()
            _sideEffect.send(CloudSideEffect.ShareLogFile(logs))
        }
    }

    private fun clearCloudData() {
        logger.log("CloudViewModel", "Intent: ClearCloudData")
        val userId = _state.value.userId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = clearCloudDataUseCase(userId)
            logger.log("CloudViewModel", "ClearCloudData Result: $result")
            _state.update { it.copy(isLoading = false, actionResult = result) }
            scheduleReset()
        }
    }

    private fun resetResult() {
        _state.update {
            it.copy(actionResult = CloudActionResult.None)
        }
    }

    private fun scheduleReset() {
        viewModelScope.launch {
            delay(3.seconds)
            resetResult()
        }
    }
}
