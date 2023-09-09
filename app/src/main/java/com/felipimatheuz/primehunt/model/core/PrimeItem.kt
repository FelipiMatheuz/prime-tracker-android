package com.felipimatheuz.primehunt.model.core

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
data class PrimeItem(
    @JsonProperty("name")
    var name: String,
    @JsonProperty("components")
    val components: List<ItemComponent>,
    @JsonProperty("blueprint")
    var blueprint: Boolean = false
)

@Keep
data class ItemComponent(
    @JsonProperty("part")
    val part: ItemPart,
    @JsonProperty("obtained")
    var obtained: Boolean = false
)

@Keep
enum class ItemPart {
    NEUROPTICS,
    CHASSIS,
    SYSTEMS,
    BARREL,
    RECEIVER,
    STOCK,
    LINK,
    CARAPACE,
    CEREBRUM,
    BLADE,
    DISC,
    ORNAMENT,
    HANDLE,
    GAUNTLET,
    HILT,
    POUCH,
    STARS,
    HARNESS,
    CIRCUIT, // archwing systems
    WINGS,
    BAND,
    BUCKLE,
    HEAD,
    ULIMB,
    LLIMB,
    GRIP,
    STRING,
    GUARD,
    BOOT,
    CHAIN,
    //special parts:
    BRONCO,
    LEX,
    VASTO
}
