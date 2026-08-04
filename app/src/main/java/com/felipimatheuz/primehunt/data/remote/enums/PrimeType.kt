package com.felipimatheuz.primehunt.data.remote.enums

import com.felipimatheuz.primehunt.R
import kotlinx.serialization.Serializable

@Serializable
enum class PrimeType(val displayNameRes: Int) {
    WARFRAME(R.string.overview_warframes),
    PRIMARY(R.string.overview_primary),
    SECONDARY(R.string.overview_secondary),
    MELEE(R.string.overview_melee),
    ARCH_GUN(R.string.overview_arch_gun),
    COMPANION(R.string.overview_companion),
    ARCHWING(R.string.overview_archwing);

    companion object {
        fun fromString(value: String): PrimeType {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: WARFRAME
        }
    }
}