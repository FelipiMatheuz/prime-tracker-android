package com.felipimatheuz.primehunt.domain.usecase.primeset

import com.felipimatheuz.primehunt.data.local.dao.PrimeCollectionDao
import com.felipimatheuz.primehunt.data.local.dao.PrimeCollectionSetDao
import com.felipimatheuz.primehunt.data.local.entity.PrimePartEntity
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.data.repository.PrimeDataStore
import com.felipimatheuz.primehunt.domain.mapper.PrimeMapper
import com.felipimatheuz.primehunt.domain.model.PrimeCollection
import com.felipimatheuz.primehunt.domain.model.PrimePartDomain
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.domain.model.RelicRewardDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetPrimeSetsUseCase @Inject constructor(
    private val primeDataStore: PrimeDataStore,
    private val collectionDao: PrimeCollectionDao,
    private val collectionSetDao: PrimeCollectionSetDao
) {

    fun observeAllSets(): Flow<List<PrimeSetDomain>> = combine(
        primeDataStore.baseData.distinctUntilChanged(),
        primeDataStore.inventoryDao.observeInventory().distinctUntilChanged()
    ) { data, inventory ->
        val inventoryMap = inventory.associate { it.primePartId to it.quantity }
        val relicMap = data.relics.associateBy { it.id }
        val componentMap = data.components.groupBy { it.primePartId }
        val partsBySetMap = data.parts.groupBy { it.primeSetId }
        val setEntityMap = data.sets.associateBy { it.id }

        fun resolveParts(
            setId: String,
            multiplier: Int,
            mapper: (PrimePartEntity, Int) -> PrimePartDomain
        ): List<PrimePartDomain> {
            val setParts = partsBySetMap[setId] ?: emptyList()
            val hasBlueprint = setParts.any { it.id == setId }

            return if (!hasBlueprint) {
                val comps = componentMap[setId] ?: emptyList()
                if (comps.isNotEmpty()) {
                    val blueprint = mapper(
                        PrimePartEntity(setId, setId, PrimePartType.BLUEPRINT, 1),
                        multiplier
                    )
                    val others = setParts.map { mapper(it, multiplier) }
                    listOf(blueprint) + others
                } else {
                    setParts.map { mapper(it, multiplier) }
                }
            } else {
                setParts.map { mapper(it, multiplier) }
            }
        }

        fun mapPart(part: PrimePartEntity, multiplier: Int): PrimePartDomain {
            val comps = componentMap[part.id] ?: emptyList()
            val relicRewards = comps.map { c ->
                val relic = relicMap[c.relicId]
                val formattedName = if (relic != null) "${
                    PrimeMapper.capitalizeWords(relic.era.name.lowercase())
                } ${relic.name}" else ""
                RelicRewardDomain(formattedName, c.rarity, relic?.source ?: RelicSource.VAULT)
            }
            val sources = comps.mapNotNull { relicMap[it.relicId]?.source }
            val bestSource = sources.minByOrNull { it.ordinal } ?: RelicSource.VAULT

            val nested = if (part.part == PrimePartType.PRIME_SET) {
                resolveParts(part.id, multiplier * part.quantity, ::mapPart)
            } else {
                emptyList()
            }

            val imageUrl = if (part.part == PrimePartType.PRIME_SET) {
                setEntityMap[part.id]?.image
            } else null

            return PrimePartDomain(
                id = part.id,
                name = part.part,
                neededQuantity = part.quantity * multiplier,
                ownedQuantity = inventoryMap[part.id] ?: 0,
                relics = relicRewards,
                bestSource = bestSource,
                imageUrl = imageUrl,
                nestedParts = nested
            )
        }

        data.sets.map { set ->
            PrimeSetDomain(
                id = set.id,
                name = set.name,
                type = set.type,
                imageUrl = set.image,
                parts = resolveParts(set.id, 1, ::mapPart).sortedBy { it.name }
            )
        }
    }.flowOn(Dispatchers.Default)

    fun observeCollections(): Flow<List<PrimeCollection>> = combine(
        collectionDao.getAll().distinctUntilChanged(),
        collectionSetDao.getAll().distinctUntilChanged(),
        observeAllSets()
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
}
