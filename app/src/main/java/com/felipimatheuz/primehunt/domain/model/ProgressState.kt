package com.felipimatheuz.primehunt.domain.model

interface ProgressState {
    val progressTotal: Int
    val progressOwned: Int

    val isComplete: Boolean get() = progressTotal in 1..progressOwned
    val isNotStarted: Boolean get() = progressOwned == 0
    val isInProgress: Boolean get() = progressOwned in 1..<progressTotal
}
