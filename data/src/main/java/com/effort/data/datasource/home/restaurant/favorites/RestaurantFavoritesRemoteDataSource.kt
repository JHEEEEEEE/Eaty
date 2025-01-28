package com.effort.data.datasource.home.restaurant.favorites

import com.effort.data.model.home.restaurant.RestaurantEntity

interface RestaurantFavoritesRemoteDataSource {

    suspend fun addRestaurantToFavorites(userId: String, restaurant: RestaurantEntity): Boolean

    suspend fun removeRestaurantFromFavorites(userId: String, restaurantName: String): Boolean

    suspend fun checkIfRestaurantIsFavorite(userId: String, restaurantName: String): Boolean

    suspend fun getFavorites(userId: String): List<RestaurantEntity>
}