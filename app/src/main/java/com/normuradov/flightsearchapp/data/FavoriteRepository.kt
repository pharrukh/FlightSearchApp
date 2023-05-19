package com.normuradov.flightsearchapp.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAll(): Flow<List<Favorite>>
    suspend fun insert(favorite: Favorite)
    suspend fun delete(favorite: Favorite)
}