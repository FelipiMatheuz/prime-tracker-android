package com.felipimatheuz.primehunt.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.service.google.AppUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PrimeInfoViewModel @Inject constructor(
    appUpdate: AppUpdate
) : ViewModel() {
    val updateState: StateFlow<Boolean> = appUpdate.updateState
}