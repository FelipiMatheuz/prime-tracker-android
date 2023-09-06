package com.felipimatheuz.primehunt.state

sealed class LoadState {

    object LoadRelic : LoadState()
    object LoadSet : LoadState()
    data class Error(val lastTextRes: Int) : LoadState()
    object Ready : LoadState()
}