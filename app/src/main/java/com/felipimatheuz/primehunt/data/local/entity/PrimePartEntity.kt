package com.felipimatheuz.primehunt.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.felipimatheuz.primehunt.domain.model.enums.PrimePartType

@Entity(
    tableName = "prime_part",
    indices = [
        Index(value = ["primeSetId"])
    ]
)
data class PrimePartEntity(

    @PrimaryKey
    val id: String,

    val primeSetId: String,

    val part: PrimePartType,

    val quantity: Int
)