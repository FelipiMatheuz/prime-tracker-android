package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
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
import com.felipimatheuz.primehunt.domain.model.PrimePartDomain
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.domain.model.RelicRewardDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrimeDataStore @Inject constructor(
    setDao: PrimeSetDao,
    partDao: PrimePartDao,
    componentDao: PrimeComponentDao,
    relicDao: RelicDao,
    inventoryDao: InventoryDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val allSets: Flow<List<PrimeSetDomain>> = combine(
        setDao.getAll().distinctUntilChanged(),
        partDao.getAll().distinctUntilChanged(),
        componentDao.getAll().distinctUntilChanged(),
        relicDao.getAll().distinctUntilChanged(),
        inventoryDao.observeInventory().distinctUntilChanged()
    ) { sets, parts, components, relics, inventory ->
        val invMap = inventory.associate { it.primePartId to it.quantity }
        mapToDomain(sets, parts, components, relics, invMap)
    }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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
}
