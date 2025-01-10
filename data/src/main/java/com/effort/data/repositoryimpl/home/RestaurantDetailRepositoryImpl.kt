package com.effort.data.repositoryimpl.home

import android.util.Log
import com.effort.data.datasource.home.detail.RestaurantDetailLocalDataSource
import com.effort.data.datasource.home.detail.RestaurantDetailRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.home.blog.BlogReview
import com.effort.domain.model.home.blog.BlogReviewMeta
import com.effort.domain.model.home.parkinglot.ParkingLot
import com.effort.domain.model.home.weather.Weather
import com.effort.domain.repository.home.RestaurantDetailRepository
import javax.inject.Inject

class RestaurantDetailRepositoryImpl @Inject constructor(
    private val restaurantDetailLocalDataSource: RestaurantDetailLocalDataSource,
    private val restaurantDetailRemoteDataSource: RestaurantDetailRemoteDataSource
): RestaurantDetailRepository {

    override suspend fun getBlogReviews(query: String, page: Int): DataResource<Pair<List<BlogReview>, BlogReviewMeta?>> {
        DataResource.loading<Pair<List<BlogReview>, BlogReviewMeta?>>()

        return try {
            val (remoteBlogReviews, meta) = restaurantDetailRemoteDataSource.getBlogReviews(query, page)

            DataResource.success(Pair(remoteBlogReviews.map { it.toDomain() }, meta?.toDomain()))
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }

    override suspend fun getNearestParkingLots(latitude: String, longitude: String): DataResource<List<ParkingLot>> {

        Log.d("RestaurantDetailRepositoryImpl", "Starting getParkingLots()...")

        // 로딩 상태
        DataResource.loading<List<ParkingLot>>()

        return try {
            // Room 초기화 필요 여부 확인
            if (restaurantDetailLocalDataSource.isDatabaseEmpty()) {
                restaurantDetailLocalDataSource.initializeParkingLotsFromJson()
            }

            DataResource.success(restaurantDetailLocalDataSource.getNearestParkingLots(latitude, longitude).map { it.toDomain() })

        } catch (e: Exception) {
            Log.e("RestaurantDetailRepositoryImpl", "조회 실패: ${e.message}", e)
            DataResource.error(e)
        }
    }

    override suspend fun getWeatherData(
        latitude: String,
        longitude: String
    ): DataResource<List<Weather>> {

        DataResource.loading<List<Weather>>()

        return try {
            DataResource.success(
                restaurantDetailRemoteDataSource.getWeatherData(
                    latitude,
                    longitude
                ).map { it.toDomain() })
        } catch (e: Exception) {
            Log.e("RestaurantDetailRepositoryImpl", "조회 실패: ${e.message}", e)
            DataResource.error(e)
        }
    }
}