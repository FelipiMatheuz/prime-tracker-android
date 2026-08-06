package com.felipimatheuz.primehunt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType

@Entity(tableName = "prime_part")
data class PrimePartEntity(

    @PrimaryKey
    val id: String,

    val primeSetId: String,

    val part: PrimePartType,

    val quantity: Int
)