package com.felipimatheuz.primehunt.data.remote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prime_collection")
data class PrimeCollectionEntity(

    @PrimaryKey
    val id: String,

    val name: String,

    val promoImage: String,

    val released: Int
)