package com.felipimatheuz.primehunt.domain.usecase.cloud

import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.GoalTagDao
import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.cloud.Firestore
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudActionResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UploadBackupUseCase @Inject constructor(
    private val firestore: Firestore,
    private val inventoryDao: InventoryDao,
    private val goalDao: GoalDao,
    private val tagDao: GoalTagDao
) {
    suspend operator fun invoke(userId: String): CloudActionResult {
        val inventory = inventoryDao.observeInventory().first().associate { it.primePartId to it.quantity }
        val goals = goalDao.observeAll().first().map { goal ->
            mapOf(
                "targetType" to goal.targetType.name,
                "targetId" to goal.targetId,
                "currentQuantity" to goal.currentQuantity,
                "desiredQuantity" to goal.desiredQuantity,
                "tagId" to goal.tagId,
                "status" to goal.status.name,
                "note" to goal.note,
                "createdAt" to goal.createdAt,
                "completedAt" to goal.completedAt
            )
        }
        val tags = tagDao.observeAll().first().map { tag ->
            mapOf(
                "id" to tag.id,
                "name" to tag.name,
                "icon" to tag.icon.name,
                "color" to tag.color
            )
        }
        return firestore.uploadBackup(userId, inventory, goals, tags)
    }
}
