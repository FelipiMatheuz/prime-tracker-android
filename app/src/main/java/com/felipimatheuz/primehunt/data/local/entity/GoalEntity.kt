package com.felipimatheuz.primehunt.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.felipimatheuz.primehunt.domain.model.enums.GoalStatus
import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType

@Entity(
    tableName = "goal",
    foreignKeys = [

        ForeignKey(
            entity = GoalTagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.RESTRICT
        )

    ],
    indices = [
        Index("tagId"),
        Index("targetId")
    ]
)
data class GoalEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetType: GoalTargetType,
    val targetId: String,
    val currentQuantity: Int = 0,
    val desiredQuantity: Int,
    val tagId: Long,
    val status: GoalStatus = GoalStatus.ACTIVE,
    val note: String? = null,
    val createdAt: Long,
    val completedAt: Long? = null
)