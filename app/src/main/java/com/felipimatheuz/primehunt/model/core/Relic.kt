package com.felipimatheuz.primehunt.model.core

data class Relic(
    val name: String,
    val quantity: Int,
    val hasForma: Boolean,
    val vaulted: Boolean
)

enum class RelicTier {
    Lith,
    Meso,
    Neo,
    Axi
}
