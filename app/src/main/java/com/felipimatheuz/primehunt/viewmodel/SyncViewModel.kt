package com.felipimatheuz.primehunt.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.business.external.Firestore
import com.felipimatheuz.primehunt.business.state.SyncState
import com.felipimatheuz.primehunt.model.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SyncViewModel : ViewModel() {
    val state = MutableStateFlow<SyncState>(SyncState.None)

    fun onSignInResult(result: SignInResult) {
        state.update {
            if (result.data != null) SyncState.SuccessSignIn else SyncState.Error(result.errorMessage ?: "")
        }
    }

    fun importCheckList(context: Context, docId: String) {
        Firestore().importCheckList(context, docId, state)
    }

    fun exportCheckList(context: Context, docId: String) {
        Firestore().exportCheckList(context, docId, state)
    }


    fun logOut() {
        state.update {
            SyncState.SuccessSignOut
        }
    }

    fun resetState() {
        state.update {
            SyncState.None
        }
    }
}