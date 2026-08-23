package com.felipimatheuz.primehunt.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.felipimatheuz.primehunt.domain.model.enums.AppLanguage
import com.felipimatheuz.primehunt.domain.model.enums.AppTheme
import com.felipimatheuz.primehunt.domain.model.enums.BgIcons
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val SELECTED_WALLPAPER = stringPreferencesKey("selected_wallpaper")
        val THEME = stringPreferencesKey("theme")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val selectedWallpaper: Flow<BgIcons> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val wallpaperName = preferences[PreferencesKeys.SELECTED_WALLPAPER] ?: BgIcons.WARFRAME.name
            try {
                BgIcons.valueOf(wallpaperName)
            } catch (_: Exception) {
                BgIcons.WARFRAME
            }
        }

    val selectedTheme: Flow<AppTheme> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME] ?: AppTheme.SYSTEM.name
            try {
                AppTheme.valueOf(themeName)
            } catch (_: Exception) {
                AppTheme.SYSTEM
            }
        }

    val selectedLanguage: Flow<AppLanguage> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val langName = preferences[PreferencesKeys.LANGUAGE] ?: AppLanguage.SYSTEM.name
            try {
                AppLanguage.valueOf(langName)
            } catch (_: Exception) {
                AppLanguage.SYSTEM
            }
        }

    suspend fun setSelectedWallpaper(icon: BgIcons) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_WALLPAPER] = icon.name
        }
    }

    suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language.name
        }
    }
}
