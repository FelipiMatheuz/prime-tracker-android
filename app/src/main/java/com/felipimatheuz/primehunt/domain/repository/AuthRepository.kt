package com.felipimatheuz.primehunt.domain.repository

import com.felipimatheuz.primehunt.domain.model.AuthState
import com.felipimatheuz.primehunt.domain.model.UserData
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>
    fun getCurrentUser(): UserData?
    suspend fun signIn(): AuthState
    suspend fun signOut(): AuthState
}
