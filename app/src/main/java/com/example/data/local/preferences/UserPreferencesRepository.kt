package com.example.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dayflow_user_preferences")

enum class AppThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

data class UserPreferences(
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val currencySymbol: String = AppConfig.DEFAULT_CURRENCY_SYMBOL,
    val currencyCode: String = AppConfig.DEFAULT_CURRENCY_CODE,
    val isPinLockEnabled: Boolean = false,
    val pinHash: String = "",
    val isOnboardingCompleted: Boolean = false,
    val isAiEnabled: Boolean = true,
    val isNotificationsEnabled: Boolean = true,
    val isProUser: Boolean = false,
    val morningReminderHour: Int = 8,
    val eveningReminderHour: Int = 20
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferenceKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val CURRENCY_CODE = stringPreferencesKey("currency_code")
        val PIN_LOCK_ENABLED = booleanPreferencesKey("pin_lock_enabled")
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val AI_ENABLED = booleanPreferencesKey("ai_enabled")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val PRO_USER = booleanPreferencesKey("pro_user")
        val MORNING_REMINDER_HOUR = intPreferencesKey("morning_reminder_hour")
        val EVENING_REMINDER_HOUR = intPreferencesKey("evening_reminder_hour")
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
            val themeModeName = preferences[PreferenceKeys.THEME_MODE] ?: AppThemeMode.DARK.name
            val themeMode = try {
                AppThemeMode.valueOf(themeModeName)
            } catch (e: Exception) {
                AppThemeMode.DARK
            }

            UserPreferences(
                themeMode = themeMode,
                currencySymbol = preferences[PreferenceKeys.CURRENCY_SYMBOL] ?: AppConfig.DEFAULT_CURRENCY_SYMBOL,
                currencyCode = preferences[PreferenceKeys.CURRENCY_CODE] ?: AppConfig.DEFAULT_CURRENCY_CODE,
                isPinLockEnabled = preferences[PreferenceKeys.PIN_LOCK_ENABLED] ?: false,
                pinHash = preferences[PreferenceKeys.PIN_HASH] ?: "",
                isOnboardingCompleted = preferences[PreferenceKeys.ONBOARDING_COMPLETED] ?: false,
                isAiEnabled = preferences[PreferenceKeys.AI_ENABLED] ?: AppConfig.FEATURE_AI_INSIGHTS_ENABLED,
                isNotificationsEnabled = preferences[PreferenceKeys.NOTIFICATIONS_ENABLED] ?: AppConfig.FEATURE_NOTIFICATIONS_ENABLED,
                isProUser = preferences[PreferenceKeys.PRO_USER] ?: false,
                morningReminderHour = preferences[PreferenceKeys.MORNING_REMINDER_HOUR] ?: 8,
                eveningReminderHour = preferences[PreferenceKeys.EVENING_REMINDER_HOUR] ?: 20
            )
        }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setCurrency(symbol: String, code: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.CURRENCY_SYMBOL] = symbol
            preferences[PreferenceKeys.CURRENCY_CODE] = code
        }
    }

    suspend fun setPinLock(enabled: Boolean, hash: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.PIN_LOCK_ENABLED] = enabled
            preferences[PreferenceKeys.PIN_HASH] = hash
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setAiEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.AI_ENABLED] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setProUser(isPro: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.PRO_USER] = isPro
        }
    }

    suspend fun setReminderHours(morningHour: Int, eveningHour: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.MORNING_REMINDER_HOUR] = morningHour
            preferences[PreferenceKeys.EVENING_REMINDER_HOUR] = eveningHour
        }
    }
}
