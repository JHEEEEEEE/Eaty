package com.effort.data.model.home.restaurant

import com.effort.domain.model.home.restaurant.RestaurantMeta

data class RestaurantMetaEntity(
    val totalCount: Int, val pageableCount: Int, val isEnd: Boolean
) {
    fun toDomain(): RestaurantMeta {
        return RestaurantMeta(totalCount, pageableCount, isEnd)
    }
}