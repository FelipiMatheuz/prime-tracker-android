package com.felipimatheuz.primehunt.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.service.google.Firestore
import com.felipimatheuz.primehunt.business.state.SyncState
import com.felipimatheuz.primehunt.model.SignInResult
import com.felipimatheuz.primehunt.service.google.GoogleCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val firestore: Firestore,
    private val googleCredential: GoogleCredential,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    val state = MutableStateFlow<SyncState>(SyncState.None)
    fun getCredentials() = googleCredential

    fun onSignInResult(result: SignInResult) {
        state.update {
            if (result.data != null) SyncState.SuccessSignIn else SyncState.Error(
                result.errorMessage ?: ""
            )
        }
    }

    fun importCheckList(docId: String) {
        firestore.importCheckList(docId, state)
    }

    fun exportCheckList(docId: String) {
        firestore.exportCheckList(docId, state)
    }


    fun logOut() {
        state.update {
            SyncState.SuccessSignOut
        }
    }

    fun getPromptMessage(
        state: SyncState,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ): String {
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

    private fun getNotificationState(
        state: SyncState,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ): Int? {
        CoroutineScope(Dispatchers.IO).launch {
            delay(3.seconds)
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