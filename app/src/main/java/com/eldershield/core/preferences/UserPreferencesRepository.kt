package com.eldershield.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "eldershield_prefs")

data class UserPreferences(
    val languageCode: String = "en",
    val isLargeTextEnabled: Boolean = false,
    val isVoiceGuidanceEnabled: Boolean = true,
    val isFamilyAssistanceActive: Boolean = false,
    val lastScanTime: Long = 0L
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
        val LARGE_TEXT = booleanPreferencesKey("large_text_enabled")
        val VOICE_GUIDANCE = booleanPreferencesKey("voice_guidance_enabled")
        val FAMILY_ASSISTANCE = booleanPreferencesKey("family_assistance_active")
        val LAST_SCAN_TIME = longPreferencesKey("last_scan_time")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                languageCode = preferences[PreferencesKeys.LANGUAGE_CODE] ?: "en",
                isLargeTextEnabled = preferences[PreferencesKeys.LARGE_TEXT] ?: false,
                isVoiceGuidanceEnabled = preferences[PreferencesKeys.VOICE_GUIDANCE] ?: true,
                isFamilyAssistanceActive = preferences[PreferencesKeys.FAMILY_ASSISTANCE] ?: false,
                lastScanTime = preferences[PreferencesKeys.LAST_SCAN_TIME] ?: 0L
            )
        }

    suspend fun updateLanguage(code: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE_CODE] = code
        }
    }

    suspend fun setLargeText(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LARGE_TEXT] = enabled
        }
    }

    suspend fun setVoiceGuidance(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VOICE_GUIDANCE] = enabled
        }
    }

    suspend fun setFamilyAssistance(active: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FAMILY_ASSISTANCE] = active
        }
    }

    suspend fun updateLastScanTime(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SCAN_TIME] = timestamp
        }
    }
}
