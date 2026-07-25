package com.felipimatheuz.primehunt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.business.state.SyncEvent
import com.felipimatheuz.primehunt.data.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val syncRepository: SyncRepository) : ViewModel() {

    private val _syncEvent = MutableStateFlow<SyncEvent>(SyncEvent.Starting)
    val syncEvent: StateFlow<SyncEvent> = _syncEvent.asStateFlow()

    init {
        startSync()
    }

    fun startSync() {
        viewModelScope.launch {
            syncRepository.performSync().collect { event ->
                _syncEvent.value = event
            }
        }
    }
}
