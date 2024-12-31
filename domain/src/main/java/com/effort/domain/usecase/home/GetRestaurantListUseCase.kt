package com.effort.domain.usecase.home

import com.effort.domain.DataResource
import com.effort.domain.model.home.Restaurant
import com.effort.domain.model.home.SortType
import kotlinx.coroutines.flow.Flow

interface GetRestaurantListUseCase {

    operator fun invoke(query: String, sortType: SortType): Flow<DataResource<List<Restaurant>>>
}