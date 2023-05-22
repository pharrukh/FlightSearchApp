package com.normuradov.flightsearchapp.ui.flight

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Exception

class FlightViewModel(
    private val airportRepository: AirportRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            getFavorites().collect { flights ->
                _uiState.value = UiState(flights)
            }
        }
    }

    fun getFavorites(): Flow<List<Flight>> = favoriteRepository.getAll().map { favorites ->
        val airports = airportRepository.getAll().stateIn(viewModelScope).value

        val flights: MutableList<Flight> = mutableListOf()
        for (airport in airports) {
            for (airport2 in airports) {
                if (airport.name == airport2.name) continue
                val favorite = favorites.find { favorite ->
                    favorite.departureCode == airport.code
                            && favorite.destinationCode == airport2.code
                }
                val flight =
                    Flight(
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

    fun getFlights(): Flow<List<Flight>> =
        airportRepository.getAll().map {
            val favorites = favoriteRepository.getAll().stateIn(viewModelScope).value

            val flights: MutableList<Flight> = mutableListOf()
            for (airport in it) {
                for (airport2 in it) {
                    if (airport.name == airport2.name) continue
                    val favorite = favorites.find { favorite ->
                        favorite.departureCode == airport.code
                                && favorite.destinationCode == airport2.code
                    }
                    val flight =
                        Flight(
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


    suspend fun saveFavorite(flightUiState: Flight) {
        favoriteRepository.insert(flightUiState.toFavorite())
    }

    suspend fun removeFavorite(flightUiState: Flight) {
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

data class UiState(val flights: List<Flight> = listOf())