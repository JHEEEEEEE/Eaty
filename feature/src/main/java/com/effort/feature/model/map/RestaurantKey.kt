package com.effort.feature.model.map

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey

data class RestaurantKey(
    val title: String, // 고유 식별자
    private val position: LatLng
) : ClusteringKey {
    override fun getPosition(): LatLng = position

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val key = other as RestaurantKey
        return title == key.title
    }

    override fun hashCode(): Int = title.hashCode()
}