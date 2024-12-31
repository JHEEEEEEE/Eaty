package com.effort.remote.datasourceimpl.home

import com.effort.data.datasource.home.RestaurantRemoteDataSource
import com.effort.data.model.home.RestaurantEntity
import com.effort.remote.service.home.RestaurantService
import com.effort.remote.service.location.LocationService
import javax.inject.Inject

class RestaurantRemoteDataSourceImpl @Inject constructor(
    private val restaurantService: RestaurantService,
    private val locationService: LocationService
): RestaurantRemoteDataSource {

    override suspend fun getRestaurants(query: String): List<RestaurantEntity> {

        return try {
            // 1. API 요청
            val response = restaurantService.getRestaurantList(query)

            // 2. 응답 성공 여부 확인
            if (response.isSuccessful) {
                val result = response.body()?.items?.map {
                    it.toData()
                } ?: emptyList()

                result
            } else {
                // 4. 에러 처리
                val errorMessage = "API Error: ${response.errorBody()?.string()}"
                throw Exception(errorMessage)
            }
        } catch (e: Exception) {
            // 5. 예외 처리 및 로깅
            throw e // 예외를 다시 던져서 상위 레이어에서 처리하도록 함
        }
    }
}