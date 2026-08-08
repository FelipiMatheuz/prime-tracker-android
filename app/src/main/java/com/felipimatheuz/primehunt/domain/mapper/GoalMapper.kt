package com.felipimatheuz.primehunt.domain.mapper

import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.data.repository.PrimeBaseData
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.domain.util.PrimeSetResolver

object GoalMapper {

    fun mapToDomain(
        goal: com.felipimatheuz.primehunt.data.local.entity.GoalEntity,
        tag: com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity,
        inventoryMap: Map<String, Int>,
        data: PrimeBaseData
    ): GoalDomain {
        val partsBySetMap = data.parts.groupBy { it.primeSetId }.mapValues { entry ->
            entry.value.map { PrimeSetResolver.ResolvePart(it.id, it.part, it.quantity) }
        }
        val hasComponentsMap = data.components.associate { it.primePartId to true }
        val setMap = data.sets.associateBy { it.id }
        val partMap = data.parts.associateBy { it.id }
        val relicMap = data.relics.associateBy { it.id }

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
}
