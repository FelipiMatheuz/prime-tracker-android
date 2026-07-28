package com.felipimatheuz.primehunt.data.remote.enums

import androidx.annotation.DrawableRes
import com.felipimatheuz.primehunt.R

enum class RelicEra(@param:DrawableRes val icon: Int) {
    LITH(R.drawable.ic_lith_relic),
    MESO(R.drawable.ic_meso_relic),
    NEO(R.drawable.ic_neo_relic),
    AXI(R.drawable.ic_axi_relic);

    val displayName: String get() = name.lowercase().replaceFirstChar { it.uppercase() }

    companion object {
        fun fromString(value: String): RelicEra {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: AXI
        }
    }
}
