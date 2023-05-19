package com.normuradov.flightsearchapp.ui.theme

import com.normuradov.flightsearchapp.data.Favorite

data class FlightUiState(
    val favoriteId: Int = 0,
    val isFavorite: Boolean = false,
    val departureCode: String = "",
    val departureName: String = "",
    val arrivalCode: String = "",
    val arrivalName: String = ""
)

fun FlightUiState.toFavorite(): Favorite = Favorite(
    id = favoriteId,
    departureCode = departureCode,
    destinationCode = arrivalCode
)