package com.felipimatheuz.primehunt.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.felipimatheuz.primehunt.data.remote.enums.RelicEra
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource

@Entity(
    tableName = "relic",
    indices = [
        Index(value = ["era"]),
        Index(value = ["source"])
    ]
)
data class RelicEntity(

    @PrimaryKey
    val id: String,

    val name: String,

    val era: RelicEra,

    val source: RelicSource
)