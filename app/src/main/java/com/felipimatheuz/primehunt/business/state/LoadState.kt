package com.felipimatheuz.primehunt.business.state

sealed class LoadState {

    object LoadRelic : LoadState()
    object LoadSet : LoadState()
    object LoadOther : LoadState()
    data class Error(val lastTextRes: Int) : LoadState()
    object Ready : LoadState()
}