package com.felipimatheuz.primehunt.domain.model

enum class EtlFile(val text: String) {
    RELICS("relics"),
    PRIME_SETS("prime sets"),
    PRIME_COLLECTIONS("prime collections")
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
