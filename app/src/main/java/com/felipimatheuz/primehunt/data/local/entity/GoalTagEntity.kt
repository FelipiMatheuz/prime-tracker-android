package com.felipimatheuz.primehunt.data.local.entity

import com.felipimatheuz.primehunt.data.local.enums.GoalIcons
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "goal_tag",
    indices = [
        Index("name", unique = true)
    ]
)
data class GoalTagEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: GoalIcons,
    val color: Int
)
