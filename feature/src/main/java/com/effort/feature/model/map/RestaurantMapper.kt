package com.effort.feature.model.map

import com.effort.presentation.model.home.restaurant.RestaurantModel
import com.naver.maps.geometry.LatLng

object RestaurantMapper {
    // RestaurantModel -> RestaurantKey 변환
    fun toRestaurantKey(model: RestaurantModel): RestaurantKey {
        return RestaurantKey(
            title = model.title,
            position = LatLng(model.latitude.toDouble(), model.longitude.toDouble())
        )
    }

    // RestaurantModel 리스트 -> RestaurantKey 리스트 변환
    fun toRestaurantKeyList(models: List<RestaurantModel>): List<RestaurantKey> {
        return models.map { toRestaurantKey(it) }
    }

    // RestaurantKey -> RestaurantModel 변환 (title을 기준으로)
    fun toRestaurantModel(key: RestaurantKey, models: List<RestaurantModel>): RestaurantModel? {
        return models.find { it.title == key.title }
    }
}