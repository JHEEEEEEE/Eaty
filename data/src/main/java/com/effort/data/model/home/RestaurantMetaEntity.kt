package com.effort.data.model.home

import com.effort.domain.model.home.RestaurantMeta

data class RestaurantMetaEntity (
    val totalCount: Int,
    val pageableCount: Int,
    val isEnd: Boolean
) {
    fun toDomain(): RestaurantMeta {
        return RestaurantMeta(totalCount, pageableCount, isEnd)
    }
}