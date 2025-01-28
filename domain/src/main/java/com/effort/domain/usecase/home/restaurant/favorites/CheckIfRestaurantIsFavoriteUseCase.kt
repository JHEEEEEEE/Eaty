package com.effort.domain.usecase.home.restaurant.favorites

import com.effort.domain.DataResource

interface CheckIfRestaurantIsFavoriteUseCase {

    suspend operator fun invoke(userId: String, restaurantName: String): DataResource<Boolean>
}