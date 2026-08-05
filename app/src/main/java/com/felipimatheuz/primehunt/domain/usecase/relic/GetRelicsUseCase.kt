package com.felipimatheuz.primehunt.domain.usecase.relic

import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.data.repository.PrimeDataStore
import com.felipimatheuz.primehunt.domain.mapper.PrimeMapper
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.domain.model.RelicComponentDomain
import com.felipimatheuz.primehunt.domain.model.RelicDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class GetRelicsUseCase @Inject constructor(
    private val primeDataStore: PrimeDataStore
) {

    operator fun invoke(): Flow<List<RelicDomain>> = combine(
        primeDataStore.baseData,
        primeDataStore.inventoryDao.observeInventory().distinctUntilChanged(),
        primeDataStore.goalDao.observeAllWithTags().distinctUntilChanged()
    ) { data, inventory, goals ->
        val inventoryMap = inventory.associate { it.primePartId to it.quantity }
        val partMap = data.parts.associateBy { it.id }
        val setMap = data.sets.associateBy { it.id }
        val componentMap = data.components.groupBy { it.relicId }
        val partsBySetMap = data.parts.groupBy { it.primeSetId }
        val goalsByTarget = goals.groupBy { it.goal.targetId }

        data.relics.map { relic ->
            val relicComponents = componentMap[relic.id]?.map { comp ->
                val part = partMap[comp.primePartId]
                val set = part?.let { setMap[it.primeSetId] } ?: setMap[comp.primePartId]

                var isForma = false
                var isBlueprint = false

                val name = when {
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

                val relevantGoals = mutableListOf<com.felipimatheuz.primehunt.data.local.entity.GoalWithTag>()
                goalsByTarget[comp.primePartId]?.filter { 
                    it.goal.targetType == com.felipimatheuz.primehunt.data.local.enums.GoalTargetType.PRIME_PART 
                }?.let { relevantGoals.addAll(it) }

                set?.let { s ->
                    goalsByTarget[s.id]?.filter { 
                        it.goal.targetType == com.felipimatheuz.primehunt.data.local.enums.GoalTargetType.PRIME_SET ||
                        (it.goal.targetType == com.felipimatheuz.primehunt.data.local.enums.GoalTargetType.PRIME_PART && part == null)
                    }?.let { relevantGoals.addAll(it) }
                }

                val goalTags = relevantGoals.distinctBy { it.tag.id }.map {
                    GoalTagDomain(it.tag.id, it.tag.name, it.tag.icon, it.tag.color)
                }

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
                it.goal.targetType == com.felipimatheuz.primehunt.data.local.enums.GoalTargetType.RELIC 
            }?.map {
                GoalTagDomain(it.tag.id, it.tag.name, it.tag.icon, it.tag.color)
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
}
