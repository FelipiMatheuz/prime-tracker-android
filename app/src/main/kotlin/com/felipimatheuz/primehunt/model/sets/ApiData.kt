package com.felipimatheuz.primehunt.model.sets

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
data class ApiData(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("rewards")
    val rewards: List<Reward>,
    @JsonProperty("vaulted")
    val vaulted: Boolean
)

@Keep
data class Reward(
    @JsonProperty("rarity")
    val rarity: String,
    @JsonProperty("chance")
    val chance: Float,
    @JsonProperty("item")
    val item: ItemRelic
)

@Keep
data class ItemRelic(
    @JsonProperty("name")
    val name: String
)