package com.effort.remote.datasourceimpl.home

import android.util.Log
import com.effort.data.datasource.home.detail.RestaurantDetailRemoteDataSource
import com.effort.data.model.home.blog.BlogReviewEntity
import com.effort.data.model.home.blog.BlogReviewMetaEntity
import com.effort.data.model.home.weather.WeatherEntity
import com.effort.remote.BuildConfig
import com.effort.remote.service.home.blog.BlogReviewService
import com.effort.remote.service.home.weather.WeatherService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class RestaurantDetailRemoteDataSourceImpl @Inject constructor(
    private val blogReviewService: BlogReviewService,
    private val weatherService: WeatherService
) : RestaurantDetailRemoteDataSource {

    override suspend fun getBlogReviews(
        query: String,
        page: Int
    ): Pair<List<BlogReviewEntity>, BlogReviewMetaEntity?> {
        return try {
            val response = blogReviewService.getBlogReviewList(query, page)
            val data = response.documents.map { it.toData() }
            val meta = response.meta.toData()

            Pair(data, meta)
        } catch (e: Exception) {
            // 4. 예외 처리
            Log.e("RestaurantDetailRemote", "API 요청 실패: ${e.message}", e)
            throw e // 예외 전파
        }
    }

    override suspend fun getWeatherData(latitude: String, longitude: String): List<WeatherEntity> {
        return try {
            // 오늘 날짜 가져오기
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(Date())

            // 요청 수행
            val response = weatherService.getWeatherData(
                latitude = latitude.toDouble(),
                longitude = longitude.toDouble(),
                apiKey = BuildConfig.OPEN_WEATHER_API_KEY,
            )

            // 오늘 날짜 + 9시, 12시, 15시, 18시, 21시만 필터링
            val filteredData = response.weatherList.filter { data ->
                // 날짜와 시간 추출
                val date = data.dateTimeText.substring(0, 10) // yyyy-MM-dd
                val hour = data.dateTimeText.substring(11, 13).toInt() // 시간 (HH)

                // 오늘 날짜 + 특정 시간만 필터링
                date == today && hour in listOf(9, 12, 15, 18, 21)
            }

            filteredData.map { it.toData() }
        } catch (e: Exception) {
            throw Exception("날씨 데이터 조회 실패: ${e.message}")
        }
    }
}