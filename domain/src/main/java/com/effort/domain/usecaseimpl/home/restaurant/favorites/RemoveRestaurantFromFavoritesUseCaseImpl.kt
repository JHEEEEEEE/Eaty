package com.effort.domain.usecaseimpl.home.restaurant.favorites

import com.effort.domain.DataResource
import com.effort.domain.repository.home.restaurant.favorites.RestaurantFavoritesRepository
import com.effort.domain.usecase.home.restaurant.favorites.RemoveRestaurantFromFavoritesUseCase
import javax.inject.Inject

class RemoveRestaurantFromFavoritesUseCaseImpl @Inject constructor(
    private val restaurantFavoritesRepository: RestaurantFavoritesRepository
) : RemoveRestaurantFromFavoritesUseCase {

    override suspend fun invoke(userId: String, restaurantName: String): DataResource<Boolean> {
        return restaurantFavoritesRepository.removeRestaurantFromFavorites(userId, restaurantName)
    }
}