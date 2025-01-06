package com.effort.domain.usecase.home.restaurant

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.Restaurant
import com.effort.domain.model.home.restaurant.RestaurantMeta
import com.effort.domain.model.home.restaurant.SortType
import kotlinx.coroutines.flow.Flow

interface GetRestaurantListUseCase {

    operator fun invoke(query: String, sortType: SortType, page: Int): Flow<DataResource<Pair<List<Restaurant>, RestaurantMeta?>>>
}