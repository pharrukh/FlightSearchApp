package com.normuradov.flightsearchapp.data

import kotlinx.coroutines.flow.Flow

interface AirportRepository {
    suspend fun getAll(): List<Airport>
    suspend fun search(query: String): List<Airport>
}