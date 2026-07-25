package com.felipimatheuz.primehunt.ui.mvi

import kotlinx.coroutines.flow.StateFlow

interface MviState

interface MviIntent

interface MviViewModel<S : MviState, I : MviIntent> {
    val state: StateFlow<S>
    fun onIntent(intent: I)
}
