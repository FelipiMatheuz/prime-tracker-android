package com.felipimatheuz.primehunt.service.google

import android.content.Context
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.SignInResult
import com.felipimatheuz.primehunt.model.UserData
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleCredential @Inject constructor(@param:ApplicationContext private val context: Context) {

    private val credentialManager = CredentialManager.create(context)
    private val auth = Firebase.auth

    private val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(context.getString(R.string.default_web_client_id))
        .setAutoSelectEnabled(true)
        .build()

    private val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(userId = uid, name = displayName)
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        auth.signOut()
    }

    private suspend fun handleSignIn(result: GetCredentialResponse): SignInResult {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val token = GoogleIdTokenCredential.createFrom(credential.data)
                        val authCredential = GoogleAuthProvider.getCredential(token.idToken, null)
                        val user = auth.signInWithCredential(authCredential).await().user
                        return SignInResult(
                            data = user?.run {
                                UserData(userId = user.uid, name = user.displayName)
                            }, errorMessage = null
                        )
                    } catch (_: GoogleIdTokenParsingException) {
                        return SignInResult(null, "${context.getString(R.string.sync_sign_error)} (2)")
                    } catch (_: CancellationException) {
                        return SignInResult(null, context.getString(R.string.sync_sign_cancel))
                    }
                } else {
                    return SignInResult(null, context.getString(R.string.sync_sign_invalid))
                }
            }

            else -> {
                return SignInResult(null, context.getString(R.string.sync_sign_invalid))
            }
        }
    }

    suspend fun signUpCredentialManager(): SignInResult {
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )
            return handleSignIn(result)
        } catch (_: GetCredentialException) {
            return SignInResult(null, "${context.getString(R.string.sync_sign_error)} (1)")
        }
    }
}