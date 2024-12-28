package com.effort.domain.usecase.home

import com.effort.domain.DataResource
import com.effort.domain.model.home.Restaurant
import kotlinx.coroutines.flow.Flow

interface GetRestaurantListUseCase {

    operator fun invoke(query: String): Flow<DataResource<List<Restaurant>>>
}