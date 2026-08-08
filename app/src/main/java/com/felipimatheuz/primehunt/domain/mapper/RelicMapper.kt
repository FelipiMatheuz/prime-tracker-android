package com.felipimatheuz.primehunt.domain.mapper

import com.felipimatheuz.primehunt.data.local.entity.PrimePartEntity
import com.felipimatheuz.primehunt.data.local.entity.PrimeSetEntity
import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.enums.PrimePartType
import com.felipimatheuz.primehunt.domain.model.PrimeBaseData
import com.felipimatheuz.primehunt.domain.model.*
import com.felipimatheuz.primehunt.domain.util.StringFormatter

object RelicMapper {

    fun mapToDomain(
        data: PrimeBaseData,
        inventoryMap: Map<String, Int>,
        goalsByTarget: Map<String, List<GoalDomain>>
    ): List<RelicDomain> {
        val partMap = data.parts.associateBy { it.id }
        val setMap = data.sets.associateBy { it.id }
        val componentMap = data.components.groupBy { it.relicId }
        val partsBySetMap = data.parts.groupBy { it.primeSetId }

        return data.relics.map { relic ->
            val relicComponents = componentMap[relic.id]?.map { comp ->
                val part = partMap[comp.primePartId]
                val set = part?.let { setMap[it.primeSetId] } ?: setMap[comp.primePartId]

                val nameResult = resolveComponentName(part, set, setMap)
                val owned = part?.let { inventoryMap[it.id] ?: 0 } ?: inventoryMap[comp.primePartId] ?: 0
                val needed = part?.quantity ?: 1
                
                val compositeInfo = resolveCompositeInfo(part, partsBySetMap, setMap)
                val goalTags = resolveGoalTags(comp.primePartId, part, set, goalsByTarget)

                RelicComponentDomain(
                    name = nameResult.name,
                    rarity = comp.rarity,
                    isObtained = owned >= needed,
                    neededQuantity = needed,
                    ownedQuantity = owned,
                    compositeInfo = compositeInfo,
                    isForma = nameResult.isForma,
                    isBlueprint = nameResult.isBlueprint,
                    goalTags = goalTags
                )
            } ?: emptyList()

            val relicTags = goalsByTarget[relic.id]?.filter { 
                it.targetType == GoalTargetType.RELIC 
            }?.map { it.tag }?.distinctBy { it.id } ?: emptyList()

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

    private data class NameResult(val name: String, val isForma: Boolean, val isBlueprint: Boolean)

    private fun resolveComponentName(
        part: PrimePartEntity?,
        set: PrimeSetEntity?,
        setMap: Map<String, PrimeSetEntity>
    ): NameResult {
        return when {
            part != null && part.part == PrimePartType.PRIME_SET -> {
                NameResult(setMap[part.id]?.name ?: part.id, false, true)
            }
            part != null && set != null -> {
                NameResult(StringFormatter.formatPartName(set.name, part.part), false, false)
            }
            set != null -> {
                NameResult(set.name, false, true)
            }
            else -> {
                NameResult("", true, false)
            }
        }
    }

    private fun resolveCompositeInfo(
        part: PrimePartEntity?,
        partsBySetMap: Map<String, List<PrimePartEntity>>,
        setMap: Map<String, PrimeSetEntity>
    ): String? {
        return part?.let { p ->
            partsBySetMap[p.primeSetId]?.find { it.part == PrimePartType.PRIME_SET }
                ?.let { dep ->
                    setMap[dep.id]?.let { depSet ->
                        "${depSet.name} ×${dep.quantity}"
                    }
                }
        }
    }

    private fun resolveGoalTags(
        primePartId: String,
        part: PrimePartEntity?,
        set: PrimeSetEntity?,
        goalsByTarget: Map<String, List<GoalDomain>>
    ): List<GoalTagDomain> {
        val relevantGoals = mutableListOf<GoalDomain>()
        
        goalsByTarget[primePartId]?.filter { 
            it.targetType == GoalTargetType.PRIME_PART 
        }?.let { relevantGoals.addAll(it) }

        set?.let { s ->
            goalsByTarget[s.id]?.filter { 
                it.targetType == GoalTargetType.PRIME_SET ||
                (it.targetType == GoalTargetType.PRIME_PART && part == null)
            }?.let { relevantGoals.addAll(it) }
        }

        return relevantGoals.map { it.tag }.distinctBy { it.id }
    }
}
