package com.example.easycrypto.core.database

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


object UserPreferences {
    private val Context.dataStore by preferencesDataStore(name = "user_prefs")

    val USER_ID = stringPreferencesKey("user_id")

    val IS_NEW_USER = booleanPreferencesKey("is_new_user")

    suspend fun setNewUserFlag(context: Context, isNew: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_NEW_USER] = isNew
        }
    }

    suspend fun isNewUser(context: Context): Boolean {
        return context.dataStore.data.first()[IS_NEW_USER] ?: false
    }

    suspend fun saveUserId(context: Context, userId: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = userId
        }
    }

    suspend fun getUserId(context: Context): String? {
        return context.dataStore.data.first()[USER_ID]
    }

    suspend fun clearUserId(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_ID)
        }
    }

}
