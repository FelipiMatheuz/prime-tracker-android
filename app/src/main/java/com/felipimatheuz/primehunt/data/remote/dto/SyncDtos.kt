package com.felipimatheuz.primehunt.data.remote.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RelicDto(
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("era") val era: String,
    @JsonProperty("source") val source: String,
    @JsonProperty("drops") val drops: List<RelicDropDto>
)

data class RelicDropDto(
    @JsonProperty("id") val id: String,
    @JsonProperty("rarity") val rarity: String
)

data class PrimeSetDto(
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("image") val image: String,
    @JsonProperty("components") val components: List<PrimeComponentDto>
)

data class PrimeComponentDto(
    @JsonProperty("id") val id: String,
    @JsonProperty("part") val part: String,
    @JsonProperty("quantity") val quantity: Int
)

data class PrimeCollectionDto(
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("promoImage") val promoImage: String,
    @JsonProperty("released") val released: Int,
    @JsonProperty("primeSets") val primeSets: List<String>
)
