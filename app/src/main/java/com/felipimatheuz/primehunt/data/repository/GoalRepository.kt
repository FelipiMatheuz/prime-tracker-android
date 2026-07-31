package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.entity.GoalEntity
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
import com.felipimatheuz.primehunt.data.remote.dao.PrimeComponentDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimePartDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimeSetDao
import com.felipimatheuz.primehunt.data.remote.dao.RelicDao
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.domain.model.TargetDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao,
    private val goalTagDao: com.felipimatheuz.primehunt.data.local.dao.GoalTagDao,
    private val inventoryDao: InventoryDao,
    setDao: PrimeSetDao,
    partDao: PrimePartDao,
    componentDao: PrimeComponentDao,
    relicDao: RelicDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private data class GoalMetadata(
        val setMap: Map<String, com.felipimatheuz.primehunt.data.remote.entity.PrimeSetEntity>,
        val partMap: Map<String, com.felipimatheuz.primehunt.data.remote.entity.PrimePartEntity>,
        val partsBySetMap: Map<String, List<com.felipimatheuz.primehunt.data.remote.entity.PrimePartEntity>>,
        val componentByPartMap: Map<String, List<com.felipimatheuz.primehunt.data.remote.entity.PrimeComponentEntity>>,
        val relicMap: Map<String, com.felipimatheuz.primehunt.data.remote.entity.RelicEntity>,
        val synthesizedBlueprints: List<TargetDomain>
    )

    private val metadataFlow: StateFlow<GoalMetadata?> = combine(
        setDao.getAll().distinctUntilChanged(),
        partDao.getAll().distinctUntilChanged(),
        componentDao.getAll().distinctUntilChanged(),
        relicDao.getAll().distinctUntilChanged()
    ) { sets, parts, components, relics ->
        val componentByPartMap = components.groupBy { it.primePartId }
        val synthesizedBlueprints = sets.filter { set ->
            val hasExplicitBlueprint = parts.any { it.id == set.id && it.primeSetId == set.id }
            !hasExplicitBlueprint && componentByPartMap[set.id]?.isNotEmpty() == true
        }.map { set ->
            TargetDomain(
                set.id,
                "${set.name} Blueprint",
                GoalTargetType.PRIME_PART
            )
        }

        GoalMetadata(
            setMap = sets.associateBy { it.id },
            partMap = parts.associateBy { it.id },
            partsBySetMap = parts.groupBy { it.primeSetId },
            componentByPartMap = componentByPartMap,
            relicMap = relics.associateBy { it.id },
            synthesizedBlueprints = synthesizedBlueprints
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun observeGoals(): Flow<List<GoalDomain>> {
        return combine(
            goalDao.observeAllWithTags().distinctUntilChanged(),
            inventoryDao.observeInventory().distinctUntilChanged(),
            metadataFlow.filterNotNull()
        ) { goals, inventory, metadata ->
            val inventoryMap = inventory.associate { it.primePartId to it.quantity }

            goals.map { item ->
                val goal = item.goal
                val tag = item.tag

                val (name, current) = when (goal.targetType) {
                    GoalTargetType.PRIME_SET -> {
                        val required = mutableMapOf<String, Int>()
                        resolveRequiredParts(
                            goal.targetId,
                            1,
                            metadata.partsBySetMap,
                            metadata.componentByPartMap,
                            required
                        )

                        val completedSets = if (required.isEmpty()) 0 else {
                            required.map { (partId, needed) ->
                                (inventoryMap[partId] ?: 0) / (needed.takeIf { it > 0 } ?: 1)
                            }.minOrNull() ?: 0
                        }

                        (metadata.setMap[goal.targetId]?.name ?: "Unknown Set") to completedSets
                    }

                    GoalTargetType.PRIME_PART -> {
                        val part = metadata.partMap[goal.targetId]
                        val setName = part?.let { metadata.setMap[it.primeSetId]?.name } ?: ""
                        val partName =
                            part?.part?.name?.replace("_", " ")?.lowercase()?.capitalizeWords() ?: ""

                        val displayName = if (setName.isNotEmpty()) "$setName $partName".trim() else {
                            val set = metadata.setMap[goal.targetId]
                            if (set != null) "${set.name} Blueprint" else "Unknown Part"
                        }

                        displayName to (inventoryMap[goal.targetId] ?: 0)
                    }

                    GoalTargetType.RELIC -> {
                        val relic = metadata.relicMap[goal.targetId]
                        val relicName =
                            relic?.let { "${it.era.name} ${it.name}" } ?: "Unknown Relic"
                        relicName to goal.currentQuantity
                    }

                    GoalTargetType.FORMA -> {
                        "Forma" to goal.currentQuantity
                    }
                }

                GoalDomain(
                    id = goal.id,
                    targetId = goal.targetId,
                    targetName = name,
                    targetType = goal.targetType,
                    currentQuantity = current,
                    desiredQuantity = goal.desiredQuantity,
                    status = goal.status,
                    note = goal.note,
                    tag = GoalTagDomain(
                        id = tag.id,
                        name = tag.name,
                        icon = tag.icon,
                        color = tag.color
                    )
                )
            }
        }.flowOn(Dispatchers.Default)
    }

    fun observeAllTargets(): Flow<List<TargetDomain>> {
        return metadataFlow.filterNotNull().map { metadata ->
            val setTargets = metadata.setMap.values.map {
                TargetDomain(it.id, it.name, GoalTargetType.PRIME_SET)
            }

            val partTargets = metadata.partMap.values.map { part ->
                val setName = metadata.setMap[part.primeSetId]?.name ?: ""
                val partName = part.part.name.replace("_", " ").lowercase().capitalizeWords()
                TargetDomain(part.id, "$setName $partName".trim(), GoalTargetType.PRIME_PART)
            }

            val relicTargets = metadata.relicMap.values.map {
                TargetDomain(it.id, "${it.era.name} ${it.name}", GoalTargetType.RELIC)
            }
            
            val formaTargets = listOf(
                TargetDomain("forma_blueprint", "Forma Blueprint", GoalTargetType.FORMA)
            )

            setTargets + metadata.synthesizedBlueprints + partTargets + relicTargets + formaTargets
        }.flowOn(Dispatchers.Default)
    }

    fun observeGoal(id: Long): Flow<GoalDomain?> {
        return combine(
            goalDao.observeByIdWithTag(id).distinctUntilChanged(),
            inventoryDao.observeInventory().distinctUntilChanged(),
            metadataFlow.filterNotNull()
        ) { item, inventory, metadata ->
            if (item == null) return@combine null

            val inventoryMap = inventory.associate { it.primePartId to it.quantity }
            val goal = item.goal
            val tag = item.tag

            val (name, current) = when (goal.targetType) {
                GoalTargetType.PRIME_SET -> {
                    val required = mutableMapOf<String, Int>()
                    resolveRequiredParts(
                        goal.targetId,
                        1,
                        metadata.partsBySetMap,
                        metadata.componentByPartMap,
                        required
                    )

                    val completedSets = if (required.isEmpty()) 0 else {
                        required.map { (partId, needed) ->
                            (inventoryMap[partId] ?: 0) / (needed.takeIf { it > 0 } ?: 1)
                        }.minOrNull() ?: 0
                    }

                    (metadata.setMap[goal.targetId]?.name ?: "Unknown Set") to completedSets
                }

                GoalTargetType.PRIME_PART -> {
                    val part = metadata.partMap[goal.targetId]
                    val setName = part?.let { metadata.setMap[it.primeSetId]?.name } ?: ""
                    val partName =
                        part?.part?.name?.replace("_", " ")?.lowercase()?.capitalizeWords() ?: ""

                    val displayName = if (setName.isNotEmpty()) "$setName $partName".trim() else {
                        val set = metadata.setMap[goal.targetId]
                        if (set != null) "${set.name} Blueprint" else "Unknown Part"
                    }

                    displayName to (inventoryMap[goal.targetId] ?: 0)
                }

                GoalTargetType.RELIC -> {
                    val relic = metadata.relicMap[goal.targetId]
                    val relicName =
                        relic?.let { "${it.era.name} ${it.name}" } ?: "Unknown Relic"
                    relicName to goal.currentQuantity
                }

                GoalTargetType.FORMA -> {
                    "Forma" to goal.currentQuantity
                }
            }

            GoalDomain(
                id = goal.id,
                targetId = goal.targetId,
                targetName = name,
                targetType = goal.targetType,
                currentQuantity = current,
                desiredQuantity = goal.desiredQuantity,
                status = goal.status,
                note = goal.note,
                tag = GoalTagDomain(
                    id = tag.id,
                    name = tag.name,
                    icon = tag.icon,
                    color = tag.color
                )
            )
        }.flowOn(Dispatchers.Default)
    }

    private fun resolveRequiredParts(
        setId: String,
        multiplier: Int,
        partsBySetMap: Map<String, List<com.felipimatheuz.primehunt.data.remote.entity.PrimePartEntity>>,
        componentByPartMap: Map<String, List<com.felipimatheuz.primehunt.data.remote.entity.PrimeComponentEntity>>,
        result: MutableMap<String, Int>
    ) {
        val parts = partsBySetMap[setId] ?: emptyList()
        val hasBlueprint = parts.any { it.id == setId }

        if (!hasBlueprint) {
            val comps = componentByPartMap[setId] ?: emptyList()
            if (comps.isNotEmpty()) {
                result[setId] = (result[setId] ?: 0) + multiplier
            }
        }

        parts.forEach { part ->
            if (part.part == PrimePartType.PRIME_SET) {
                resolveRequiredParts(part.id, multiplier * part.quantity, partsBySetMap, componentByPartMap, result)
            } else {
                result[part.id] = (result[part.id] ?: 0) + multiplier * part.quantity
            }
        }
    }

    suspend fun deleteGoal(id: Long) {
        val goal = goalDao.getById(id)
        goal?.let { goalDao.delete(it) }
    }

    suspend fun saveGoal(goal: GoalEntity) {
        goalDao.upsert(goal)
    }

    suspend fun completeGoal(id: Long) {
        goalDao.updateStatus(id, GoalStatus.COMPLETED, System.currentTimeMillis())
    }

    suspend fun saveTag(tag: GoalTagEntity) {
        goalTagDao.upsert(tag)
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}
