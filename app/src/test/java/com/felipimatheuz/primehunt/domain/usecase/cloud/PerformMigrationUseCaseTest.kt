package com.felipimatheuz.primehunt.domain.usecase.cloud

import com.felipimatheuz.primehunt.data.cloud.Firestore
import com.felipimatheuz.primehunt.domain.model.prefs.CloudUiPrefs
import com.felipimatheuz.primehunt.domain.repository.CloudRepository
import com.felipimatheuz.primehunt.domain.repository.UiPreferencesRepository
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudActionResult
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PerformMigrationUseCaseTest {

    private val firestore = mockk<Firestore>()
    private val cloudRepository = mockk<CloudRepository>()
    private val uiPreferencesRepository = mockk<UiPreferencesRepository>()
    private val useCase = PerformMigrationUseCase(firestore, cloudRepository, uiPreferencesRepository)

    private val userId = "test_user_id"

    @Test
    fun `invoke should return SuccessMigration when migration is successful`() = runTest {
        // Arrange
        val jsonContent = loadJsonFromResources("migration_mock.json")
        val legacyData = parseJsonToMap(jsonContent)
        
        coEvery { firestore.readLegacyChecklist(userId) } returns Firestore.LegacyChecklistResult.Success(legacyData)
        coEvery { cloudRepository.performMigration(any(), any()) } returns CloudRepository.MigrationResult(
            itemsInserted = 6,
            ignoredKeys = emptyList()
        )
        coEvery { firestore.deleteLegacyChecklist(userId) } returns true
        coEvery { uiPreferencesRepository.cloudPrefs } returns flowOf(CloudUiPrefs(isMigrationSuccess = false))
        coEvery { uiPreferencesRepository.updateCloudPrefs(any()) } just Runs

        // Act
        val result = useCase(userId)

        // Assert
        assertEquals(CloudActionResult.SuccessMigration, result)
        coVerify { cloudRepository.performMigration(any(), any()) }
        coVerify { firestore.deleteLegacyChecklist(userId) }
        coVerify { uiPreferencesRepository.updateCloudPrefs(match { it.isMigrationSuccess }) }
    }

    @Test
    fun `invoke should return Error when readLegacyChecklist fails`() = runTest {
        // Arrange
        coEvery { firestore.readLegacyChecklist(userId) } returns Firestore.LegacyChecklistResult.Error("Network error")

        // Act
        val result = useCase(userId)

        // Assert
        assertTrue(result is CloudActionResult.Error)
        assertEquals("Network error", (result as CloudActionResult.Error).message)
        coVerify(exactly = 0) { cloudRepository.performMigration(any(), any()) }
    }

    @Test
    fun `invoke should return Error when no items are inserted`() = runTest {
        // Arrange
        val legacyData = mapOf("SET" to emptyMap<String, Any>(), "OTHER" to emptyMap<String, Any>())
        coEvery { firestore.readLegacyChecklist(userId) } returns Firestore.LegacyChecklistResult.Success(legacyData)
        coEvery { cloudRepository.performMigration(any(), any()) } returns CloudRepository.MigrationResult(
            itemsInserted = 0,
            ignoredKeys = emptyList()
        )

        // Act
        val result = useCase(userId)

        // Assert
        assertTrue(result is CloudActionResult.Error)
        assertEquals("No matching items found for migration", (result as CloudActionResult.Error).message)
    }

    @Test
    fun `invoke should return Error when items are ignored`() = runTest {
        // Arrange
        val legacyData = mapOf("SET" to emptyMap<String, Any>(), "OTHER" to emptyMap<String, Any>())
        coEvery { firestore.readLegacyChecklist(userId) } returns Firestore.LegacyChecklistResult.Success(legacyData)
        coEvery { cloudRepository.performMigration(any(), any()) } returns CloudRepository.MigrationResult(
            itemsInserted = 0,
            ignoredKeys = listOf("Unknown_Item")
        )

        // Act
        val result = useCase(userId)

        // Assert
        assertTrue(result is CloudActionResult.Error)
        assertTrue((result as CloudActionResult.Error).message.contains("Unknown_Item"))
    }

    @Test
    fun `invoke should return Error when deleteLegacyChecklist fails`() = runTest {
        // Arrange
        val legacyData = mapOf("SET" to emptyMap<String, Any>(), "OTHER" to emptyMap<String, Any>())
        coEvery { firestore.readLegacyChecklist(userId) } returns Firestore.LegacyChecklistResult.Success(legacyData)
        coEvery { cloudRepository.performMigration(any(), any()) } returns CloudRepository.MigrationResult(
            itemsInserted = 1,
            ignoredKeys = emptyList()
        )
        coEvery { firestore.deleteLegacyChecklist(userId) } returns false

        // Act
        val result = useCase(userId)

        // Assert
        assertTrue(result is CloudActionResult.Error)
        assertEquals("Failed to remove legacy data from cloud", (result as CloudActionResult.Error).message)
    }

    private fun loadJsonFromResources(fileName: String): String {
        val classLoader = Thread.currentThread().contextClassLoader ?: javaClass.classLoader
        val resource = classLoader.getResource(fileName)
        return resource?.readText() ?: throw IllegalArgumentException("File not found: $fileName")
    }

    private fun parseJsonToMap(jsonString: String): Map<String, Any> {
        val jsonElement = Json.parseToJsonElement(jsonString)
        return jsonElement.toAny() as Map<String, Any>
    }

    private fun JsonElement.toAny(): Any? {
        return when (this) {
            is JsonObject -> this.mapValues { it.value.toAny() }
            is JsonArray -> this.map { it.toAny() }
            is JsonPrimitive -> {
                if (this.isString) this.content
                else if (this.booleanOrNull != null) this.booleanOrNull
                else if (this.doubleOrNull != null) this.doubleOrNull
                else if (this.intOrNull != null) this.intOrNull
                else if (this.longOrNull != null) this.longOrNull
                else null
            }
        }
    }
}
