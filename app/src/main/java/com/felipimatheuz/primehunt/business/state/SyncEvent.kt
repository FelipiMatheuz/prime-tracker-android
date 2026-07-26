package com.felipimatheuz.primehunt.business.state

sealed interface SyncEvent {

    data object Starting : SyncEvent

    data class CheckingManifest(
        val version: String
    ) : SyncEvent

    data class Downloading(
        val file: EtlFile
    ) : SyncEvent

    data class Importing(
        val file: EtlFile
    ) : SyncEvent

    data object Success : SyncEvent

    data object AlreadyUpToDate : SyncEvent

    data object Error : SyncEvent
}