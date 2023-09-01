package com.felipimatheuz.primehunt.model.sets

data class Relic(
    val name: String,
    val quantity: Int,
    val hasForma: Boolean,
    val vaulted: Boolean
)

enum class RelicTier {
    LITH,
    MESO,
    NEO,
    AXI
}
