package com.effort.data.model.home

import com.effort.domain.model.home.Restaurant

data class RestaurantEntity(
    val title: String
) {
    fun toDomain(): Restaurant =
        Restaurant(title)
}
