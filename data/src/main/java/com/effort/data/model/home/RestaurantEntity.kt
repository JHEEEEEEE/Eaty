package com.effort.data.model.home

import com.effort.domain.model.home.Restaurant

data class RestaurantEntity(
    val title: String,
    val mapx: String,
    val mapy: String,
) {
    fun toDomain(): Restaurant =
        Restaurant(
            title = this.title,
            mapx = this.mapx,
            mapy = this.mapy,
        )
}
