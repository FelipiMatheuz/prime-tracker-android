package com.felipimatheuz.primehunt.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
data class RelicItem(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("obtained")
    var obtained: Boolean = false
)

enum class RelicTier {
    Lith,
    Meso,
    Neo,
    Axi
}
