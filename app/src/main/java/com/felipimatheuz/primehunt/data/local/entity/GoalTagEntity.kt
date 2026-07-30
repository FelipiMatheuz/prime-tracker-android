package com.felipimatheuz.primehunt.data.local.entity

import androidx.compose.ui.graphics.Color
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
    val icon: String,
    val color: Color,
    val sortOrder: Int = 0
)