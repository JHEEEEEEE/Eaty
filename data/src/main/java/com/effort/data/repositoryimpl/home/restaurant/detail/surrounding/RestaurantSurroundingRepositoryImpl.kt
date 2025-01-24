package com.effort.data.repositoryimpl.home.restaurant.detail.surrounding

import android.util.Log
import com.effort.data.datasource.home.restaurant.detail.surrounding.RestaurantSurroundingLocalDataSource
import com.effort.data.datasource.home.restaurant.detail.surrounding.RestaurantSurroundingRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.home.parkinglot.ParkingLot
import com.effort.domain.model.home.weather.Weather
import com.effort.domain.repository.home.restaurant.detail.surrounding.RestaurantSurroundingRepository
import javax.inject.Inject

class RestaurantSurroundingRepositoryImpl @Inject constructor(
    private val restaurantSurroundingLocalDataSource: RestaurantSurroundingLocalDataSource,
    private val restaurantSurroundingRemoteDataSource: RestaurantSurroundingRemoteDataSource
) : RestaurantSurroundingRepository {
    override suspend fun getNearestParkingLots(
        latitude: String,
        longitude: String
    ): DataResource<List<ParkingLot>> {

        Log.d("RestaurantDetailRepositoryImpl", "Starting getParkingLots()...")

        // 로딩 상태
        DataResource.loading<List<ParkingLot>>()

        return try {
            // Room 초기화 필요 여부 확인
            if (restaurantSurroundingLocalDataSource.isDatabaseEmpty()) {
                restaurantSurroundingLocalDataSource.initializeParkingLotsFromJson()
            }

            DataResource.success(
                restaurantSurroundingLocalDataSource.getNearestParkingLots(
                    latitude,
                    longitude
                ).map { it.toDomain() })

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
                restaurantSurroundingRemoteDataSource.getWeatherData(
                    latitude,
                    longitude
                ).map { it.toDomain() })
        } catch (e: Exception) {
            Log.e("RestaurantDetailRepositoryImpl", "조회 실패: ${e.message}", e)
            DataResource.error(e)
        }
    }
}