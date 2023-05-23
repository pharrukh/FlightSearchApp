package com.normuradov.flightsearchapp.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserInputRepository(private val dataStore: DataStore<Preferences>) {
    val userInput: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[USER_INPUT] ?: ""
        }


    private companion object {
        val USER_INPUT = stringPreferencesKey("user_input")
        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveUserInput(userInput: String) {
        dataStore.edit { preferences ->
            preferences[USER_INPUT] = userInput
        }
    }

}