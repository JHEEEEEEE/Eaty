package com.effort.presentation.model.home

import com.effort.domain.model.home.Restaurant

data class RestaurantModel(
    val title: String
)

fun Restaurant.toPresentation(): RestaurantModel {
    return RestaurantModel(
        title = this.title,
    )
}
