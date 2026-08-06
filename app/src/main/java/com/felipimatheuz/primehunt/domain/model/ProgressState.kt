package com.felipimatheuz.primehunt.domain.model

interface ProgressState {
    val progressTotal: Int
    val progressOwned: Int

    val isComplete: Boolean get() = progressTotal in 1..progressOwned
    val isNotStarted: Boolean get() = progressOwned == 0
    val isInProgress: Boolean get() = progressOwned in 1..<progressTotal
}

fun ProgressState.matches(filter: com.felipimatheuz.primehunt.data.local.enums.ProgressFilter): Boolean {
    return when (filter) {
        com.felipimatheuz.primehunt.data.local.enums.ProgressFilter.ALL -> true
        com.felipimatheuz.primehunt.data.local.enums.ProgressFilter.COMPLETE -> isComplete
        com.felipimatheuz.primehunt.data.local.enums.ProgressFilter.INCOMPLETE -> !isComplete
        com.felipimatheuz.primehunt.data.local.enums.ProgressFilter.IN_PROGRESS -> isInProgress
        com.felipimatheuz.primehunt.data.local.enums.ProgressFilter.NOT_STARTED -> isNotStarted
    }
}
