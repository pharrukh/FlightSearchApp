package com.normuradov.flightsearchapp.ui.flight

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.normuradov.flightsearchapp.FlightApplication
import com.normuradov.flightsearchapp.data.Airport
import com.normuradov.flightsearchapp.data.AirportRepository
import com.normuradov.flightsearchapp.data.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.Exception

class FlightViewModel(
    private val airportRepository: AirportRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            val airports = airportRepository.getAll()
            getAllAirportsFavoriteFlights(airports).collect { flights ->
                _uiState.value = _uiState.value.copy(favoriteFlights = flights)
            }

            _text.debounce(1000).collect {
                updateText(it)
            }
        }
    }

    fun filterAirports(query: String) {
        viewModelScope.launch {
            val airports = airportRepository.search(query)
            _uiState.value = _uiState.value.copy(airports = airports)
        }
    }

    fun showFlights() {
        _uiState.value = _uiState.value.copy(
            mode = HomeScreenMode.ShowSearchResults,
        )
        Log.v("DEBUG", _uiState.value.searchedFlights.size.toString())
        Log.v("DEBUG", _uiState.value.mode.toString())
    }

    fun updateText(text: String) {
        _text.value = text
        if (_text.value == "") {
            _uiState.value = _uiState.value.copy(mode = HomeScreenMode.ShowFavorites)
        } else {
            _uiState.value = _uiState.value.copy(mode = HomeScreenMode.SearchForAirport)
            filterAirports(text)
        }
    }

    fun getFlightsBasedOn(airport: Airport) {
        viewModelScope.launch {
            val allAirports = airportRepository.getAll()
            getOneAirportFlights(
                airport = airport, airports = allAirports
            ).collect { flights ->
                _uiState.value = _uiState.value.copy(searchedFlights = flights)
            }
        }
    }

    fun getOneAirportFlights(airport: Airport, airports: List<Airport>): Flow<List<Flight>> =
        favoriteRepository.getAll().map { favorites ->
            val flights: MutableList<Flight> = mutableListOf()
            for (airport2 in airports) {
                if (airport.name == airport2.name) continue
                val favorite = favorites.find { favorite ->
                    favorite.departureCode == airport.code && favorite.destinationCode == airport2.code
                }
                val flight = Flight(
                    favoriteId = favorite?.id ?: 0,
                    isFavorite = favorite != null,
                    departureCode = airport.code,
                    departureName = airport.name,
                    arrivalCode = airport2.code,
                    arrivalName = airport2.name
                )
                flights.add(flight)
            }
            flights
        }

    fun getAllAirportsFavoriteFlights(airports: List<Airport>): Flow<List<Flight>> =
        favoriteRepository.getAll().map { favorites ->
            val flights: MutableList<Flight> = mutableListOf()
            for (favorite in favorites) {
                val departureAirport = airports.find { a -> a.code == favorite.departureCode }
                val arrivalAirport = airports.find { a -> a.code == favorite.destinationCode }
                flights.add(
                    Flight(
                        favoriteId = favorite.id,
                        isFavorite = true,
                        departureCode = favorite.departureCode,
                        departureName = departureAirport?.name ?: "",
                        arrivalCode = favorite.destinationCode,
                        arrivalName = arrivalAirport?.name ?: ""
                    )
                )
            }
            flights
        }


    suspend fun saveFavorite(flight: Flight) {
        favoriteRepository.insert(flight.toFavorite())
    }

    suspend fun removeFavorite(flight: Flight) {
        if (flight.favoriteId == 0) throw Exception("Id must not be zero!")
        favoriteRepository.delete(flight.toFavorite())
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightApplication)
                FlightViewModel(
                    airportRepository = application.container.airportRepository,
                    favoriteRepository = application.container.favoriteRepository
                )
            }
        }
    }
}

data class UiState(
    val airports: List<Airport> = listOf(),
    val searchedFlights: List<Flight> = listOf(),
    val favoriteFlights: List<Flight> = listOf(),
    val mode: HomeScreenMode = HomeScreenMode.ShowFavorites
)

enum class HomeScreenMode {
    SearchForAirport, ShowFavorites, ShowSearchResults
}