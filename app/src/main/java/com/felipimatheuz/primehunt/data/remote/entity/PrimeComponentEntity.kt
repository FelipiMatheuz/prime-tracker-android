package com.felipimatheuz.primehunt.data.remote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.felipimatheuz.primehunt.data.remote.enums.DropRarity

@Entity(tableName = "prime_component")
data class PrimeComponentEntity(

    @PrimaryKey
    val id: String,

    val relicId: String,

    val primePartId: String,

    val rarity: DropRarity
)