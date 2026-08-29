package com.felipimatheuz.primehunt.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "prime_collection_set",
    primaryKeys = [
        "collectionId",
        "primeSetId"
    ]
)
data class PrimeCollectionSetCrossRef(

    val collectionId: String,

    val primeSetId: String
)