package com.felipimatheuz.primehunt.data.repository

import androidx.room.withTransaction
import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.entity.InventoryPartEntity
import com.felipimatheuz.primehunt.data.local.AppDatabase
import com.felipimatheuz.primehunt.data.local.dao.PrimePartDao
import com.felipimatheuz.primehunt.data.local.dao.PrimeSetDao
import com.felipimatheuz.primehunt.data.local.entity.PrimePartEntity
import com.felipimatheuz.primehunt.data.local.entity.PrimeSetEntity
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import com.felipimatheuz.primehunt.domain.mapper.LegacyMigrationParser
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LegacyMigrationIntegrationTest {

    private val database = mockk<AppDatabase>(relaxed = true)
    private val inventoryDao = mockk<InventoryDao>(relaxed = true)
    private val primePartDao = mockk<PrimePartDao>(relaxed = true)
    private val primeSetDao = mockk<PrimeSetDao>(relaxed = true)
    private val parser = LegacyMigrationParser()

    private lateinit var repository: CloudRepository

    @Before
    fun setup() {
        mockkStatic("androidx.room.RoomDatabaseKt")
        coEvery { database.withTransaction<Any?>(any()) } coAnswers {
            val block = invocation.args[1] as suspend () -> Any?
            block()
        }
        repository = CloudRepository(database, inventoryDao, primePartDao, primeSetDao, parser)
    }

    @Test
    fun `should migrate legacy bundle parts and accumulate quantity`() = runTest {
        // Massa Legada
        val mapSet = mapOf(
            "Gauss_Gauss_NEUROPTICS_0" to true, 
        )
        val mapOther = mapOf(
            "Fang_BLADE_0" to true,            
            "Fang_BLADE_1" to true             
        )

        // Mock Novo Banco
        coEvery { primeSetDao.getAllSync() } returns listOf(
            PrimeSetEntity("gauss_prime", "Gauss Prime", PrimeType.WARFRAME, ""),
            PrimeSetEntity("fang_prime", "Fang Prime", PrimeType.MELEE, "")
        )
        coEvery { primePartDao.getAllSync() } returns listOf(
            PrimePartEntity("gauss_neuro", "gauss_prime", PrimePartType.NEUROPTICS, 1),
            PrimePartEntity("fang_blade", "fang_prime", PrimePartType.BLADE, 1)
        )

        val result = repository.performMigration(mapSet, mapOther)

        val captured = slot<List<InventoryPartEntity>>()
        coVerify { inventoryDao.upsert(capture(captured)) }

        val gaussPart = captured.captured.find { it.primePartId == "gauss_neuro" }
        val fangPart = captured.captured.find { it.primePartId == "fang_blade" }

        assertEquals(1, gaussPart?.quantity)
        assertEquals(2, fangPart?.quantity) 
        assertEquals(2, result.itemsInserted)
        assertTrue(result.ignoredKeys.isEmpty())
    }

    @Test
    fun `should return ignored keys when no match is found`() = runTest {
        val mapSet = mapOf(
            "Unknown_Item_PART_0" to true
        )
        
        coEvery { primeSetDao.getAllSync() } returns emptyList()
        coEvery { primePartDao.getAllSync() } returns emptyList()

        val result = repository.performMigration(mapSet, emptyMap())

        assertEquals(0, result.itemsInserted)
        assertEquals(1, result.ignoredKeys.size)
        assertEquals("Item_PART", result.ignoredKeys[0])
    }

    @Test
    fun `should migrate nested sets like Aklex from LEX legacy key`() = runTest {
        val mapSet = mapOf(
            "Aklex_LEX_0" to true 
        )

        coEvery { primeSetDao.getAllSync() } returns listOf(
            PrimeSetEntity("aklex_prime", "Aklex Prime", PrimeType.SECONDARY, ""),
            PrimeSetEntity("lex_prime", "Lex Prime", PrimeType.SECONDARY, "")
        )

        coEvery { primePartDao.getAllSync() } returns listOf(
            PrimePartEntity("lex_as_part", "aklex_prime", PrimePartType.PRIME_SET, 2)
        )

        coEvery { primePartDao.getByPrimeSetSync("lex_as_part") } returns listOf(
            PrimePartEntity("lex_bp", "lex_prime", PrimePartType.BLUEPRINT, 1),
            PrimePartEntity("lex_barrel", "lex_prime", PrimePartType.BARREL, 1)
        )

        val result = repository.performMigration(mapSet, emptyMap())

        val captured = slot<List<InventoryPartEntity>>()
        coVerify { inventoryDao.upsert(capture(captured)) }

        assert(captured.captured.any { it.primePartId == "lex_bp" })
        assert(captured.captured.any { it.primePartId == "lex_barrel" })
        assertEquals(2, result.itemsInserted)
    }
}
