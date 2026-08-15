package com.felipimatheuz.primehunt.data.repository

import androidx.room.withTransaction
import com.felipimatheuz.primehunt.data.local.AppDatabase
import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.GoalTagDao
import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.entity.InventoryPartEntity
import com.felipimatheuz.primehunt.domain.mapper.LegacyMigrationParser
import com.felipimatheuz.primehunt.domain.model.enums.PrimePartType
import com.felipimatheuz.primehunt.domain.model.enums.PrimeType
import com.felipimatheuz.primehunt.domain.repository.PrimeRepository
import com.felipimatheuz.primehunt.domain.repository.SyncPart
import com.felipimatheuz.primehunt.domain.repository.SyncSet
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CloudRepositoryImplTest {

    private val database = mockk<AppDatabase>(relaxed = true)
    private val inventoryDao = mockk<InventoryDao>(relaxed = true)
    private val goalDao = mockk<GoalDao>(relaxed = true)
    private val tagDao = mockk<GoalTagDao>(relaxed = true)
    private val primeRepository = mockk<PrimeRepository>(relaxed = true)
    private val parser = LegacyMigrationParser()

    private lateinit var repository: CloudRepositoryImpl

    @Before
    fun setup() {
        mockkStatic("androidx.room.RoomDatabaseKt")
        coEvery { database.withTransaction<Any?>(any()) } coAnswers {
            val block = invocation.args[1] as suspend () -> Any?
            block()
        }
        repository = CloudRepositoryImpl(database, inventoryDao, goalDao, tagDao, primeRepository, parser)
    }

    @Test
    fun `performMigration should accurately match items from migration_mock json`() = runTest {
        // Arrange
        val jsonContent = loadJsonFromResources("migration_mock.json")
        val legacyData = parseJsonToMap(jsonContent) as Map<String, Map<String, Any>>
        val mapSet = legacyData["SET"] ?: emptyMap()
        val mapOther = legacyData["OTHER"] ?: emptyMap()

        // Mock items in the new database that match migration_mock.json
        coEvery { primeRepository.getAllSetsSync() } returns listOf(
            SyncSet("gauss_prime", "Gauss Prime", PrimeType.WARFRAME, ""),
            SyncSet("fang_prime", "Fang Prime", PrimeType.MELEE, ""),
            SyncSet("lex_prime", "Lex Prime", PrimeType.SECONDARY, "")
        )
        coEvery { primeRepository.getAllPartsSync() } returns listOf(
            SyncPart("gauss_neuro", "gauss_prime", PrimePartType.NEUROPTICS, 1),
            SyncPart("gauss_chassis", "gauss_prime", PrimePartType.CHASSIS, 1),
            SyncPart("gauss_systems", "gauss_prime", PrimePartType.SYSTEMS, 1),
            SyncPart("fang_blade", "fang_prime", PrimePartType.BLADE, 2),
            SyncPart("lex_barrel", "lex_prime", PrimePartType.BARREL, 1)
        )

        // Act
        val result = repository.performMigration(mapSet, mapOther)

        // Assert
        val captured = slot<List<InventoryPartEntity>>()
        coVerify { inventoryDao.upsert(capture(captured)) }

        val gaussNeuro = captured.captured.find { it.primePartId == "gauss_neuro" }
        val fangBlade = captured.captured.find { it.primePartId == "fang_blade" }
        val lexBarrel = captured.captured.find { it.primePartId == "lex_barrel" }

        assertEquals(1, gaussNeuro?.quantity)
        assertEquals(2, fangBlade?.quantity) // Fang_BLADE_0 and Fang_BLADE_1 both true in mock
        assertEquals(1, lexBarrel?.quantity)
        assertEquals(5, result.itemsInserted)
        assertTrue(result.ignoredKeys.isEmpty())
    }

    @Test
    fun `performMigration should handle nested sets like Aklex from Lex legacy key`() = runTest {
        // Arrange
        // Mocking legacy data similar to what might be in migration_mock but specific for this case
        val mapSet = mapOf("Aklex_LEX_0" to true) 
        
        coEvery { primeRepository.getAllSetsSync() } returns listOf(
            SyncSet("aklex_prime", "Aklex Prime", PrimeType.SECONDARY, ""),
            SyncSet("lex_prime", "Lex Prime", PrimeType.SECONDARY, "")
        )
        coEvery { primeRepository.getAllPartsSync() } returns listOf(
            SyncPart("lex_as_part", "aklex_prime", PrimePartType.PRIME_SET, 2)
        )
        coEvery { primeRepository.getPartsBySetSync("lex_as_part") } returns listOf(
            SyncPart("lex_bp", "lex_prime", PrimePartType.BLUEPRINT, 1),
            SyncPart("lex_barrel", "lex_prime", PrimePartType.BARREL, 1)
        )

        // Act
        val result = repository.performMigration(mapSet, emptyMap())

        // Assert
        val captured = slot<List<InventoryPartEntity>>()
        coVerify { inventoryDao.upsert(capture(captured)) }

        // Should have 2 items inserted (BP and Barrel from Lex)
        assertEquals(2, result.itemsInserted)
        assertTrue(captured.captured.any { it.primePartId == "lex_bp" })
        assertTrue(captured.captured.any { it.primePartId == "lex_barrel" })
    }

    @Test
    fun `performMigration should return ignored keys when items are not matched`() = runTest {
        // Arrange
        val mapSet = mapOf("Unknown_Item_PART_0" to true)
        coEvery { primeRepository.getAllSetsSync() } returns emptyList()
        coEvery { primeRepository.getAllPartsSync() } returns emptyList()

        // Act
        val result = repository.performMigration(mapSet, emptyMap())

        // Assert
        assertEquals(0, result.itemsInserted)
        assertEquals(1, result.ignoredKeys.size)
        assertEquals("Item_PART", result.ignoredKeys[0])
        coVerify(exactly = 0) { inventoryDao.upsert(any<List<InventoryPartEntity>>()) }
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
