package com.felipimatheuz.primehunt.data.local.enums

import com.felipimatheuz.primehunt.R
import kotlinx.serialization.Serializable

@Serializable
enum class GoalStatus(val displayNameRes: Int) {
    ACTIVE(R.string.status_active),
    COMPLETED(R.string.status_completed);
}