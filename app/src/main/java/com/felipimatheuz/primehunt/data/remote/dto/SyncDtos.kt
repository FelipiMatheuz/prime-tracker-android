package com.felipimatheuz.primehunt.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelicDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("era") val era: String,
    @SerialName("source") val source: String,
    @SerialName("drops") val drops: List<RelicDropDto>
)

@Serializable
data class RelicDropDto(
    @SerialName("id") val id: String,
    @SerialName("rarity") val rarity: String
)

@Serializable
data class PrimeSetDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("type") val type: String,
    @SerialName("image") val image: String,
    @SerialName("components") val components: List<PrimeComponentDto>
)

@Serializable
data class PrimeComponentDto(
    @SerialName("id") val id: String,
    @SerialName("part") val part: String,
    @SerialName("quantity") val quantity: Int
)

@Serializable
data class PrimeCollectionDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("promoImage") val promoImage: String,
    @SerialName("released") val released: Int,
    @SerialName("primeSets") val primeSets: List<String>
)
