package com.effort.domain.repository.home

import com.effort.domain.DataResource
import com.effort.domain.model.home.Restaurant
import com.effort.domain.model.home.SortType
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {

    fun getRestaurantList(query: String): Flow<DataResource<List<Restaurant>>>
}