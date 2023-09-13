package com.felipimatheuz.primehunt.model.core

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
data class RelicSet(
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
    val item: RelicItem
)