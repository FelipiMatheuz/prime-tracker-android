package com.felipimatheuz.primehunt.domain.repository

import com.felipimatheuz.primehunt.domain.model.SyncEvent
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    fun performSync(): Flow<SyncEvent>
}
