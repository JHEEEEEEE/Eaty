package com.effort.domain.repository.home.restaurant.favorites

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.Restaurant
import kotlinx.coroutines.flow.Flow

interface RestaurantFavoritesRepository {

    suspend fun addRestaurantToFavorites(userId: String, restaurant: Restaurant): DataResource<Boolean>

    suspend fun removeRestaurantFromFavorites(userId: String, restaurantName: String): DataResource<Boolean>

    suspend fun checkIfRestaurantIsFavorite(userId: String, restaurantName: String): DataResource<Boolean>

    fun getFavorites(userId: String): Flow<DataResource<List<Restaurant>>>
}