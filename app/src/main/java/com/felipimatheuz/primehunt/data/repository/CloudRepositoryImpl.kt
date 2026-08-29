package com.felipimatheuz.primehunt.data.repository

import androidx.room.withTransaction
import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.GoalTagDao
import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.entity.GoalEntity
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.local.entity.InventoryPartEntity
import com.felipimatheuz.primehunt.data.local.AppDatabase
import com.felipimatheuz.primehunt.domain.model.enums.GoalIcons
import com.felipimatheuz.primehunt.domain.model.enums.GoalStatus
import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.enums.PrimePartType
import com.felipimatheuz.primehunt.domain.util.LegacyMigrationParser
import com.felipimatheuz.primehunt.domain.repository.CloudRepository
import com.felipimatheuz.primehunt.domain.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val inventoryDao: InventoryDao,
    private val goalDao: GoalDao,
    private val tagDao: GoalTagDao,
    private val inventoryRepository: InventoryRepository,
    private val parser: LegacyMigrationParser
) : CloudRepository {

    override suspend fun performMigration(
        mapSet: Map<String, Any>,
        mapOther: Map<String, Any>
    ): CloudRepository.MigrationResult = withContext(Dispatchers.IO) {

        val legacyData = parser.parse(mapSet, isSetCategory = true) +
                parser.parse(mapOther, isSetCategory = false)

        if (legacyData.isEmpty()) return@withContext CloudRepository.MigrationResult(0, emptyList())

        val matchedLegacyKeys = mutableSetOf<String>()

        val totalInserted = database.withTransaction {
            val allParts = inventoryRepository.getAllPartsSync()
            val allSets = inventoryRepository.getAllSetsSync()
            val setMap = allSets.associateBy { it.id }

            val inventoryToInsert = mutableListOf<InventoryPartEntity>()

            allSets.forEach { set ->
                val normalizedSetName = parser.normalizeNewName(set.name)
                val legacyKey = "${normalizedSetName}_BLUEPRINT"
                legacyData[legacyKey]?.let { qty ->
                    inventoryToInsert.add(InventoryPartEntity(set.id, qty))
                    matchedLegacyKeys.add(legacyKey)
                }
            }

            allParts.forEach { part ->
                val set = setMap[part.primeSetId] ?: return@forEach
                val normalizedSetName = parser.normalizeNewName(set.name)

                val legacyKey = "${normalizedSetName}_${part.part.name}"
                legacyData[legacyKey]?.let { qty ->
                    inventoryToInsert.add(InventoryPartEntity(part.id, qty))
                    matchedLegacyKeys.add(legacyKey)
                }

                if (part.part == PrimePartType.PRIME_SET) {
                    val nestedLegacyKey = "${normalizedSetName}_${parser.normalizePrimeSetId(part.id)}"
                    legacyData[nestedLegacyKey]?.let { qty ->
                        inventoryToInsert.add(InventoryPartEntity(part.id, qty))
                        val subParts = inventoryRepository.getPartsBySetSync(part.id)
                        subParts.forEach { sub ->
                            inventoryToInsert.add(InventoryPartEntity(sub.id, qty))
                        }
                        matchedLegacyKeys.add(nestedLegacyKey)
                    }
                }
            }

            val ignoredKeys = legacyData.keys.filter { it !in matchedLegacyKeys }
            if (inventoryToInsert.isNotEmpty() && ignoredKeys.isEmpty()) {
                val consolidated = inventoryToInsert
                    .groupBy { it.primePartId }
                    .map { (id, list) -> InventoryPartEntity(id, list.sumOf { it.quantity }) }

                inventoryDao.upsert(consolidated)
                consolidated.size
            } else {
                0
            }
        }

        val ignoredKeys = legacyData.keys.filter { it !in matchedLegacyKeys }
        CloudRepository.MigrationResult(totalInserted, ignoredKeys)
    }

    override suspend fun restoreBackup(
        inventoryMap: Map<String, Int>,
        goalsList: List<Map<String, Any?>>,
        tagsList: List<Map<String, Any?>>
    ) = withContext(Dispatchers.IO) {
        database.withTransaction {
            inventoryDao.clearAll()
            goalDao.clearAll()
            tagDao.clearAll()

            val tagEntities = tagsList.map { map ->
                GoalTagEntity(
                    id = (map["id"] as Number).toLong(),
                    name = map["name"] as String,
                    icon = GoalIcons.valueOf(map["icon"] as String),
                    color = (map["color"] as Number).toInt()
                )
            }
            tagDao.upsert(tagEntities)

            val inventoryEntities = inventoryMap.map { (id, qty) -> InventoryPartEntity(id, qty) }
            inventoryDao.upsert(inventoryEntities)

            val goalEntities = goalsList.map { map ->
                GoalEntity(
                    targetType = GoalTargetType.valueOf(map["targetType"] as String),
                    targetId = map["targetId"] as String,
                    currentQuantity = (map["currentQuantity"] as Number).toInt(),
                    desiredQuantity = (map["desiredQuantity"] as Number).toInt(),
                    tagId = (map["tagId"] as Number).toLong(),
                    status = GoalStatus.valueOf(map["status"] as String),
                    note = map["note"] as? String,
                    createdAt = (map["createdAt"] as Number).toLong(),
                    completedAt = (map["completedAt"] as? Number)?.toLong()
                )
            }
            goalDao.upsert(goalEntities)
        }
    }
}
