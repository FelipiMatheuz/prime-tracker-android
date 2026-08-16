package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.*
import com.felipimatheuz.primehunt.data.mapper.PrimeMapper
import com.felipimatheuz.primehunt.data.mapper.RelicMapper
import com.felipimatheuz.primehunt.domain.model.PrimeCollection
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.domain.model.RelicDomain
import com.felipimatheuz.primehunt.domain.model.enums.RelicSource
import com.felipimatheuz.primehunt.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrimeRepositoryImpl @Inject constructor(
    private val primeDataStore: PrimeDataStore,
    private val goalRepository: GoalRepository,
    private val collectionDao: PrimeCollectionDao,
    private val collectionSetDao: PrimeCollectionSetDao,
    private val relicDao: RelicDao,
    private val partDao: PrimePartDao,
    private val setDao: PrimeSetDao,
    private val componentDao: PrimeComponentDao
) : PrimeRepository, InventoryRepository, RelicRepository {

    override fun observeAllSets(): Flow<List<PrimeSetDomain>> = combine(
        primeDataStore.baseData.distinctUntilChanged(),
        primeDataStore.inventoryDao.observeInventory().distinctUntilChanged()
    ) { data, inventory ->
        val inventoryMap = inventory.associate { it.primePartId to it.quantity }
        PrimeMapper.mapToDomainSets(data, inventoryMap)
    }

    override fun observeCollections(): Flow<List<PrimeCollection>> = combine(
        collectionDao.getAllWithSets().distinctUntilChanged(),
        observeAllSets()
    ) { collectionsWithSets, allSets ->
        collectionsWithSets.map { collWithSets ->
            val setIds = collWithSets.sets.map { it.id }.toSet()
            PrimeCollection(
                id = collWithSets.collection.id,
                name = collWithSets.collection.name,
                promoImage = collWithSets.collection.promoImage,
                sets = allSets.filter { it.id in setIds }
            )
        }
    }

    override fun observeWithoutCollection(): Flow<PrimeCollection> = combine(
        collectionSetDao.getAll().distinctUntilChanged(),
        observeAllSets()
    ) { relations, allSets ->
        val setsWithCollection = relations.map { it.primeSetId }.toSet()
        val setsWithout = allSets.filter { it.id !in setsWithCollection }
        PrimeCollection(
            "none",
            "",
            "https://www-static.warframe.com/images/guide/quests/sacrifice-key.jpg",
            setsWithout
        )
    }

    override fun observeAllRelics(): Flow<List<RelicDomain>> = combine(
        primeDataStore.baseData,
        primeDataStore.inventoryDao.observeInventory().distinctUntilChanged(),
        goalRepository.observeAllWithTags().distinctUntilChanged()
    ) { data, inventory, goals ->
        val inventoryMap = inventory.associate { it.primePartId to it.quantity }
        val goalsByTarget = goals.groupBy { it.targetId }
        RelicMapper.mapToDomain(data, inventoryMap, goalsByTarget)
    }

    override fun observeSetById(id: String): Flow<PrimeSetDomain?> = combine(
        primeDataStore.baseData.distinctUntilChanged(),
        primeDataStore.inventoryDao.observeInventory().distinctUntilChanged()
    ) { data, inventory ->
        val inventoryMap = inventory.associate { it.primePartId to it.quantity }
        PrimeMapper.mapToDomainSet(id, data, inventoryMap)
    }

    override fun getDatabaseCounts(): Flow<DatabaseCounts> = combine(
        collectionDao.count(),
        setDao.count(),
        partDao.count(),
        relicDao.count()
    ) { collections, sets, parts, relics ->
        DatabaseCounts(collections, sets, parts, relics)
    }

    override fun getRelicCounts(): Flow<RelicCounts> = combine(
        relicDao.countBySource(RelicSource.MISSION),
        relicDao.countBySource(RelicSource.VAULT),
        relicDao.countBySource(RelicSource.RESURGENCE),
        relicDao.countBySource(RelicSource.BARO)
    ) { mission, vault, resurgence, baro ->
        RelicCounts(mission, vault, resurgence, baro)
    }

    override suspend fun getAllPartsSync(): List<SyncPart> = partDao.getAllSync().map { 
        SyncPart(it.id, it.primeSetId, it.part, it.quantity) 
    }

    override suspend fun getAllSetsSync(): List<SyncSet> = setDao.getAllSync().map { 
        SyncSet(it.id, it.name, it.type, it.image) 
    }

    override suspend fun getPartsBySetSync(setId: String): List<SyncPart> = partDao.getByPrimeSetSync(setId).map { 
        SyncPart(it.id, it.primeSetId, it.part, it.quantity) 
    }

    override suspend fun getAllComponentsSync(): List<SyncComponent> = componentDao.getAllSync().map { 
        SyncComponent(it.id, it.relicId, it.primePartId, it.rarity) 
    }
}
