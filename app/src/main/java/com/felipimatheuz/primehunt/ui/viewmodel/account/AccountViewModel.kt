package com.felipimatheuz.primehunt.ui.viewmodel.account

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.domain.model.AuthState
import com.felipimatheuz.primehunt.domain.repository.AuthRepository
import com.felipimatheuz.primehunt.domain.repository.SquadRepository
import com.felipimatheuz.primehunt.service.squad.SquadServiceController
import com.felipimatheuz.primehunt.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val squadRepository: SquadRepository,
    @ApplicationContext private val context: Context
) : ViewModel(), MviViewModel<AccountState, AccountIntent> {

    private val _state = MutableStateFlow(AccountState())
    override val state: StateFlow<AccountState> = _state.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.authState.collect { authState ->
                _state.update {
                    it.copy(
                        authState = authState,
                        isLoading = authState is AuthState.Authenticating
                    )
                }
            }
        }
    }

    override fun onIntent(intent: AccountIntent) {
        when (intent) {
            is AccountIntent.SignIn -> signIn()
            is AccountIntent.RequestSignOut -> requestSignOut()
            is AccountIntent.ConfirmSignOut -> performSignOut()
            is AccountIntent.CancelSignOut -> _state.update { it.copy(showLogoutConfirmation = false) }
            is AccountIntent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.signIn()
            if (result is AuthState.AuthError) {
                _state.update { it.copy(isLoading = false, errorMessage = result.message) }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun requestSignOut() {
        viewModelScope.launch {
            val hasActiveAnnouncement = runCatching {
                squadRepository.observeOwnAnnouncement().first() != null
            }.getOrDefault(false)

            if (hasActiveAnnouncement) {
                _state.update { it.copy(showLogoutConfirmation = true) }
            } else {
                performSignOut()
            }
        }
    }

    private fun performSignOut() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, showLogoutConfirmation = false, errorMessage = null) }

            // Stop foreground service and attempt remote announcement deletion
            SquadServiceController.stopSession(context)
            runCatching { squadRepository.deleteAnnouncement() }

            // Execute auth sign out
            authRepository.signOut()

            _state.update { it.copy(isLoading = false) }
        }
    }
}
