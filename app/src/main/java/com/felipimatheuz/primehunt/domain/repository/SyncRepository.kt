package com.felipimatheuz.primehunt.domain.repository

import com.felipimatheuz.primehunt.ui.viewmodel.splash.SyncEvent
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    fun performSync(): Flow<SyncEvent>
}
