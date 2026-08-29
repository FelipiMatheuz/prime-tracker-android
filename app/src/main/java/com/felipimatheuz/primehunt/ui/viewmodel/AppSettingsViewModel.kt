package com.felipimatheuz.primehunt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.domain.model.enums.BgIcons
import com.felipimatheuz.primehunt.domain.model.enums.AppTheme
import com.felipimatheuz.primehunt.domain.model.enums.AppLanguage
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

    val selectedTheme: StateFlow<AppTheme> = preferencesDataSource.selectedTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )

    val selectedLanguage: StateFlow<AppLanguage> = preferencesDataSource.selectedLanguage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.SYSTEM
        )

    fun updateWallpaper(icon: BgIcons) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataSource.setSelectedWallpaper(icon)
        }
    }

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataSource.setTheme(theme)
        }
    }

    fun updateLanguage(language: AppLanguage) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataSource.setLanguage(language)
        }
    }
}
