package com.effort.data.datasource.home

import com.effort.data.model.home.RestaurantEntity

interface RestaurantLocalDataSource {

    suspend fun getRestaurantList(query: String): List<RestaurantEntity>

    suspend fun insertRestaurantList(restaurants: List<RestaurantEntity>)
}