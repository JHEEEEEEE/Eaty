package com.effort.domain.repository.home

import com.effort.domain.DataResource
import com.effort.domain.model.home.blog.BlogReview
import com.effort.domain.model.home.blog.BlogReviewMeta
import com.effort.domain.model.home.parkinglot.ParkingLot
import com.effort.domain.model.home.weather.Weather

interface RestaurantDetailRepository {

    suspend fun getBlogReviews(query: String, page: Int): DataResource<Pair<List<BlogReview>, BlogReviewMeta?>>

    suspend fun getNearestParkingLots(latitude: String, longitude: String): DataResource<List<ParkingLot>>

    suspend fun getWeatherData(latitude: String, longitude: String): DataResource<List<Weather>>
}