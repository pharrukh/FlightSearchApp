package com.normuradov.flightsearchapp.data

import kotlinx.coroutines.flow.Flow

interface AirportRepository {
    fun getAll(): Flow<List<Airport>>
    fun search(query: String): Flow<List<Airport>>
}