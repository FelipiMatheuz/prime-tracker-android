package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
import com.felipimatheuz.primehunt.data.remote.dao.PrimePartDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimeSetDao
import com.felipimatheuz.primehunt.data.remote.dao.RelicDao
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao,
    private val inventoryDao: InventoryDao,
    private val setDao: PrimeSetDao,
    private val partDao: PrimePartDao,
    private val relicDao: RelicDao
) {

    fun observeGoals(): Flow<List<GoalDomain>> {
        return combine(
            goalDao.observeAllWithTags().distinctUntilChanged(),
            inventoryDao.observeInventory().distinctUntilChanged(),
            setDao.getAll().distinctUntilChanged(),
            partDao.getAll().distinctUntilChanged(),
            relicDao.getAll().distinctUntilChanged()
        ) { goals, inventory, sets, parts, relics ->
            val inventoryMap = inventory.associate { it.primePartId to it.quantity }
            val setMap = sets.associateBy { it.id }
            val partMap = parts.associateBy { it.id }
            val relicMap = relics.associateBy { it.id }

            goals.map { item ->
                val goal = item.goal
                val tag = item.tag

                val (name, current) = when (goal.targetType) {
                    GoalTargetType.PRIME_SET -> {
                        val set = setMap[goal.targetId]
                        (set?.name ?: "Unknown Set") to (inventoryMap[goal.targetId] ?: 0)
                    }
                    GoalTargetType.PRIME_PART -> {
                        val part = partMap[goal.targetId]
                        val setName = part?.let { setMap[it.primeSetId]?.name } ?: ""
                        val partName = part?.part?.name?.replace("_", " ")?.lowercase()?.capitalizeWords() ?: ""
                        "$setName $partName".trim() to (inventoryMap[goal.targetId] ?: 0)
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
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}
