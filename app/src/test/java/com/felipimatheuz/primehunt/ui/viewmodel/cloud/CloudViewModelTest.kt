package com.felipimatheuz.primehunt.ui.viewmodel.cloud

import com.felipimatheuz.primehunt.core.logging.AppLogger
import com.felipimatheuz.primehunt.domain.repository.UiPreferencesRepository
import com.felipimatheuz.primehunt.domain.model.UserData
import com.felipimatheuz.primehunt.data.cloud.GoogleCredential
import com.felipimatheuz.primehunt.domain.model.prefs.CloudUiPrefs
import com.felipimatheuz.primehunt.domain.usecase.cloud.ClearCloudDataUseCase
import com.felipimatheuz.primehunt.domain.usecase.cloud.DownloadBackupUseCase
import com.felipimatheuz.primehunt.domain.usecase.cloud.PerformMigrationUseCase
import com.felipimatheuz.primehunt.domain.usecase.cloud.UploadBackupUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class CloudViewModelTest {

    private val googleCredential: GoogleCredential = mockk(relaxed = true)
    private val uiPreferencesRepository: UiPreferencesRepository = mockk(relaxed = true)
    private val uploadBackupUseCase: UploadBackupUseCase = mockk(relaxed = true)
    private val downloadBackupUseCase: DownloadBackupUseCase = mockk(relaxed = true)
    private val performMigrationUseCase: PerformMigrationUseCase = mockk(relaxed = true)
    private val clearCloudDataUseCase: ClearCloudDataUseCase = mockk(relaxed = true)
    private val logger: AppLogger = mockk(relaxed = true)
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CloudViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { uiPreferencesRepository.cloudPrefs } returns flowOf(CloudUiPrefs(false))
    }

    private fun createViewModel(user: UserData? = null) {
        every { googleCredential.getSignedInUser() } returns user
        viewModel = CloudViewModel(
            googleCredential, uiPreferencesRepository,
            uploadBackupUseCase, downloadBackupUseCase,
            performMigrationUseCase, clearCloudDataUseCase, logger
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `when not authenticated, protected intents should be ignored`() = runTest {
        createViewModel(user = null)
        
        viewModel.onIntent(CloudIntent.UploadBackup)
        runCurrent()
        
        coVerify(exactly = 0) { uploadBackupUseCase(any()) }
    }

    @Test
    fun `UploadBackup should work when authenticated`() = runTest {
        createViewModel(user = UserData("user123", "User", "user123@email.com"))
        
        viewModel.onIntent(CloudIntent.UploadBackup)
        runCurrent()
        
        coVerify { uploadBackupUseCase("user123") }
    }

    @Test
    fun `Mutual exclusion between Upload and Download`() = runTest {
        createViewModel(user = UserData("user123", "User", "user123@email.com"))
        
        coEvery { uploadBackupUseCase(any()) } coAnswers {
            kotlinx.coroutines.delay(1.seconds)
            CloudActionResult.SuccessBackupUpload
        }
        
        viewModel.onIntent(CloudIntent.UploadBackup)
        runCurrent()
        
        assertTrue("isUploading deve ser true", viewModel.state.value.isUploading)
        
        viewModel.onIntent(CloudIntent.DownloadBackup)
        runCurrent()
        
        assertFalse("isDownloading deve ser false (bloqueado)", viewModel.state.value.isDownloading)
        coVerify(exactly = 0) { downloadBackupUseCase(any()) }
    }

    @Test
    fun `Legacy Migration - success scenario`() = runTest {
        createViewModel(user = UserData("user123", "User", "user123@email.com"))
        
        coEvery { performMigrationUseCase("user123") } returns CloudActionResult.SuccessMigration
        
        viewModel.onIntent(CloudIntent.StartMigration)
        runCurrent()
        
        coVerify { performMigrationUseCase("user123") }
        assertEquals(CloudActionResult.SuccessMigration, viewModel.state.value.actionResult)
    }

    @Test
    fun `Legacy Migration - failure scenario`() = runTest {
        createViewModel(user = UserData("user123", "User", "user123@email.com"))
        
        val errorResult = CloudActionResult.Error("No items found")
        coEvery { performMigrationUseCase("user123") } returns errorResult
        
        viewModel.onIntent(CloudIntent.StartMigration)
        runCurrent()
        
        assertEquals(errorResult, viewModel.state.value.actionResult)
    }

    @Test
    fun `Clear Cloud Data - success scenario`() = runTest {
        createViewModel(user = UserData("user123", "User", "user123@email.com"))
        
        coEvery { clearCloudDataUseCase("user123") } returns CloudActionResult.SuccessClearData
        
        viewModel.onIntent(CloudIntent.ClearCloudData)
        runCurrent()
        
        coVerify { clearCloudDataUseCase("user123") }
        assertEquals(CloudActionResult.SuccessClearData, viewModel.state.value.actionResult)
    }
}
