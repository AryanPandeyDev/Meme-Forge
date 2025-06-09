package com.example.memeforge.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreference @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    val notificationsEnabled: Flow<Boolean> = dataStore.data
        .map { it[NOTIFICATIONS_KEY] ?: true }

    val darkModeEnabled: Flow<Boolean> = dataStore.data
        .map { it[DARK_MODE_KEY] ?: true }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_KEY] = enabled }
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { it[DARK_MODE_KEY] = enabled }
    }
}