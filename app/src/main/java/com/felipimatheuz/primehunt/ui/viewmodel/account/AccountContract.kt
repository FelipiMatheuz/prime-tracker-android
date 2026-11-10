package com.felipimatheuz.primehunt.ui.viewmodel.account

import com.felipimatheuz.primehunt.domain.model.AuthState
import com.felipimatheuz.primehunt.ui.mvi.MviIntent
import com.felipimatheuz.primehunt.ui.mvi.MviState

data class AccountState(
    val authState: AuthState = AuthState.Unauthenticated,
    val isLoading: Boolean = false,
    val showLogoutConfirmation: Boolean = false,
    val errorMessage: String? = null
) : MviState

sealed interface AccountIntent : MviIntent {
    data object SignIn : AccountIntent
    data object RequestSignOut : AccountIntent
    data object ConfirmSignOut : AccountIntent
    data object CancelSignOut : AccountIntent
    data object ClearError : AccountIntent
}
