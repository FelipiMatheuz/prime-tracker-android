package com.felipimatheuz.primehunt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory"
)
data class InventoryPartEntity(

    @PrimaryKey
    val primePartId: String,
    val quantity: Int
)