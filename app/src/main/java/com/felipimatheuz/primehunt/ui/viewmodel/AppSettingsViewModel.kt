package com.felipimatheuz.primehunt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.domain.model.enums.BgIcons
import com.felipimatheuz.primehunt.data.local.preferences.UserPreferencesDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val preferencesDataSource: UserPreferencesDataSource
) : ViewModel() {

    val selectedWallpaper: StateFlow<BgIcons> = preferencesDataSource.selectedWallpaper
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BgIcons.WARFRAME
        )

    fun updateWallpaper(icon: BgIcons) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataSource.setSelectedWallpaper(icon)
        }
    }
}
