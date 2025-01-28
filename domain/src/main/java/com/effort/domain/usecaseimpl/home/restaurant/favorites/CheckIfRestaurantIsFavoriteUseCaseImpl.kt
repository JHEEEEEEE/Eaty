package com.effort.domain.usecaseimpl.home.restaurant.favorites

import com.effort.domain.DataResource
import com.effort.domain.repository.home.restaurant.favorites.RestaurantFavoritesRepository
import com.effort.domain.usecase.home.restaurant.favorites.CheckIfRestaurantIsFavoriteUseCase
import javax.inject.Inject

class CheckIfRestaurantIsFavoriteUseCaseImpl @Inject constructor(
    private val restaurantFavoritesRepository: RestaurantFavoritesRepository
) : CheckIfRestaurantIsFavoriteUseCase {

    override suspend fun invoke(userId: String, restaurantName: String): DataResource<Boolean> {
        return restaurantFavoritesRepository.checkIfRestaurantIsFavorite(userId, restaurantName)
    }
}