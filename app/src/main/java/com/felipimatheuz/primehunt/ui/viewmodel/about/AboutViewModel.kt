package com.felipimatheuz.primehunt.ui.viewmodel.about

import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.data.cloud.AppUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    appUpdate: AppUpdate
) : ViewModel() {
    val updateState: StateFlow<Boolean> = appUpdate.updateState
}