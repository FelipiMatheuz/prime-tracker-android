package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.entity.InventoryPartEntity
import com.felipimatheuz.primehunt.data.remote.dao.PrimeComponentDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimePartDao
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrimeDetailRepository @Inject constructor(
    private val dataStore: PrimeDataStore,
    private val inventoryDao: InventoryDao,
    private val partDao: PrimePartDao,
    private val componentDao: PrimeComponentDao
) {

    fun observeSetDetails(setId: String): Flow<PrimeSetDomain?> = dataStore.allSets
        .map { allSets -> allSets.find { it.id == setId } }

    suspend fun updateInventory(partId: String, delta: Int) = withContext(Dispatchers.IO) {
        val current = inventoryDao.get(partId)
        val newQuantity = maxOf(0, (current?.quantity ?: 0) + delta)
        inventoryDao.upsert(InventoryPartEntity(partId, newQuantity))
    }

    suspend fun updateSetInventory(setId: String, delta: Int) = withContext(Dispatchers.IO) {
        val partsToUpdate = mutableMapOf<String, Int>()
        collectPartsRecursively(setId, 1, partsToUpdate)
        
        partsToUpdate.forEach { (partId, needed) ->
            updateInventory(partId, needed * delta)
        }
    }

    private suspend fun collectPartsRecursively(setId: String, multiplier: Int, result: MutableMap<String, Int>) {
        val parts = partDao.getByPrimeSetSync(setId)
        val hasBlueprint = parts.any { it.id == setId }
        
        if (!hasBlueprint) {
            val comps = componentDao.getByPrimePartSync(setId)
            if (comps.isNotEmpty()) {
                result[setId] = (result[setId] ?: 0) + multiplier
            }
        }
        
        parts.forEach { part ->
            if (part.part == PrimePartType.PRIME_SET) {
                collectPartsRecursively(part.id, multiplier * part.quantity, result)
            } else {
                result[part.id] = (result[part.id] ?: 0) + multiplier * part.quantity
            }
        }
    }
}
