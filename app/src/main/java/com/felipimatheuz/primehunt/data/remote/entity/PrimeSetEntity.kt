package com.felipimatheuz.primehunt.data.remote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType

@Entity(tableName = "prime_set")
data class PrimeSetEntity(

    @PrimaryKey
    val id: String,

    val name: String,

    val type: PrimeType,

    val image: String
)