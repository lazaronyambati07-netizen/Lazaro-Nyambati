package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.boostDataStore: DataStore<Preferences> by preferencesDataStore(name = "boostke_prefs")

class SettingsDataStore(private val context: Context) {

    companion object {
        const val DEFAULT_EMULATOR_BASE_URL = "http://10.0.2.2:3000"
        const val DEFAULT_LOCALHOST_BASE_URL = "http://localhost:3000"
        private val KEY_SERVER_BASE_URL = stringPreferencesKey("server_base_url")
        private val KEY_SAVED_PHONE = stringPreferencesKey("saved_phone")
    }

    val serverBaseUrl: Flow<String> = context.boostDataStore.data.map { prefs ->
        prefs[KEY_SERVER_BASE_URL]?.takeIf { it.isNotBlank() } ?: DEFAULT_EMULATOR_BASE_URL
    }

    val savedPhone: Flow<String> = context.boostDataStore.data.map { prefs ->
        prefs[KEY_SAVED_PHONE] ?: ""
    }

    suspend fun saveServerBaseUrl(url: String) {
        val cleaned = url.trim().trimEnd('/')
        context.boostDataStore.edit { prefs ->
            prefs[KEY_SERVER_BASE_URL] = cleaned.ifBlank { DEFAULT_EMULATOR_BASE_URL }
        }
    }

    suspend fun savePhone(phone: String) {
        context.boostDataStore.edit { prefs ->
            prefs[KEY_SAVED_PHONE] = phone.trim()
        }
    }
}
