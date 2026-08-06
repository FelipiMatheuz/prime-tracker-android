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
        primeDataStore.baseData
    ) { goals, inventory, data ->
        val inventoryMap = inventory.associate { it.primePartId to it.quantity }
        val partsBySetMap = data.parts.groupBy { it.primeSetId }.mapValues { entry ->
            entry.value.map { PrimeSetResolver.ResolvePart(it.id, it.part, it.quantity) }
        }
        val hasComponentsMap = data.components.associate { it.primePartId to true }
        val setMap = data.sets.associateBy { it.id }
        val partMap = data.parts.associateBy { it.id }
        val relicMap = data.relics.associateBy { it.id }

        goals.map { item ->
            mapToDomain(
                item.goal,
                item.tag,
                inventoryMap,
                partsBySetMap,
                hasComponentsMap,
                setMap,
                partMap,
                relicMap
            )
        }
    }

    fun observeGoal(id: Long): Flow<GoalDomain?> = combine(
        primeDataStore.goalDao.observeByIdWithTag(id).distinctUntilChanged(),
        primeDataStore.inventoryDao.observeInventory().distinctUntilChanged(),
        primeDataStore.baseData
    ) { itemWithTag, inventory, data ->
        val item = itemWithTag ?: return@combine null
        val inventoryMap = inventory.associate { it.primePartId to it.quantity }
        val partsBySetMap = data.parts.groupBy { it.primeSetId }.mapValues { entry ->
            entry.value.map { PrimeSetResolver.ResolvePart(it.id, it.part, it.quantity) }
        }
        val hasComponentsMap = data.components.associate { it.primePartId to true }
        val setMap = data.sets.associateBy { it.id }
        val partMap = data.parts.associateBy { it.id }
        val relicMap = data.relics.associateBy { it.id }

        mapToDomain(
            item.goal,
            item.tag,
            inventoryMap,
            partsBySetMap,
            hasComponentsMap,
            setMap,
            partMap,
            relicMap
        )
    }

    private fun mapToDomain(
        goal: com.felipimatheuz.primehunt.data.local.entity.GoalEntity,
        tag: com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity,
        inventoryMap: Map<String, Int>,
        partsBySetMap: Map<String, List<PrimeSetResolver.ResolvePart>>,
        hasComponentsMap: Map<String, Boolean>,
        setMap: Map<String, com.felipimatheuz.primehunt.data.local.entity.PrimeSetEntity>,
        partMap: Map<String, com.felipimatheuz.primehunt.data.local.entity.PrimePartEntity>,
        relicMap: Map<String, com.felipimatheuz.primehunt.data.local.entity.RelicEntity>
    ): GoalDomain {
        val (name, current) = when (goal.targetType) {
            GoalTargetType.PRIME_SET -> {
                val required = PrimeSetResolver.resolveRequiredParts(
                    goal.targetId,
                    1,
                    partsBySetMap,
                    hasComponentsMap
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

        return GoalDomain(
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

    fun observeAllTargets(): Flow<List<TargetDomain>> = primeDataStore.baseData.map { data ->
        val sets = data.sets
        val parts = data.parts
        val relics = data.relics
        val componentByPartMap = data.components.groupBy { it.primePartId }
        
        val blueprintIds = parts.filter { it.id == it.primeSetId }.map { it.id }.toSet()
        val setMap = sets.associateBy { it.id }

        val setTargets = sets.map {
            TargetDomain(it.id, it.name, GoalTargetType.PRIME_SET)
        }

        val synthesizedBlueprints = sets.filter { set ->
            val hasExplicitBlueprint = blueprintIds.contains(set.id)
            !hasExplicitBlueprint && componentByPartMap[set.id]?.isNotEmpty() == true
        }.map { set ->
            TargetDomain(
                set.id,
                PrimeMapper.getBlueprintName(set.name),
                GoalTargetType.PRIME_PART
            )
        }

        val partTargets = parts.map { part ->
            val setName = setMap[part.primeSetId]?.name ?: ""
            TargetDomain(part.id, PrimeMapper.formatPartName(setName, part.part), GoalTargetType.PRIME_PART)
        }

        val relicTargets = relics.map {
            TargetDomain(it.id, "${it.era.name} ${it.name}", GoalTargetType.RELIC)
        }
        
        val formaTargets = listOf(
            TargetDomain("forma_blueprint", "Forma Blueprint", GoalTargetType.FORMA)
        )

        setTargets + synthesizedBlueprints + partTargets + relicTargets + formaTargets
    }
}
