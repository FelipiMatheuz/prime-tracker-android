package com.felipimatheuz.primehunt.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class GoalWithTag(
    @Embedded
    val goal: GoalEntity,

    @Relation(
        parentColumn = "tagId",
        entityColumn = "id"
    )
    val tag: GoalTagEntity
)
