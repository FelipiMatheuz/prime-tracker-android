package com.felipimatheuz.primehunt.domain.model

data class ManifestDomain(
    val lastSync: Long,
    val collectionsHash: String?,
    val primeSetsHash: String?,
    val relicsHash: String?
)
