package com.example.m_commerce_admin.features.login.data.dataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val Context.dataStore by preferencesDataStore(name = "admin_prefs")

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .map { it[booleanPreferencesKey("is_logged_in")] ?: false }

    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { it[booleanPreferencesKey("is_logged_in")] = value }
    }

}