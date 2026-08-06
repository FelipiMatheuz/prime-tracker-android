package com.felipimatheuz.primehunt.domain.model

import com.felipimatheuz.primehunt.data.local.enums.ProgressFilter

interface ProgressState {
    val progressTotal: Int
    val progressOwned: Int

    val isComplete: Boolean get() = progressTotal in 1..progressOwned
    val isNotStarted: Boolean get() = progressOwned == 0
    val isInProgress: Boolean get() = progressOwned in 1..<progressTotal
}

fun ProgressState.matches(filter: ProgressFilter): Boolean {
    return when (filter) {
        ProgressFilter.ALL -> true
        ProgressFilter.COMPLETE -> isComplete
        ProgressFilter.INCOMPLETE -> !isComplete
        ProgressFilter.IN_PROGRESS -> isInProgress
        ProgressFilter.NOT_STARTED -> isNotStarted
    }
}
