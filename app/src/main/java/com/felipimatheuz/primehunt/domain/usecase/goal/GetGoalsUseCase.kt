package com.felipimatheuz.primehunt.domain.usecase.goal

import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
import com.felipimatheuz.primehunt.data.repository.PrimeDataStore
import com.felipimatheuz.primehunt.domain.mapper.PrimeMapper
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.domain.model.TargetDomain
import com.felipimatheuz.primehunt.domain.util.PrimeSetResolver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGoalsUseCase @Inject constructor(
    private val primeDataStore: PrimeDataStore
) {

    operator fun invoke(): Flow<List<GoalDomain>> = combine(
        primeDataStore.goalDao.observeAllWithTags().distinctUntilChanged(),
        primeDataStore.inventoryDao.observeInventory().distinctUntilChanged(),
        primeDataStore.baseData.distinctUntilChanged()
    ) { goals, inventory, data ->
        val inventoryMap = inventory.associate { it.primePartId to it.quantity }
        val setMap = data.sets.associateBy { it.id }
        val partMap = data.parts.associateBy { it.id }
        val relicMap = data.relics.associateBy { it.id }
        val partsBySetMap = data.parts.groupBy { it.primeSetId }
        val componentByPartMap = data.components.groupBy { it.primePartId }

        goals.map { item ->
            val goal = item.goal
            val tag = item.tag

            val (name, current) = when (goal.targetType) {
                GoalTargetType.PRIME_SET -> {
                    val required = PrimeSetResolver.resolveRequiredParts(
                        goal.targetId,
                        1,
                        partsBySetMap,
                        componentByPartMap
                    )

                    val completedSets = if (required.isEmpty()) 0 else {
                        required.map { (partId, needed) ->
                            (inventoryMap[partId] ?: 0) / (needed.takeIf { it > 0 } ?: 1)
                        }.minOrNull() ?: 0
                    }

                    (setMap[goal.targetId]?.name ?: "Unknown Set") to completedSets
                }

                GoalTargetType.PRIME_PART -> {
                    val part = partMap[goal.targetId]
                    val setName = part?.let { setMap[it.primeSetId]?.name } ?: ""
                    
                    val displayName = if (setName.isNotEmpty()) {
                        PrimeMapper.formatPartName(setName, part!!.part)
                    } else {
                        val set = setMap[goal.targetId]
                        if (set != null) PrimeMapper.getBlueprintName(set.name) else "Unknown Part"
                    }

                    displayName to (inventoryMap[goal.targetId] ?: 0)
                }

                GoalTargetType.RELIC -> {
                    val relic = relicMap[goal.targetId]
                    val relicName = relic?.let { "${it.era.name} ${it.name}" } ?: "Unknown Relic"
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
    }

    fun observeGoal(id: Long): Flow<GoalDomain?> = combine(
        primeDataStore.goalDao.observeByIdWithTag(id).distinctUntilChanged(),
        primeDataStore.inventoryDao.observeInventory().distinctUntilChanged(),
        primeDataStore.baseData.distinctUntilChanged()
    ) { item, inventory, data ->
        if (item == null) return@combine null

        val inventoryMap = inventory.associate { it.primePartId to it.quantity }
        val goal = item.goal
        val tag = item.tag

        val setMap = data.sets.associateBy { it.id }
        val partMap = data.parts.associateBy { it.id }
        val relicMap = data.relics.associateBy { it.id }
        val partsBySetMap = data.parts.groupBy { it.primeSetId }
        val componentByPartMap = data.components.groupBy { it.primePartId }

        val (name, current) = when (goal.targetType) {
            GoalTargetType.PRIME_SET -> {
                val required = PrimeSetResolver.resolveRequiredParts(
                    goal.targetId,
                    1,
                    partsBySetMap,
                    componentByPartMap
                )

                val completedSets = if (required.isEmpty()) 0 else {
                    required.map { (partId, needed) ->
                        (inventoryMap[partId] ?: 0) / (needed.takeIf { it > 0 } ?: 1)
                    }.minOrNull() ?: 0
                }

                (setMap[goal.targetId]?.name ?: "Unknown Set") to completedSets
            }

            GoalTargetType.PRIME_PART -> {
                val part = partMap[goal.targetId]
                val setName = part?.let { setMap[it.primeSetId]?.name } ?: ""

                val displayName = if (setName.isNotEmpty()) {
                    PrimeMapper.formatPartName(setName, part!!.part)
                } else {
                    val set = setMap[goal.targetId]
                    if (set != null) PrimeMapper.getBlueprintName(set.name) else "Unknown Part"
                }

                displayName to (inventoryMap[goal.targetId] ?: 0)
            }

            GoalTargetType.RELIC -> {
                val relic = relicMap[goal.targetId]
                val relicName = relic?.let { "${it.era.name} ${it.name}" } ?: "Unknown Relic"
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

    fun observeAllTargets(): Flow<List<TargetDomain>> {
        return primeDataStore.baseData.map { data ->
            val setMap = data.sets.associateBy { it.id }
            val componentByPartMap = data.components.groupBy { it.primePartId }

            val setTargets = data.sets.map {
                TargetDomain(it.id, it.name, GoalTargetType.PRIME_SET)
            }

            val synthesizedBlueprints = data.sets.filter { set ->
                val hasExplicitBlueprint = data.parts.any { it.id == set.id && it.primeSetId == set.id }
                !hasExplicitBlueprint && componentByPartMap[set.id]?.isNotEmpty() == true
            }.map { set ->
                TargetDomain(
                    set.id,
                    PrimeMapper.getBlueprintName(set.name),
                    GoalTargetType.PRIME_PART
                )
            }

            val partTargets = data.parts.map { part ->
                val setName = setMap[part.primeSetId]?.name ?: ""
                TargetDomain(part.id, PrimeMapper.formatPartName(setName, part.part), GoalTargetType.PRIME_PART)
            }

            val relicTargets = data.relics.map {
                TargetDomain(it.id, "${it.era.name} ${it.name}", GoalTargetType.RELIC)
            }
            
            val formaTargets = listOf(
                TargetDomain("forma_blueprint", "Forma Blueprint", GoalTargetType.FORMA)
            )

            setTargets + synthesizedBlueprints + partTargets + relicTargets + formaTargets
        }
    }
}
