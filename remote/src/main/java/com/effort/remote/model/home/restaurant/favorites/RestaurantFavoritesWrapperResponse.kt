package com.effort.remote.model.home.restaurant.favorites

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantFavoritesWrapperResponse(

    @SerialName("favorite_results") val resultFavorites: List<RestaurantFavoritesResponse>
)