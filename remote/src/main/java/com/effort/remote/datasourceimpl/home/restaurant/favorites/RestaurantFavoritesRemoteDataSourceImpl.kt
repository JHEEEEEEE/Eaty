package com.effort.remote.datasourceimpl.home.restaurant.favorites

import com.effort.data.datasource.home.restaurant.favorites.RestaurantFavoritesRemoteDataSource
import com.effort.data.model.home.restaurant.RestaurantEntity
import com.effort.remote.model.home.restaurant.toRemote
import com.effort.remote.service.home.restaurant.favorites.FavoriteService
import javax.inject.Inject

class RestaurantFavoritesRemoteDataSourceImpl @Inject constructor(
    private val favoriteService: FavoriteService
) : RestaurantFavoritesRemoteDataSource {

    override suspend fun addRestaurantToFavorites(
        userId: String, restaurant: RestaurantEntity
    ): Boolean {
        return favoriteService.addRestaurantToFavorites(userId, restaurant.toRemote())
    }

    override suspend fun removeRestaurantFromFavorites(
        userId: String, restaurantName: String
    ): Boolean {
        return favoriteService.removeRestaurantFromFavorites(userId, restaurantName)
    }

    override suspend fun checkIfRestaurantIsFavorite(
        userId: String, restaurantName: String
    ): Boolean {
        return favoriteService.checkIfRestaurantIsFavorite(userId, restaurantName)
    }

    override suspend fun getFavorites(userId: String): List<RestaurantEntity> {
        return favoriteService.getFavorites(userId).resultFavorites.map { it.toData() }
    }
}