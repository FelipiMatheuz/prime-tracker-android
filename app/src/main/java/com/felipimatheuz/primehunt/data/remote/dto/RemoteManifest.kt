package com.felipimatheuz.primehunt.data.remote.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RemoteManifest(
    @JsonProperty("generatedAt") val generatedAt: String,
    @JsonProperty("generatorVersion") val generatorVersion: String,
    @JsonProperty("files") val files: List<ManifestFile>
)

data class ManifestFile(
    @JsonProperty("name") val name: String,
    @JsonProperty("size") val size: Long,
    @JsonProperty("sha256") val sha256: String
)
