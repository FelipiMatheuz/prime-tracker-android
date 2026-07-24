package com.felipimatheuz.primehunt.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.felipimatheuz.primehunt.data.local.enums.TrackingTargetType

@Entity(
    tableName = "tracking",
    foreignKeys = [

        ForeignKey(
            entity = TrackingCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )

    ],
    indices = [
        Index("categoryId"),
        Index("targetId")
    ]
)
data class TrackingEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val targetType: TrackingTargetType,

    val targetId: String,

    val desiredQuantity: Int = 1,

    val categoryId: Long,

    val note: String? = null,

    val priority: Int = 0,

    val createdAt: Long
)