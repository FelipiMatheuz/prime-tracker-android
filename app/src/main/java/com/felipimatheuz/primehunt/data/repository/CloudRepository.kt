package com.felipimatheuz.primehunt.data.repository

import androidx.room.withTransaction
import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.entity.InventoryPartEntity
import com.felipimatheuz.primehunt.data.local.AppDatabase
import com.felipimatheuz.primehunt.data.local.dao.PrimePartDao
import com.felipimatheuz.primehunt.data.local.dao.PrimeSetDao
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.domain.mapper.LegacyMigrationParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudRepository @Inject constructor(
    private val database: AppDatabase,
    private val inventoryDao: InventoryDao,
    private val primePartDao: PrimePartDao,
    private val primeSetDao: PrimeSetDao,
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
            val allParts = primePartDao.getAllSync()
            val allSets = primeSetDao.getAllSync()
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
                        val subParts = primePartDao.getByPrimeSetSync(part.id)
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

    suspend fun restoreInventory(inventoryMap: Map<String, Int>) = withContext(Dispatchers.IO) {
        database.withTransaction {
            val inventoryEntities = inventoryMap.map { (id, qty) -> InventoryPartEntity(id, qty) }
            inventoryDao.upsert(inventoryEntities)
        }
    }
}
