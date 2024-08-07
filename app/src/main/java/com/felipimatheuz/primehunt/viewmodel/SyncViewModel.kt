package com.felipimatheuz.primehunt.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.service.google.Firestore
import com.felipimatheuz.primehunt.business.state.SyncState
import com.felipimatheuz.primehunt.model.SignInResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    fun getPromptMessage(context: Context, state: SyncState, onSuccess: () -> Unit, onError: () -> Unit): String {
        val notification = getNotificationState(state, onSuccess, onError)
        return if (notification != null) {
            if (notification == 0)
                (state as SyncState.Error).message
            else context.getString(notification)
        } else {
            ""
        }
    }

    private fun resetState() {
        state.update {
            SyncState.None
        }
    }

    private fun getNotificationState(state: SyncState, onSuccess: () -> Unit, onError: () -> Unit): Int? {
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            resetState()
        }
        return when (state) {
            SyncState.SuccessSignIn -> {
                onSuccess()
                R.string.sync_signin_success
            }

            SyncState.SuccessSignOut -> {
                onSuccess()
                R.string.sync_signout_success
            }

            SyncState.SuccessImport -> {
                onSuccess()
                R.string.sync_imported
            }

            SyncState.SuccessExport -> {
                onSuccess()
                R.string.sync_exported
            }

            is SyncState.Error -> {
                onError()
                0
            }

            SyncState.None -> null
        }
    }
}