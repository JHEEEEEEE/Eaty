package com.effort.remote.service.home.restaurant.detail.weather

import com.effort.remote.model.home.restaurant.detail.weather.WeatherWrapperResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("forecast")
    suspend fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",    // 섭씨 온도 설정
        @Query("lang") lang: String = "kr"           // 한국어 설정
    ): WeatherWrapperResponse
}