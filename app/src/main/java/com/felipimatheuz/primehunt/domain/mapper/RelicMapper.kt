package com.felipimatheuz.primehunt.domain.mapper

import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.enums.PrimePartType
import com.felipimatheuz.primehunt.data.repository.PrimeBaseData
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.RelicComponentDomain
import com.felipimatheuz.primehunt.domain.model.RelicDomain

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

                var isForma = false
                var isBlueprint = false
                val name = when {
                    part != null && part.part == PrimePartType.PRIME_SET -> {
                        isBlueprint = true
                        setMap[part.id]?.name ?: part.id
                    }
                    part != null && set != null -> PrimeMapper.formatPartName(set.name, part.part)
                    set != null -> {
                        isBlueprint = true
                        set.name
                    }
                    else -> {
                        isForma = true
                        ""
                    }
                }

                val owned = part?.let { inventoryMap[it.id] ?: 0 } ?: inventoryMap[comp.primePartId] ?: 0
                val needed = part?.quantity ?: 1
                
                val compositeInfo = part?.let { p ->
                    partsBySetMap[p.primeSetId]?.find { it.part == PrimePartType.PRIME_SET }
                        ?.let { dep ->
                            setMap[dep.id]?.let { depSet ->
                                "${depSet.name} ×${dep.quantity}"
                            }
                        }
                }

                val relevantGoals = mutableListOf<GoalDomain>()
                goalsByTarget[comp.primePartId]?.filter { 
                    it.targetType == GoalTargetType.PRIME_PART 
                }?.let { relevantGoals.addAll(it) }

                set?.let { s ->
                    goalsByTarget[s.id]?.filter { 
                        it.targetType == GoalTargetType.PRIME_SET ||
                        (it.targetType == GoalTargetType.PRIME_PART && part == null)
                    }?.let { relevantGoals.addAll(it) }
                }

                val goalTags = relevantGoals.map { it.tag }.distinctBy { it.id }

                RelicComponentDomain(
                    name = name,
                    rarity = comp.rarity,
                    isObtained = owned >= needed,
                    neededQuantity = needed,
                    ownedQuantity = owned,
                    compositeInfo = compositeInfo,
                    isForma = isForma,
                    isBlueprint = isBlueprint,
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
}
