package com.effort.domain.repository.home.restaurant

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.Restaurant
import com.effort.domain.model.home.restaurant.RestaurantMeta
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {

    fun getRestaurantList(query: String, page: Int): Flow<DataResource<Pair<List<Restaurant>, RestaurantMeta?>>>
}