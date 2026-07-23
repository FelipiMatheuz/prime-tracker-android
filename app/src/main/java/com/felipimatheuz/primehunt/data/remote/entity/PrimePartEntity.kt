package com.felipimatheuz.primehunt.data.remote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prime_part")
data class PrimePartEntity(

    @PrimaryKey
    val id: String,

    val primeSetId: String,

    val part: PrimePartType,

    val quantity: Int
)