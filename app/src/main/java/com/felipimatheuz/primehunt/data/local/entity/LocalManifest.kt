package com.felipimatheuz.primehunt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manifest")
data class LocalManifest(
    @PrimaryKey val id: Int = 1,
    val lastSync: Long,
    val collectionsHash: String?,
    val primeSetsHash: String?,
    val relicsHash: String?
)
