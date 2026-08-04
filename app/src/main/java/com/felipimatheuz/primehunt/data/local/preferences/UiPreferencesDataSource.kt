package com.felipimatheuz.primehunt.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
import com.felipimatheuz.primehunt.data.local.enums.ProgressFilter
import com.felipimatheuz.primehunt.data.local.enums.RelicsView
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import com.felipimatheuz.primehunt.data.remote.enums.RelicEra
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class PrimeSetUiPrefs(
    val selectedView: Int = 0,
    val progress: ProgressFilter = ProgressFilter.ALL,
    val categories: Set<PrimeType> = emptySet(),
    val availabilities: Set<RelicSource> = emptySet()
)

@Serializable
data class RelicUiPrefs(
    val selectedView: RelicsView = RelicsView.ERA,
    val eras: Set<RelicEra> = emptySet(),
    val availabilities: Set<RelicSource> = emptySet(),
    val progress: ProgressFilter = ProgressFilter.ALL
)

@Serializable
data class GoalUiPrefs(
    val status: GoalStatus = GoalStatus.ACTIVE,
    val targetTypes: Set<GoalTargetType> = GoalTargetType.entries.toSet(),
    val categoryIds: Set<Long> = emptySet()
)

@Singleton
class UiPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val PRIME_SET_PREFS = stringPreferencesKey("prime_set_prefs")
        val RELIC_PREFS = stringPreferencesKey("relic_prefs")
        val GOAL_PREFS = stringPreferencesKey("goal_prefs")
    }

    private val json = Json { ignoreUnknownKeys = true }

    val primeSetPrefs: Flow<PrimeSetUiPrefs> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.PRIME_SET_PREFS]?.let {
                try { json.decodeFromString<PrimeSetUiPrefs>(it) } catch (_: Exception) { PrimeSetUiPrefs() }
            } ?: PrimeSetUiPrefs()
        }

    val relicPrefs: Flow<RelicUiPrefs> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.RELIC_PREFS]?.let {
                try { json.decodeFromString<RelicUiPrefs>(it) } catch (_: Exception) { RelicUiPrefs() }
            } ?: RelicUiPrefs()
        }

    val goalPrefs: Flow<GoalUiPrefs> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.GOAL_PREFS]?.let {
                try { json.decodeFromString<GoalUiPrefs>(it) } catch (_: Exception) { GoalUiPrefs() }
            } ?: GoalUiPrefs()
        }

    suspend fun updatePrimeSetPrefs(prefs: PrimeSetUiPrefs) {
        dataStore.edit { it[PreferencesKeys.PRIME_SET_PREFS] = json.encodeToString(prefs) }
    }

    suspend fun updateRelicPrefs(prefs: RelicUiPrefs) {
        dataStore.edit { it[PreferencesKeys.RELIC_PREFS] = json.encodeToString(prefs) }
    }

    suspend fun updateGoalPrefs(prefs: GoalUiPrefs) {
        dataStore.edit { it[PreferencesKeys.GOAL_PREFS] = json.encodeToString(prefs) }
    }
}
