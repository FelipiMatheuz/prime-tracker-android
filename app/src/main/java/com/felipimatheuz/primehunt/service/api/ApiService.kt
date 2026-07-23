package com.felipimatheuz.primehunt.service.api

import com.felipimatheuz.primehunt.business.state.SyncEvent
import com.felipimatheuz.primehunt.data.repository.SyncRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiService @Inject constructor(
    private val syncRepository: SyncRepository
) {
    fun syncWithRemote(): Flow<SyncEvent> = syncRepository.performSync()
}
