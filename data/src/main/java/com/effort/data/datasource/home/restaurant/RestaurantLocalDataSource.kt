package com.effort.data.datasource.home.restaurant

import com.effort.data.model.home.restaurant.RestaurantEntity

interface RestaurantLocalDataSource {

    suspend fun getRestaurantList(query: String): List<RestaurantEntity>

    suspend fun insertRestaurantList(restaurants: List<RestaurantEntity>)
}