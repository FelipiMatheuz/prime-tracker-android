package com.felipimatheuz.primehunt.domain.model

sealed interface AuthState {
    data object Unauthenticated : AuthState
    data object Authenticating : AuthState
    data class Authenticated(val user: UserData) : AuthState
    data class AuthError(val message: String) : AuthState

    val isAuthenticated: Boolean
        get() = this is Authenticated

    val currentUser: UserData?
        get() = (this as? Authenticated)?.user
}
