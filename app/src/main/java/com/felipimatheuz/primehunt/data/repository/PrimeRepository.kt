package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.entity.InventoryPartEntity
import com.felipimatheuz.primehunt.data.remote.dao.PrimeCollectionDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimeComponentDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimePartDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimeSetDao
import com.felipimatheuz.primehunt.data.remote.dao.RelicDao
import com.felipimatheuz.primehunt.data.remote.entity.PrimePartEntity
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
    private val inventoryDao: InventoryDao
) {

    fun observeCollections(): Flow<List<PrimeCollection>> =
        collectionDao.getAll().distinctUntilChanged().flatMapLatest { collections ->
            if (collections.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val collectionFlows = collections.map { coll ->
                observeSetsByCollection(coll.id).map { sets ->
                    PrimeCollection(
                        id = coll.id,
                        name = coll.name,
                        promoImage = coll.promoImage,
                        sets = sets
                    )
                }
            }
            combine(collectionFlows) { it.toList() }
        }

    fun observeWithoutCollection(): Flow<PrimeCollection> =
        setDao.getWithoutCollection().flatMapLatest { sets ->
            if (sets.isEmpty()) return@flatMapLatest flowOf(
                PrimeCollection(
                    "none",
                    "",
                    "https://www-static.warframe.com/images/guide/quests/sacrifice-key.jpg",
                    emptyList()
                )
            )

            val setFlows = sets.map { observeSetDetails(it.id) }
            combine(setFlows) { it.toList().filterNotNull() }.map {
                PrimeCollection(
                    "none",
                    "",
                    "https://www-static.warframe.com/images/guide/quests/sacrifice-key.jpg",
                    it
                )
            }
        }

    fun observeSetsGroupedByCategory(): Flow<Map<PrimeType, List<PrimeSetDomain>>> =
        observeAllSetsWithProgress()
            .map { sets -> sets.groupBy { it.type } }

    private fun observeSetsByCollection(collectionId: String): Flow<List<PrimeSetDomain>> =
        setDao.getByCollection(collectionId).flatMapLatest { sets ->
            if (sets.isEmpty()) return@flatMapLatest flowOf(emptyList())
            combine(sets.map { observeSetDetails(it.id) }) { it.toList().filterNotNull() }
        }

    fun observeAllSetsWithProgress(): Flow<List<PrimeSetDomain>> =
        setDao.getAll().flatMapLatest { sets ->
            if (sets.isEmpty()) return@flatMapLatest flowOf(emptyList())
            combine(sets.map { observeSetDetails(it.id) }) { it.toList().filterNotNull() }
        }

    fun observeSetDetails(setId: String): Flow<PrimeSetDomain?> = setDao.observeById(setId)
        .flatMapLatest { setEntity ->
            if (setEntity == null) return@flatMapLatest flowOf(null)
            observePartList(setId, 1).map { domainParts ->
                PrimeSetDomain(
                    id = setEntity.id,
                    name = setEntity.name,
                    type = setEntity.type,
                    imageUrl = setEntity.image,
                    parts = domainParts.sortedBy { it.name }
                )
            }
        }

    private fun observePartList(setId: String, multiplier: Int): Flow<List<PrimePartDomain>> =
        partDao.getByPrimeSet(setId).flatMapLatest { parts ->
            val hasBlueprintInParts = parts.any { it.id == setId }

            val initialPartsFlow = if (!hasBlueprintInParts) {
                componentDao.getByPrimePart(setId).flatMapLatest { comps ->
                    if (comps.isNotEmpty()) {
                        val blueprintFlow = observePartDomain(
                            PrimePartEntity(setId, setId, PrimePartType.BLUEPRINT, 1),
                            multiplier
                        )
                        val otherPartsFlows = parts.map { observePartDomain(it, multiplier) }
                        combine(otherPartsFlows + blueprintFlow) { it.toList() }
                    } else {
                        if (parts.isEmpty()) flowOf(emptyList())
                        else combine(parts.map { observePartDomain(it, multiplier) }) { it.toList() }
                    }
                }
            } else {
                if (parts.isEmpty()) flowOf(emptyList())
                else combine(parts.map { observePartDomain(it, multiplier) }) { it.toList() }
            }
            initialPartsFlow
        }

    private fun observePartDomain(part: PrimePartEntity, multiplier: Int): Flow<PrimePartDomain> {
        val ownedFlow = inventoryDao.observeInventory()
            .map { inv -> inv.find { it.primePartId == part.id }?.quantity ?: 0 }
            .distinctUntilChanged()

        val relicInfoFlow = componentDao.getByPrimePart(part.id).distinctUntilChanged().flatMapLatest { comps ->
            if (comps.isEmpty()) return@flatMapLatest flowOf(emptyList<RelicSource>() to emptyList())
            val relicFlows = comps.map { c ->
                flow {
                    val relic = relicDao.getById(c.relicId)
                    val formattedName = if (relic != null) "${relic.era.name.lowercase().replaceFirstChar { it.uppercase() }} ${relic.name}" else ""
                    emit(relic?.source to RelicRewardDomain(formattedName, c.rarity, relic?.source ?: RelicSource.VAULT))
                }
            }
            combine(relicFlows) { it.toList() }.map { list ->
                list.mapNotNull { it.first } to list.map { it.second }.filter { it.name.isNotEmpty() }
            }
        }

        val nestedPartsFlow = if (part.part == PrimePartType.PRIME_SET) {
            observePartList(part.id, multiplier * part.quantity)
        } else {
            flowOf(emptyList())
        }

        val imageUrlFlow = if (part.part == PrimePartType.PRIME_SET) {
            flow { emit(setDao.getByIdSync(part.id)?.image) }
        } else {
            flowOf<String?>(null)
        }

        return combine(ownedFlow, relicInfoFlow, nestedPartsFlow, imageUrlFlow) { owned, sourceInfo, nested, imageUrl ->
            val (sources, relics) = sourceInfo
            val bestSource = sources.minByOrNull { it.ordinal } ?: RelicSource.VAULT

            PrimePartDomain(
                id = part.id,
                name = part.part,
                neededQuantity = part.quantity * multiplier,
                ownedQuantity = owned,
                relics = relics,
                bestSource = bestSource,
                imageUrl = imageUrl,
                nestedParts = nested
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
