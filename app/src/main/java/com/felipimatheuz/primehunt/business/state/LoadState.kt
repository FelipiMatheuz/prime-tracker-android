package com.felipimatheuz.primehunt.business.state

sealed class LoadState {

    object LoadRelic : LoadState()
    object LoadSet : LoadState()
    object LoadOther : LoadState()
    data class Error(val previousLoadState: LoadState, val message: String?) : LoadState()
    object Ready : LoadState()
}