package com.normuradov.flightsearchapp.data

import kotlinx.coroutines.flow.Flow

class OfflineFavoriteRepository(private val dao: FavoriteDao) : FavoriteRepository {
    override fun getAll(): Flow<List<Favorite>> = dao.getAll()

    override suspend fun insert(favorite: Favorite) = dao.insert(favorite)

    override suspend fun delete(favorite: Favorite) = dao.delete(favorite)
}