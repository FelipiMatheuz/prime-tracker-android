package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.entity.GoalWithTag
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
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.domain.model.PrimePartDomain
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.domain.model.RelicComponentDomain
import com.felipimatheuz.primehunt.domain.model.RelicDomain
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
    inventoryDao: InventoryDao,
    goalDao: GoalDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private data class BaseDataSnapshot(
        val sets: List<PrimeSetEntity>,
        val parts: List<PrimePartEntity>,
        val components: List<PrimeComponentEntity>,
        val relics: List<RelicEntity>
    )

    private val baseData: Flow<BaseDataSnapshot> = combine(
        setDao.getAll().distinctUntilChanged(),
        partDao.getAll().distinctUntilChanged(),
        componentDao.getAll().distinctUntilChanged(),
        relicDao.getAll().distinctUntilChanged()
    ) { sets, parts, components, relics ->
        BaseDataSnapshot(sets, parts, components, relics)
    }

    val allSets: Flow<List<PrimeSetDomain>> = combine(
        baseData,
        inventoryDao.observeInventory().distinctUntilChanged()
    ) { data, inventory ->
        val invMap = inventory.associate { it.primePartId to it.quantity }
        mapToDomain(data.sets, data.parts, data.components, data.relics, invMap)
    }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allRelics: Flow<List<RelicDomain>> = combine(
        baseData,
        inventoryDao.observeInventory().distinctUntilChanged(),
        goalDao.observeAllWithTags().distinctUntilChanged()
    ) { data, inventory, goals ->
        val invMap = inventory.associate { it.primePartId to it.quantity }
        mapToRelicDomain(data.relics, data.components, data.parts, data.sets, invMap, goals)
    }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private fun mapToRelicDomain(
        relics: List<RelicEntity>,
        components: List<PrimeComponentEntity>,
        parts: List<PrimePartEntity>,
        sets: List<PrimeSetEntity>,
        inventory: Map<String, Int>,
        goalList: List<GoalWithTag>
    ): List<RelicDomain> {
        val partMap = parts.associateBy { it.id }
        val setMap = sets.associateBy { it.id }
        val componentMap = components.groupBy { it.relicId }
        val partsBySetMap = parts.groupBy { it.primeSetId }

        // Group goals by targetId for efficient lookup, but keep type context
        val goalsByTarget = goalList.groupBy { it.goal.targetId }

        return relics.map { relic ->
            val relicComponents = componentMap[relic.id]?.map { comp ->
                val part = partMap[comp.primePartId]
                val set = part?.let { setMap[it.primeSetId] } ?: setMap[comp.primePartId]

                var isForma = false
                var suffixRes: Int? = null

                val name = when {
                    part != null && set != null -> {
                        "${set.name} ${
                            part.part.name.replace("_", " ").lowercase().capitalizeWords()
                        }"
                    }

                    set != null -> {
                        suffixRes = R.string.comp_blueprint
                        set.name
                    }

                    else -> {
                        isForma = true
                        ""
                    }
                }

                val owned = part?.let { inventory[it.id] ?: 0 } ?: inventory[comp.primePartId] ?: 0
                val needed = part?.quantity ?: 1
                val isObtained = owned >= needed

                val compositeInfo = part?.let { p ->
                    partsBySetMap[p.primeSetId]?.find { it.part == PrimePartType.PRIME_SET }
                        ?.let { dep ->
                            setMap[dep.id]?.let { depSet ->
                                "${depSet.name} ×${dep.quantity}"
                            }
                        }
                }

                // Match rules for components:
                // 1. Direct PRIME_PART goal
                // 2. Parent PRIME_SET goal
                // 3. Synthesized PRIME_PART goal (where comp.primePartId is the setId)
                val relevantGoals = mutableListOf<GoalWithTag>()
                
                // Rule 1: Direct part goal
                goalsByTarget[comp.primePartId]?.filter { 
                    it.goal.targetType == com.felipimatheuz.primehunt.data.local.enums.GoalTargetType.PRIME_PART 
                }?.let { relevantGoals.addAll(it) }

                // Rule 2 & 3: From Parent Set (if exists)
                set?.let { s ->
                    goalsByTarget[s.id]?.filter { 
                        it.goal.targetType == com.felipimatheuz.primehunt.data.local.enums.GoalTargetType.PRIME_SET ||
                        (it.goal.targetType == com.felipimatheuz.primehunt.data.local.enums.GoalTargetType.PRIME_PART && part == null)
                    }?.let { relevantGoals.addAll(it) }
                }

                val goalTags = relevantGoals.distinctBy { it.tag.id }.map {
                    GoalTagDomain(
                        id = it.tag.id,
                        name = it.tag.name,
                        icon = it.tag.icon,
                        color = it.tag.color
                    )
                }

                RelicComponentDomain(
                    name = name,
                    rarity = comp.rarity,
                    isObtained = isObtained,
                    neededQuantity = needed,
                    ownedQuantity = owned,
                    compositeInfo = compositeInfo,
                    isForma = isForma,
                    nameSuffixRes = suffixRes,
                    goalTags = goalTags
                )
            } ?: emptyList()

            // Match rules for relics: Only RELIC target type
            val relicTags = goalsByTarget[relic.id]?.filter { 
                it.goal.targetType == com.felipimatheuz.primehunt.data.local.enums.GoalTargetType.RELIC 
            }?.map {
                GoalTagDomain(
                    id = it.tag.id,
                    name = it.tag.name,
                    icon = it.tag.icon,
                    color = it.tag.color
                )
            }?.distinctBy { it.id } ?: emptyList()

            RelicDomain(
                id = relic.id,
                name = relic.name,
                era = relic.era,
                source = relic.source,
                rewards = relicComponents,
                goalTags = relicTags
            )
        }
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

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
                    relic.era.name.lowercase().replaceFirstChar { it.uppercase() }
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
            PrimeSetDomain(
                id = set.id,
                name = set.name,
                type = set.type,
                imageUrl = set.image,
                parts = resolveParts(set.id, 1, ::mapPart).sortedBy { it.name }
            )
        }
    }
}
