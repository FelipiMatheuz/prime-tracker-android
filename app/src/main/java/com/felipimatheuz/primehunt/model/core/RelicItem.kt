package com.felipimatheuz.primehunt.model.core

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
data class RelicItem(
    @JsonProperty("name")
    val name: String,
    var obtained: Boolean = false
)

enum class RelicTier {
    Lith,
    Meso,
    Neo,
    Axi
}
