package com.effort.domain.usecaseimpl.home.restaurant.favorites

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.Restaurant
import com.effort.domain.repository.home.restaurant.favorites.RestaurantFavoritesRepository
import com.effort.domain.usecase.home.restaurant.favorites.AddRestaurantToFavoritesUseCase
import javax.inject.Inject

class AddRestaurantToFavoritesUseCaseImpl @Inject constructor(
    private val addRestaurantFavoritesRepository: RestaurantFavoritesRepository
) : AddRestaurantToFavoritesUseCase {

    override suspend fun invoke(userId: String, restaurant: Restaurant): DataResource<Boolean> {
        return addRestaurantFavoritesRepository.addRestaurantToFavorites(userId, restaurant)
    }
}