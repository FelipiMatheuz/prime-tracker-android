package com.felipimatheuz.primehunt.data.repository

import androidx.room.withTransaction
import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.GoalTagDao
import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.entity.GoalEntity
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.local.entity.InventoryPartEntity
import com.felipimatheuz.primehunt.data.local.AppDatabase
import com.felipimatheuz.primehunt.data.local.enums.GoalIcons
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.domain.mapper.LegacyMigrationParser
import com.felipimatheuz.primehunt.domain.repository.PrimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudRepository @Inject constructor(
    private val database: AppDatabase,
    private val inventoryDao: InventoryDao,
    private val goalDao: GoalDao,
    private val tagDao: GoalTagDao,
    private val primeRepository: PrimeRepository,
    private val parser: LegacyMigrationParser
) {

    data class MigrationResult(
        val itemsInserted: Int,
        val ignoredKeys: List<String>
    )

    suspend fun performMigration(
        mapSet: Map<String, Any>,
        mapOther: Map<String, Any>
    ): MigrationResult = withContext(Dispatchers.IO) {
        
        val legacyData = parser.parse(mapSet, isSetCategory = true) + 
                         parser.parse(mapOther, isSetCategory = false)

        if (legacyData.isEmpty()) return@withContext MigrationResult(0, emptyList())

        val matchedLegacyKeys = mutableSetOf<String>()

        val totalInserted = database.withTransaction {
            val allParts = primeRepository.getAllPartsSync()
            val allSets = primeRepository.getAllSetsSync()
            val setMap = allSets.associateBy { it.id }

            val inventoryToInsert = mutableListOf<InventoryPartEntity>()

            // 1. Processamento de Match
            allParts.forEach { part ->
                val set = setMap[part.primeSetId] ?: return@forEach
                val normalizedSetName = parser.normalizeNewName(set.name)
                
                // Tenta encontrar o item no legado
                val legacyKey = if (part.part == PrimePartType.BLUEPRINT) {
                    "${normalizedSetName}_BLUEPRINT"
                } else {
                    "${normalizedSetName}_${part.part.name}"
                }

                legacyData[legacyKey]?.let { qty ->
                    inventoryToInsert.add(InventoryPartEntity(part.id, qty))
                    matchedLegacyKeys.add(legacyKey)
                }

                // 2. Casos Especiais: Partes Aninhadas (ex: Aklex_LEX)
                if (part.part == PrimePartType.PRIME_SET) {
                    val nestedLegacyKey = "${normalizedSetName}_${parser.normalizeNewName(part.id)}"
                    legacyData[nestedLegacyKey]?.let { qty ->
                        val subParts = primeRepository.getPartsBySetSync(part.id)
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
        MigrationResult(totalInserted, ignoredKeys)
    }

    suspend fun restoreBackup(
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
