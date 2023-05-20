package com.normuradov.flightsearchapp.ui.flight

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.normuradov.flightsearchapp.FlightApplication
import com.normuradov.flightsearchapp.data.AirportRepository
import com.normuradov.flightsearchapp.data.Favorite
import com.normuradov.flightsearchapp.data.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Exception

class FlightViewModel(
    private val airportRepository: AirportRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    fun getFavorites(): Flow<List<Favorite>> = favoriteRepository.getAll()
    fun getFlights(): Flow<List<FlightUiState>> =
        airportRepository.getAll().map {
            val favorites = favoriteRepository.getAll().map { favorites ->
                favorites
            }.stateIn(
                scope = viewModelScope,
            ).value

            val flights: MutableList<FlightUiState> = mutableListOf()
            for (airport in it) {
                for (airport2 in it) {
                    if (airport.name == airport2.name) continue
                    val favorite = favorites.find { favorite ->
                        favorite.departureCode == airport.code
                                && favorite.destinationCode == airport2.code
                    }
                    val flight =
                        FlightUiState(
                            favoriteId = favorite?.id ?: 0,
                            isFavorite = favorite != null,
                            departureCode = airport.code,
                            departureName = airport.name,
                            arrivalCode = airport2.code,
                            arrivalName = airport2.name
                        )
                    flights.add(flight)
                }
            }
            flights
        }


    suspend fun saveFavorite(flightUiState: FlightUiState) {
        favoriteRepository.insert(flightUiState.toFavorite())
    }

    suspend fun removeFavorite(flightUiState: FlightUiState) {
        if (flightUiState.favoriteId == 0) throw Exception("Id must not be zero!")
        favoriteRepository.delete(flightUiState.toFavorite())
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[APPLICATION_KEY] as FlightApplication)
                FlightViewModel(
                    airportRepository = application.container.airportRepository,
                    favoriteRepository = application.container.favoriteRepository
                )
            }
        }
    }
}