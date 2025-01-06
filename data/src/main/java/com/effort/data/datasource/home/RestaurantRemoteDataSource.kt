package com.effort.data.datasource.home

import com.effort.data.model.home.restaurant.RestaurantEntity
import com.effort.data.model.home.restaurant.RestaurantMetaEntity

interface RestaurantRemoteDataSource {

    suspend fun getRestaurants(query: String, page: Int): Pair<List<RestaurantEntity>, RestaurantMetaEntity>
}