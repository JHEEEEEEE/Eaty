package com.effort.data.datasource.home.detail

import com.effort.data.model.home.blog.BlogReviewEntity
import com.effort.data.model.home.blog.BlogReviewMetaEntity
import com.effort.data.model.home.weather.WeatherEntity

interface RestaurantDetailRemoteDataSource {

    suspend fun getBlogReviews(query: String, page: Int): Pair<List<BlogReviewEntity>, BlogReviewMetaEntity?>

    suspend fun getWeatherData(latitude: String, longitude: String): List<WeatherEntity>
}