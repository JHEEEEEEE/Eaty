package com.effort.domain.usecase.home.restaurant.favorites

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.Restaurant
import kotlinx.coroutines.flow.Flow

interface GetFavoriteListUseCase {

    operator fun invoke(userId: String): Flow<DataResource<List<Restaurant>>>
}