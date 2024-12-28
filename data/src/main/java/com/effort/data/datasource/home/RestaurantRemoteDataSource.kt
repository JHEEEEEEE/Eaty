package com.effort.data.datasource.home

import com.effort.data.model.home.RestaurantEntity

interface RestaurantRemoteDataSource {

    suspend fun getRestaurants(query: String): List<RestaurantEntity>
}