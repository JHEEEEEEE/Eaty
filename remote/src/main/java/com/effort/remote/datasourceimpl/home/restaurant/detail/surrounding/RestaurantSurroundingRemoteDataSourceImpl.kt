package com.effort.remote.datasourceimpl.home.restaurant.detail.surrounding

import android.util.Log
import com.effort.data.datasource.home.restaurant.detail.surrounding.RestaurantSurroundingRemoteDataSource
import com.effort.data.model.home.restaurant.detail.subway.SubwayEntity
import com.effort.data.model.home.restaurant.detail.weather.WeatherEntity
import com.effort.remote.BuildConfig
import com.effort.remote.service.home.restaurant.detail.subway.SubwayService
import com.effort.remote.service.home.restaurant.detail.weather.WeatherService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class RestaurantSurroundingRemoteDataSourceImpl @Inject constructor(
    private val weatherService: WeatherService,
    private val subwayService: SubwayService
) : RestaurantSurroundingRemoteDataSource {

    /**
     * 외부 API에서 날씨 데이터를 가져온다.
     * - 현재 날짜(today) 기준으로 특정 시간대(9, 12, 15, 18, 21)만 필터링하여 반환
     * - API 응답 데이터를 Entity 형태로 변환
     * - 예외 발생 시 로그 출력 후 예외 처리
     */
    override suspend fun getWeatherData(latitude: String, longitude: String): List<WeatherEntity> {
        return try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(Date())
            val response = weatherService.getWeatherData(
                latitude = latitude.toDouble(),
                longitude = longitude.toDouble(),
                apiKey = BuildConfig.OPEN_WEATHER_API_KEY,
            )

            val filteredData = response.weatherList.filter { data ->
                val date = data.dateTimeText.substring(0, 10)
                val hour = data.dateTimeText.substring(11, 13).toInt()
                date == today && hour in listOf(9, 12, 15, 18, 21) // 특정 시간대 데이터만 필터링
            }

            filteredData.map { it.toData() }.onEach { entity ->
                Log.d("WeatherEntity", "temperature: ${entity.temp}, description: ${entity.condition}")
            }
        } catch (e: Exception) {
            Log.e("WeatherDataError", "Failed to fetch weather data: ${e.message}")
            throw Exception("날씨 데이터 조회 실패: ${e.message}") // 예외 발생 시 처리
        }
    }

    /**
     * 외부 API에서 주변 지하철역 정보를 가져온다.
     * - 위도/경도를 기반으로 가까운 지하철역 목록을 조회하여 반환
     */
    override suspend fun getSubwayStation(latitude: String, longitude: String): List<SubwayEntity> {
        val response = subwayService.getSubwayStation(latitude = latitude, longitude = longitude)
        return response.documents.map { it.toData() }
    }
}