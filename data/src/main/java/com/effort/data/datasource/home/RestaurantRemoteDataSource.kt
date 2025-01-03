package com.effort.data.datasource.home

import com.effort.data.model.home.RestaurantEntity
import com.effort.data.model.home.RestaurantMetaEntity

interface RestaurantRemoteDataSource {

    suspend fun getRestaurants(query: String, page: Int): Pair<List<RestaurantEntity>, RestaurantMetaEntity>
}