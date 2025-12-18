package com.felipimatheuz.primehunt.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
data class RelicItem(
    @param:JsonProperty("name")
    val name: String,
    @param:JsonProperty("obtained")
    var obtained: Boolean = false
)

enum class RelicTier {
    Lith,
    Meso,
    Neo,
    Axi
}
