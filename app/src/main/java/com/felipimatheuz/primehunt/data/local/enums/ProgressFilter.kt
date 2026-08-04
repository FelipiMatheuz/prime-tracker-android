package com.felipimatheuz.primehunt.data.local.enums

import com.felipimatheuz.primehunt.R
import kotlinx.serialization.Serializable

@Serializable
enum class ProgressFilter(val displayNameRes: Int) {
    ALL(R.string.filter_progress_all),
    COMPLETE(R.string.filter_progress_complete),
    INCOMPLETE(R.string.filter_progress_incomplete),
    IN_PROGRESS(R.string.filter_progress_in_progress),
    NOT_STARTED(R.string.filter_progress_not_started)
}
