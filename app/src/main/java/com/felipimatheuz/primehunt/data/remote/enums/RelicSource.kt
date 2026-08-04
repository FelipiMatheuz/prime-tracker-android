package com.felipimatheuz.primehunt.data.remote.enums

import com.felipimatheuz.primehunt.R
import kotlinx.serialization.Serializable

@Serializable
enum class RelicSource(val displayNameRes: Int) {
    MISSION(R.string.status_mission),
    RESURGENCE(R.string.status_resurgence),
    BARO(R.string.status_baro),
    VAULT(R.string.status_vault);

    companion object {
        fun fromString(value: String): RelicSource {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: VAULT
        }
    }
}