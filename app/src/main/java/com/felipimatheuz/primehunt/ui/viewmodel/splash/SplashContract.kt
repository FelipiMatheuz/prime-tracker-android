package com.felipimatheuz.primehunt.ui.viewmodel.splash

enum class EtlFile(val text: String) {
    PRIME_COLLECTIONS("prime collections"),
    PRIME_SETS("prime sets"),
    RELICS ("relics")
}

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
