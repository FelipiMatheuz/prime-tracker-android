package com.felipimatheuz.primehunt.data.local.enums

import com.felipimatheuz.primehunt.R
import kotlinx.serialization.Serializable

@Serializable
enum class RelicsView(val displayNameRes: Int) {
    ERA(R.string.view_relic_era),
    AVAILABILITY(R.string.view_relic_availability),
    PROGRESS(R.string.view_relic_progress)
}
