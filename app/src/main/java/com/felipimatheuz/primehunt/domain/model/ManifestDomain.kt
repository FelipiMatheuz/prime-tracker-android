package com.felipimatheuz.primehunt.domain.model

data class ManifestDomain(
    val lastSync: Long,
    val collectionsHash: String?,
    val primeSetsHash: String?,
    val relicsHash: String?,
    val isRelicsValid: Boolean = true,
    val isSetsValid: Boolean = true,
    val isCollectionsValid: Boolean = true
)
