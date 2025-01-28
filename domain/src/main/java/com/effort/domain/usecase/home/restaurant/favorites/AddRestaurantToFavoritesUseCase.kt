package com.effort.domain.usecase.home.restaurant.favorites

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.Restaurant

interface AddRestaurantToFavoritesUseCase {

    suspend operator fun invoke(userId: String, restaurant: Restaurant): DataResource<Boolean>
}