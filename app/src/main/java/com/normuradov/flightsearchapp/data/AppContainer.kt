package com.normuradov.flightsearchapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

interface AppContainer {
    val airportRepository: AirportRepository
    val favoriteRepository: FavoriteRepository
    val userInputRepository: UserInputRepository
}

class AppDataContainer(
    private val context: Context,
    private val dataStore: DataStore<Preferences>
) : AppContainer {
    override val airportRepository: AirportRepository by lazy {
        OfflineAirportRepository(FlightDatabase.getDatabase(context).airportDao())
    }
    override val favoriteRepository: FavoriteRepository by lazy {
        OfflineFavoriteRepository(FlightDatabase.getDatabase((context)).favoriteDao())
    }
    override val userInputRepository: UserInputRepository by lazy {
        UserInputRepository(dataStore)
    }
}