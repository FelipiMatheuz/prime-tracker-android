package com.felipimatheuz.primehunt.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
data class RelicSet(
    @param:JsonProperty("name")
    var name: String,
    @param:JsonProperty("rewards")
    var rewards: List<Reward>,
    @param:JsonProperty("vaulted")
    val vaulted: Boolean
)

@Keep
data class Reward(
    @param:JsonProperty("rarity")
    val rarity: String,
    @param:JsonProperty("chance")
    val chance: Float,
    @param:JsonProperty("item")
    val item: RelicItem
)