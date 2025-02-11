package com.effort.remote.model.home.restaurant.detail.weather

import com.effort.data.model.home.restaurant.detail.weather.WeatherEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("dt_txt")
    val dateTimeText: String = "", // 날짜 및 시간 (예: "2022-08-30 15:00:00")

    @SerialName("main")
    val temperatureInfo: WeatherTemperature = WeatherTemperature(), // 온도 정보

    @SerialName("weather")
    val weatherConditions: List<WeatherCondition> = emptyList() // 날씨 상태 정보
) {

    // 변환 함수: WeatherModel로 변환
    fun toData(): WeatherEntity {
        return WeatherEntity(
            dateTime = dateTimeText.substring(11, 16), // 시간만 추출 (HH:mm)
            temp = temperatureInfo.temp,
            condition = weatherConditions.firstOrNull()?.condition ?: "정보 없음",
            id = weatherConditions.firstOrNull()?.id ?: 0,
        )
    }
}