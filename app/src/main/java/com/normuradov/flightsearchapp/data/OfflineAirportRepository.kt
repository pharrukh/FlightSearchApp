package com.normuradov.flightsearchapp.data

import kotlinx.coroutines.flow.Flow

class OfflineAirportRepository(private val dao: AirportDao) : AirportRepository {
    override suspend fun getAll(): List<Airport> = dao.getAll()
    override suspend fun search(query: String): List<Airport> = dao.search(query)
}