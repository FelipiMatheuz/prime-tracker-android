package com.felipimatheuz.primehunt.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteManifest(
    @SerialName("generatedAt") val generatedAt: String,
    @SerialName("generatorVersion") val generatorVersion: String,
    @SerialName("files") val files: List<ManifestFile>
)

@Serializable
data class ManifestFile(
    @SerialName("name") val name: String,
    @SerialName("size") val size: Long,
    @SerialName("sha256") val sha256: String
)
