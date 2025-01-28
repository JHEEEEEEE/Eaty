package com.effort.domain.usecaseimpl.home.restaurant.favorites

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.Restaurant
import com.effort.domain.repository.home.restaurant.favorites.RestaurantFavoritesRepository
import com.effort.domain.usecase.home.restaurant.favorites.GetFavoriteListUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteListUseCaseImpl @Inject constructor(
    private val restaurantFavoritesRepository: RestaurantFavoritesRepository
) : GetFavoriteListUseCase {

    override fun invoke(userId: String): Flow<DataResource<List<Restaurant>>> {
        return restaurantFavoritesRepository.getFavorites(userId)
    }
}