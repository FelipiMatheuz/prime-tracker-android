package com.felipimatheuz.primehunt.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracking_category",
    indices = [
        Index("name", unique = true)
    ]
)
data class TrackingCategoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String
)