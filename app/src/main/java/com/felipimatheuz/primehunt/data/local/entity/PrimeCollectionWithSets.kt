package com.felipimatheuz.primehunt.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PrimeCollectionWithSets(
    @Embedded
    val collection: PrimeCollectionEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PrimeCollectionSetCrossRef::class,
            parentColumn = "collectionId",
            entityColumn = "primeSetId"
        )
    )
    val sets: List<PrimeSetEntity>
)
