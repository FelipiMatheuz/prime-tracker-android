package com.felipimatheuz.primehunt.data.repository

import app.cash.turbine.test
import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimeCollectionDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimeCollectionSetDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimeComponentDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimePartDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimeSetDao
import com.felipimatheuz.primehunt.data.remote.dao.RelicDao
import com.felipimatheuz.primehunt.data.remote.entity.PrimeComponentEntity
import com.felipimatheuz.primehunt.data.remote.entity.PrimePartEntity
import com.felipimatheuz.primehunt.data.remote.entity.PrimeSetEntity
import com.felipimatheuz.primehunt.data.remote.entity.RelicEntity
import com.felipimatheuz.primehunt.data.remote.enums.DropRarity
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import com.felipimatheuz.primehunt.data.remote.enums.RelicEra
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.domain.usecase.primeset.GetPrimeSetsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrimeDataStoreTest {

    private val setDao = mockk<PrimeSetDao>()
    private val partDao = mockk<PrimePartDao>()
    private val componentDao = mockk<PrimeComponentDao>()
    private val relicDao = mockk<RelicDao>()
    private val inventoryDao = mockk<InventoryDao>()
    private val goalDao = mockk<GoalDao>()
    
    private val collectionDao = mockk<PrimeCollectionDao>()
    private val collectionSetDao = mockk<PrimeCollectionSetDao>()

    private lateinit var dataStore: PrimeDataStore
    private lateinit var useCase: GetPrimeSetsUseCase

    @Before
    fun setup() {
        dataStore = PrimeDataStore(setDao, partDao, componentDao, relicDao, inventoryDao, goalDao)
        useCase = GetPrimeSetsUseCase(dataStore, collectionDao, collectionSetDao)
    }

    @Test
    fun `ensure top-level blueprints are injected for sets with components but no blueprint part`() = runTest {
        // Given
        val setId = "main_set"
        
        val sets = listOf(
            PrimeSetEntity(setId, "Main Set", PrimeType.WARFRAME, "")
        )
        
        // No parts defined in part table
        val parts = emptyList<PrimePartEntity>()
        
        // Components defined
        val components = listOf(
            PrimeComponentEntity("c1", "r1", setId, DropRarity.RARE)
        )
        
        val relics = listOf(
            RelicEntity("r1", "A1", RelicEra.LITH, RelicSource.MISSION)
        )
        
        every { setDao.getAll() } returns flowOf(sets)
        every { partDao.getAll() } returns flowOf(parts)
        every { componentDao.getAll() } returns flowOf(components)
        every { relicDao.getAll() } returns flowOf(relics)
        every { inventoryDao.observeInventory() } returns flowOf(emptyList())

        // When
        useCase.observeAllSets().test {
            val result = awaitItem()
            
            // Then
            val mainSet = result.find { it.id == setId }
            val blueprint = mainSet?.parts?.find { it.name == PrimePartType.BLUEPRINT }
            
            assertEquals("Blueprint should be injected for top-level set", PrimePartType.BLUEPRINT, blueprint?.name)
        }
    }

    @Test
    fun `ensure nested blueprints are injected for sets with components but no blueprint part`() = runTest {
        // Given
        val setId = "main_set"
        val nestedSetId = "nested_set"
        
        val sets = listOf(
            PrimeSetEntity(setId, "Main Set", PrimeType.WARFRAME, ""),
            PrimeSetEntity(nestedSetId, "Nested Set", PrimeType.COMPANION, "")
        )
        
        // Main set has a part that is another set
        val partsCorrected = listOf(
            PrimePartEntity(nestedSetId, setId, PrimePartType.PRIME_SET, 1)
        )
        
        // Nested set has components (relic rewards) but NO entries in prime_part table for itself as a blueprint
        val components = listOf(
            PrimeComponentEntity("c1", "r1", nestedSetId, DropRarity.RARE)
        )
        
        val relics = listOf(
            RelicEntity("r1", "A1", RelicEra.LITH, RelicSource.MISSION)
        )
        
        every { setDao.getAll() } returns flowOf(sets)
        every { partDao.getAll() } returns flowOf(partsCorrected)
        every { componentDao.getAll() } returns flowOf(components)
        every { relicDao.getAll() } returns flowOf(relics)
        every { inventoryDao.observeInventory() } returns flowOf(emptyList())

        // When
        useCase.observeAllSets().test {
            val result = awaitItem()
            
            // Then
            val mainSet = result.find { it.id == setId }
            val nestedPart = mainSet?.parts?.find { it.id == nestedSetId }
            
            assertTrue("Nested part should have nested parts (recursive resolution)", nestedPart?.nestedParts?.isNotEmpty() == true)
            
            val nestedBlueprint = nestedPart?.nestedParts?.find { it.name == PrimePartType.BLUEPRINT }
            assertEquals("Blueprint should be injected for nested set", PrimePartType.BLUEPRINT, nestedBlueprint?.name)
        }
    }
}
