package com.felipimatheuz.primehunt.business.state

import com.google.firebase.firestore.DocumentSnapshot

sealed class SyncState {
    object SuccessSignIn : SyncState()
    object SuccessSignOut : SyncState()
    object SuccessImport : SyncState()
    object SuccessExport : SyncState()
    data class Error(val message: String) : SyncState()
    object None : SyncState()
}