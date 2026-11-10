package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.cloud.GoogleCredential
import com.felipimatheuz.primehunt.domain.model.AuthState
import com.felipimatheuz.primehunt.domain.model.UserData
import com.felipimatheuz.primehunt.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val googleCredential: GoogleCredential
) : AuthRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _authState = MutableStateFlow(
        googleCredential.getSignedInUser()?.let { AuthState.Authenticated(it) }
            ?: AuthState.Unauthenticated
    )
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        repositoryScope.launch {
            googleCredential.isUserSignedInFlow.collect { isSignedIn ->
                if (isSignedIn) {
                    val user = googleCredential.getSignedInUser()
                    if (user != null) {
                        _authState.value = AuthState.Authenticated(user)
                    }
                } else {
                    if (_authState.value !is AuthState.Authenticating) {
                        _authState.value = AuthState.Unauthenticated
                    }
                }
            }
        }
    }

    override fun getCurrentUser(): UserData? {
        return googleCredential.getSignedInUser()
    }

    override suspend fun signIn(): AuthState {
        _authState.value = AuthState.Authenticating
        val result = googleCredential.signUpCredentialManager()
        val user = result.data
        val newState = if (user != null) {
            AuthState.Authenticated(user)
        } else {
            AuthState.AuthError(result.errorMessage ?: "Sign in error")
        }
        _authState.value = newState
        return newState
    }

    override suspend fun signOut(): AuthState {
        googleCredential.signOut()
        _authState.value = AuthState.Unauthenticated
        return AuthState.Unauthenticated
    }
}
