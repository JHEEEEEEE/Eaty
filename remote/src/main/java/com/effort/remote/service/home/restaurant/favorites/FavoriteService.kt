package com.effort.remote.service.home.restaurant.favorites

import com.effort.remote.model.home.restaurant.RestaurantResponse
import com.effort.remote.model.home.restaurant.favorites.RestaurantFavoritesWrapperResponse

interface FavoriteService {

    suspend fun addRestaurantToFavorites(userId: String, restaurant: RestaurantResponse): Boolean

    suspend fun removeRestaurantFromFavorites(userId: String, restaurantName: String): Boolean

    suspend fun checkIfRestaurantIsFavorite(userId: String, restaurantName: String): Boolean

    suspend fun getFavorites(userId: String): RestaurantFavoritesWrapperResponse
}