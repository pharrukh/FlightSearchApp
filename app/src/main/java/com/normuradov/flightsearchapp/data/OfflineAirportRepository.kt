package com.normuradov.flightsearchapp.data

import kotlinx.coroutines.flow.Flow

class OfflineAirportRepository(private val dao: AirportDao) : AirportRepository {
    override fun getAll(): Flow<List<Airport>> = dao.getAll()
    override fun search(query: String): Flow<List<Airport>> = dao.search(query)
}