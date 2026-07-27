package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.entity.InventoryPartEntity
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
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.domain.model.PrimeCollection
import com.felipimatheuz.primehunt.domain.model.PrimePartDomain
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.domain.model.RelicRewardDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class PrimeRepository @Inject constructor(
    private val collectionDao: PrimeCollectionDao,
    private val setDao: PrimeSetDao,
    private val partDao: PrimePartDao,
    private val componentDao: PrimeComponentDao,
    private val relicDao: RelicDao,
    private val inventoryDao: InventoryDao,
    private val collectionSetDao: PrimeCollectionSetDao
) {

    private fun observeRawData() = combine(
        setDao.getAll().distinctUntilChanged(),
        partDao.getAll().distinctUntilChanged(),
        componentDao.getAll().distinctUntilChanged(),
        relicDao.getAll().distinctUntilChanged(),
        inventoryDao.observeInventory().distinctUntilChanged()
    ) { sets, parts, components, relics, inventory ->
        val invMap = inventory.associate { it.primePartId to it.quantity }
        mapToDomain(sets, parts, components, relics, invMap)
    }

    fun observeCollections(): Flow<List<PrimeCollection>> = combine(
        collectionDao.getAll().distinctUntilChanged(),
        collectionSetDao.getAll().distinctUntilChanged(),
        observeRawData()
    ) { collections, relations, allSets ->
        collections.map { coll ->
            val setIds = relations.filter { it.collectionId == coll.id }.map { it.primeSetId }
            PrimeCollection(
                id = coll.id,
                name = coll.name,
                promoImage = coll.promoImage,
                sets = allSets.filter { it.id in setIds }
            )
        }
    }

    fun observeWithoutCollection(): Flow<PrimeCollection> = combine(
        collectionSetDao.getAll().distinctUntilChanged(),
        observeRawData()
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

    fun observeSetDetails(setId: String): Flow<PrimeSetDomain?> = observeRawData()
        .map { allSets -> allSets.find { it.id == setId } }

    private fun mapToDomain(
        sets: List<PrimeSetEntity>,
        parts: List<PrimePartEntity>,
        components: List<PrimeComponentEntity>,
        relics: List<RelicEntity>,
        inventory: Map<String, Int>
    ): List<PrimeSetDomain> {
        val relicMap = relics.associateBy { it.id }
        val componentMap = components.groupBy { it.primePartId }
        val partsBySetMap = parts.groupBy { it.primeSetId }

        fun mapPart(part: PrimePartEntity, multiplier: Int): PrimePartDomain {
            val comps = componentMap[part.id] ?: emptyList()
            val relicRewards = comps.map { c ->
                val relic = relicMap[c.relicId]
                val formattedName = if (relic != null) "${relic.era.name.lowercase().replaceFirstChar { it.uppercase() }} ${relic.name}" else ""
                RelicRewardDomain(formattedName, c.rarity, relic?.source ?: RelicSource.VAULT)
            }
            val sources = comps.mapNotNull { relicMap[it.relicId]?.source }
            val bestSource = sources.minByOrNull { it.ordinal } ?: RelicSource.VAULT

            val nested = if (part.part == PrimePartType.PRIME_SET) {
                partsBySetMap[part.id]?.map { mapPart(it, multiplier * part.quantity) } ?: emptyList()
            } else {
                emptyList()
            }

            val imageUrl = if (part.part == PrimePartType.PRIME_SET) {
                sets.find { it.id == part.id }?.image
            } else null

            return PrimePartDomain(
                id = part.id,
                name = part.part,
                neededQuantity = part.quantity * multiplier,
                ownedQuantity = inventory[part.id] ?: 0,
                relics = relicRewards,
                bestSource = bestSource,
                imageUrl = imageUrl,
                nestedParts = nested
            )
        }

        return sets.map { set ->
            val setParts = partsBySetMap[set.id] ?: emptyList()
            val hasBlueprint = setParts.any { it.id == set.id }

            val domainParts = if (!hasBlueprint) {
                val comps = componentMap[set.id] ?: emptyList()
                if (comps.isNotEmpty()) {
                    val blueprint = mapPart(PrimePartEntity(set.id, set.id, PrimePartType.BLUEPRINT, 1), 1)
                    val others = setParts.map { mapPart(it, 1) }
                    others + blueprint
                } else {
                    setParts.map { mapPart(it, 1) }
                }
            } else {
                setParts.map { mapPart(it, 1) }
            }

            PrimeSetDomain(
                id = set.id,
                name = set.name,
                type = set.type,
                imageUrl = set.image,
                parts = domainParts.sortedBy { it.name }
            )
        }
    }

    suspend fun updateInventory(partId: String, delta: Int) = withContext(Dispatchers.IO) {
        val current = inventoryDao.get(partId)
        val newQuantity = maxOf(0, (current?.quantity ?: 0) + delta)
        inventoryDao.upsert(InventoryPartEntity(partId, newQuantity))
    }

    suspend fun updateSetInventory(setId: String, delta: Int) = withContext(Dispatchers.IO) {
        val partsToUpdate = mutableMapOf<String, Int>()
        collectPartsRecursively(setId, 1, partsToUpdate)
        
        partsToUpdate.forEach { (partId, needed) ->
            updateInventory(partId, needed * delta)
        }
    }

    private suspend fun collectPartsRecursively(setId: String, multiplier: Int, result: MutableMap<String, Int>) {
        val parts = partDao.getByPrimeSetSync(setId)
        val hasBlueprint = parts.any { it.id == setId }
        
        if (!hasBlueprint) {
            val comps = componentDao.getByPrimePartSync(setId)
            if (comps.isNotEmpty()) {
                result[setId] = (result[setId] ?: 0) + multiplier
            }
        }
        
        parts.forEach { part ->
            if (part.part == PrimePartType.PRIME_SET) {
                collectPartsRecursively(part.id, multiplier * part.quantity, result)
            } else {
                result[part.id] = (result[part.id] ?: 0) + multiplier * part.quantity
            }
        }
    }
}
