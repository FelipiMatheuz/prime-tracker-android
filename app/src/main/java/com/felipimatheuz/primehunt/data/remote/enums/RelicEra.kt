package com.felipimatheuz.primehunt.data.remote.enums

import androidx.annotation.DrawableRes
import com.felipimatheuz.primehunt.R

enum class RelicEra(@param:DrawableRes val icon: Int) {
    LITH(R.drawable.relic_lith),
    MESO(R.drawable.relic_meso),
    NEO(R.drawable.relic_neo),
    AXI(R.drawable.relic_axi);

    val displayName: String get() = name.lowercase().replaceFirstChar { it.uppercase() }

    companion object {
        fun fromString(value: String): RelicEra {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: AXI
        }
    }
}
