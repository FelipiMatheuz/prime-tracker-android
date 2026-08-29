package com.felipimatheuz.primehunt.domain.model.enums

import com.felipimatheuz.primehunt.R

enum class ProgressFilter(val displayNameRes: Int) {
    ALL(R.string.filter_progress_all),
    COMPLETE(R.string.filter_progress_complete),
    INCOMPLETE(R.string.filter_progress_incomplete),
    IN_PROGRESS(R.string.filter_progress_in_progress),
    NOT_STARTED(R.string.filter_progress_not_started)
}
