package com.effort.domain.usecaseimpl.home

import com.effort.domain.DataResource
import com.effort.domain.model.home.Restaurant
import com.effort.domain.repository.home.RestaurantRepository
import com.effort.domain.usecase.home.GetRestaurantListUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRestaurantListUseCaseImpl @Inject constructor(
    private val restaurantRepository: RestaurantRepository
): GetRestaurantListUseCase {

    override fun invoke(query: String): Flow<DataResource<List<Restaurant>>> {
        return restaurantRepository.getRestaurantList(query)
    }
}