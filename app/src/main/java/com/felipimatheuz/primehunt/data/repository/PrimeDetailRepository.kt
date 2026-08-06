package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.entity.InventoryPartEntity
import com.felipimatheuz.primehunt.domain.repository.PrimeRepository
import com.felipimatheuz.primehunt.domain.util.PrimeSetResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrimeDetailRepository @Inject constructor(
    private val inventoryDao: InventoryDao,
    private val primeRepository: PrimeRepository
) {

    suspend fun updateInventory(partId: String, delta: Int) = withContext(Dispatchers.IO) {
        val current = inventoryDao.get(partId)
        val newQuantity = maxOf(0, (current?.quantity ?: 0) + delta)
        inventoryDao.upsert(InventoryPartEntity(partId, newQuantity))
    }

    suspend fun updateSetInventory(setId: String, delta: Int) = withContext(Dispatchers.IO) {
        // Here we still use the DAOs to get the snapshot for the update operation.
        // We could also get it from a UseCase, but for a write operation, 
        // fetching from DB is fine as long as we use the unified Resolver.
        val parts = primeRepository.getAllPartsSync()
        val components = primeRepository.getAllComponentsSync()
        
        val partsBySetMap = parts.groupBy { it.primeSetId }.mapValues { entry ->
            entry.value.map { PrimeSetResolver.ResolvePart(it.id, it.part, it.quantity) }
        }
        val hasComponentsMap = components.associate { it.primePartId to true }
        
        val partsToUpdate = mutableMapOf<String, Int>()
        PrimeSetResolver.resolveRequiredParts(setId, 1, partsBySetMap, hasComponentsMap, partsToUpdate)
        
        partsToUpdate.forEach { (partId, needed) ->
            updateInventory(partId, needed * delta)
        }
    }
}
