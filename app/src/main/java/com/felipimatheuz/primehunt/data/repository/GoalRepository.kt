package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.GoalTagDao
import com.felipimatheuz.primehunt.data.local.entity.GoalEntity
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    private val goalTagDao: GoalTagDao,
    private val primeDataStore: PrimeDataStore
) : GoalRepository {

    override suspend fun deleteGoal(id: Long) {
        val goal = goalDao.getById(id)
        goal?.let { goalDao.delete(it) }
    }

    override suspend fun saveGoal(
        targetId: String,
        targetType: GoalTargetType,
        desiredQuantity: Int,
        tagId: Long,
        note: String?,
        manualCurrentQuantity: Int?
    ) {
        goalDao.upsert(
            GoalEntity(
                targetId = targetId,
                targetType = targetType,
                desiredQuantity = desiredQuantity,
                currentQuantity = manualCurrentQuantity ?: 0,
                tagId = tagId,
                note = note,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun updateGoal(
        id: Long,
        desiredQuantity: Int,
        tagId: Long,
        note: String?,
        manualCurrentQuantity: Int?
    ) {
        val existing = goalDao.getById(id)
        existing?.let {
            goalDao.upsert(
                it.copy(
                    desiredQuantity = desiredQuantity,
                    tagId = tagId,
                    note = note,
                    currentQuantity = manualCurrentQuantity ?: it.currentQuantity
                )
            )
        }
    }

    override suspend fun completeGoal(id: Long) {
        goalDao.updateStatus(id, GoalStatus.COMPLETED, System.currentTimeMillis())
    }

    override suspend fun saveTag(tag: GoalTagDomain) {
        goalTagDao.upsert(
            GoalTagEntity(
                id = tag.id,
                name = tag.name,
                icon = tag.icon,
                color = tag.color
            )
        )
    }

    override fun observeAllWithTags(): Flow<List<GoalDomain>> = combine(
        goalDao.observeAllWithTags(),
        primeDataStore.inventoryDao.observeInventory(),
        primeDataStore.baseData
    ) { goals, inventory, data ->
        val inventoryMap = inventory.associate { it.primePartId to it.quantity }
        goals.map { mapToDomain(it, inventoryMap, data) }
    }

    override fun observeByIdWithTag(id: Long): Flow<GoalDomain?> = combine(
        goalDao.observeByIdWithTag(id),
        primeDataStore.inventoryDao.observeInventory(),
        primeDataStore.baseData
    ) { goalWithTag, inventory, data ->
        goalWithTag?.let {
            val inventoryMap = inventory.associate { it.primePartId to it.quantity }
            mapToDomain(it, inventoryMap, data)
        }
    }

    override fun observeAllTags(): Flow<List<GoalTagDomain>> = goalTagDao.observeAll().map { tags ->
        tags.map { GoalTagDomain(it.id, it.name, it.icon, it.color) }
    }

    override fun countByStatus(status: GoalStatus): Flow<Int> = goalDao.countByStatus(status)

    private fun mapToDomain(
        item: com.felipimatheuz.primehunt.data.local.entity.GoalWithTag,
        inventoryMap: Map<String, Int>,
        data: PrimeBaseData
    ): GoalDomain {
        // Logic moved from GetGoalsUseCase
        // ... (I'll fill this in properly in the next step or here if I have enough context)
        // I need to import PrimeMapper and PrimeSetResolver
        return com.felipimatheuz.primehunt.domain.mapper.GoalMapper.mapToDomain(
            item.goal,
            item.tag,
            inventoryMap,
            data
        )
    }
}
