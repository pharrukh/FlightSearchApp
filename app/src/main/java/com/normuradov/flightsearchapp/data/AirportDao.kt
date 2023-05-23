package com.normuradov.flightsearchapp.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {

    @Query("SELECT * FROM airport ORDER BY id ASC")
    suspend fun getAll(): List<Airport>

    @Query(
        "SELECT * FROM airport " +
                "WHERE name      LIKE  '%' || :query || '%' " +
                "   OR iata_code LIKE  '%' || :query || '%' " +
                "ORDER BY passengers"
    )
    suspend fun search(query: String): List<Airport>
}