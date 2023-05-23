package com.normuradov.flightsearchapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.normuradov.flightsearchapp.data.AppContainer
import com.normuradov.flightsearchapp.data.AppDataContainer

private const val USER_INPUT_PREFERENCE_NAME = "user_input_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_INPUT_PREFERENCE_NAME
)

class FlightApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this, dataStore)
    }
}