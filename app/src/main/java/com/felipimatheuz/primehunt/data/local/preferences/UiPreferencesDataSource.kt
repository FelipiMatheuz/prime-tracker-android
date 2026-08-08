package com.felipimatheuz.primehunt.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.felipimatheuz.primehunt.domain.model.enums.*
import com.felipimatheuz.primehunt.domain.model.prefs.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
internal data class PrimeSetUiPrefsDto(
    val selectedView: Int = 0,
    val progress: ProgressFilter = ProgressFilter.ALL,
    val categories: Set<PrimeType> = emptySet(),
    val availabilities: Set<RelicSource> = emptySet()
) {
    fun toDomain() = PrimeSetUiPrefs(selectedView, progress, categories, availabilities)
    companion object {
        fun fromDomain(domain: PrimeSetUiPrefs) = PrimeSetUiPrefsDto(domain.selectedView, domain.progress, domain.categories, domain.availabilities)
    }
}

@Serializable
internal data class RelicUiPrefsDto(
    val selectedView: RelicsView = RelicsView.ERA,
    val eras: Set<RelicEra> = emptySet(),
    val availabilities: Set<RelicSource> = emptySet(),
    val progress: ProgressFilter = ProgressFilter.ALL
) {
    fun toDomain() = RelicUiPrefs(selectedView, eras, availabilities, progress)
    companion object {
        fun fromDomain(domain: RelicUiPrefs) = RelicUiPrefsDto(domain.selectedView, domain.eras, domain.availabilities, domain.progress)
    }
}

@Serializable
internal data class GoalUiPrefsDto(
    val status: GoalStatus = GoalStatus.ACTIVE,
    val targetTypes: Set<GoalTargetType> = GoalTargetType.entries.toSet(),
    val categoryIds: Set<Long> = emptySet()
) {
    fun toDomain() = GoalUiPrefs(status, targetTypes, categoryIds)
    companion object {
        fun fromDomain(domain: GoalUiPrefs) = GoalUiPrefsDto(domain.status, domain.targetTypes, domain.categoryIds)
    }
}

@Serializable
internal data class CloudUiPrefsDto(
    val isMigrationSuccess: Boolean = false
) {
    fun toDomain() = CloudUiPrefs(isMigrationSuccess)
    companion object {
        fun fromDomain(domain: CloudUiPrefs) = CloudUiPrefsDto(domain.isMigrationSuccess)
    }
}

@Singleton
class UiPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val PRIME_SET_PREFS = stringPreferencesKey("prime_set_prefs")
        val RELIC_PREFS = stringPreferencesKey("relic_prefs")
        val GOAL_PREFS = stringPreferencesKey("goal_prefs")
        val CLOUD_PREFS = stringPreferencesKey("cloud_prefs")
    }

    private val json = Json { ignoreUnknownKeys = true }

    val primeSetPrefs: Flow<PrimeSetUiPrefs> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.PRIME_SET_PREFS]?.let {
                try { json.decodeFromString<PrimeSetUiPrefsDto>(it).toDomain() } catch (_: Exception) { PrimeSetUiPrefs() }
            } ?: PrimeSetUiPrefs()
        }

    val relicPrefs: Flow<RelicUiPrefs> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.RELIC_PREFS]?.let {
                try { json.decodeFromString<RelicUiPrefsDto>(it).toDomain() } catch (_: Exception) { RelicUiPrefs() }
            } ?: RelicUiPrefs()
        }

    val goalPrefs: Flow<GoalUiPrefs> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.GOAL_PREFS]?.let {
                try { json.decodeFromString<GoalUiPrefsDto>(it).toDomain() } catch (_: Exception) { GoalUiPrefs() }
            } ?: GoalUiPrefs()
        }

    val cloudPrefs: Flow<CloudUiPrefs> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.CLOUD_PREFS]?.let {
                try { json.decodeFromString<CloudUiPrefsDto>(it).toDomain() } catch (_: Exception) { CloudUiPrefs() }
            } ?: CloudUiPrefs()
        }

    suspend fun updatePrimeSetPrefs(prefs: PrimeSetUiPrefs) {
        dataStore.edit { it[PreferencesKeys.PRIME_SET_PREFS] = json.encodeToString(PrimeSetUiPrefsDto.fromDomain(prefs)) }
    }

    suspend fun updateRelicPrefs(prefs: RelicUiPrefs) {
        dataStore.edit { it[PreferencesKeys.RELIC_PREFS] = json.encodeToString(RelicUiPrefsDto.fromDomain(prefs)) }
    }

    suspend fun updateGoalPrefs(prefs: GoalUiPrefs) {
        dataStore.edit { it[PreferencesKeys.GOAL_PREFS] = json.encodeToString(GoalUiPrefsDto.fromDomain(prefs)) }
    }

    suspend fun updateCloudPrefs(prefs: CloudUiPrefs) {
        dataStore.edit { it[PreferencesKeys.CLOUD_PREFS] = json.encodeToString(CloudUiPrefsDto.fromDomain(prefs)) }
    }
}
