package com.effort.remote.model.home.restaurant.detail.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherWrapperResponse(

    @SerialName("list")
    val weatherList: List<WeatherResponse> // 시간별 날씨 예보 리스트
)