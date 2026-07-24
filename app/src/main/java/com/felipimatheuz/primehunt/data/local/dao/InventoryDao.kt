package com.felipimatheuz.primehunt.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.local.entity.InventoryPartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {

    @Query("""
        SELECT *
        FROM inventory
    """)
    fun observeInventory(): Flow<List<InventoryPartEntity>>

    @Query("""
        SELECT *
        FROM inventory
        WHERE primePartId = :partId
    """)
    suspend fun get(partId: String): InventoryPartEntity?

    @Upsert
    suspend fun upsert(item: InventoryPartEntity)

    @Upsert
    suspend fun upsert(items: List<InventoryPartEntity>)

    @Query("""
        DELETE FROM inventory
        WHERE primePartId = :partId
    """)
    suspend fun remove(partId: String)
}