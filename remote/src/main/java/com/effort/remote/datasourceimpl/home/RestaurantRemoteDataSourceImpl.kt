package com.effort.remote.datasourceimpl.home

import android.util.Log
import com.effort.data.datasource.home.RestaurantRemoteDataSource
import com.effort.data.model.home.RestaurantEntity
import com.effort.remote.service.home.RestaurantService
import javax.inject.Inject

class RestaurantRemoteDataSourceImpl @Inject constructor(
    private val restaurantService: RestaurantService
): RestaurantRemoteDataSource {

    override suspend fun getRestaurants(query: String): List<RestaurantEntity> {
        Log.d("RemoteDataSource", "getRestaurants() - Query: $query") // 쿼리 로그

        return try {
            // 1. API 요청
            val response = restaurantService.getRestaurantList(query)
            Log.d("RemoteDataSource", "getRestaurants() - API Response: $response") // 응답 로그

            // 2. 응답 성공 여부 확인
            if (response.isSuccessful) {
                val result = response.body()?.items?.map {
                    Log.d("RemoteDataSource", "getRestaurants() - Mapping to entity: $it") // 매핑 중 로그
                    it.toData()
                } ?: emptyList()

                Log.d("RemoteDataSource", "getRestaurants() - Success: $result") // 성공 로그
                result
            } else {
                // 4. 에러 처리
                val errorMessage = "API Error: ${response.errorBody()?.string()}"
                Log.e("RemoteDataSource", "getRestaurants() - Error: $errorMessage") // 에러 로그
                throw Exception(errorMessage)
            }
        } catch (e: Exception) {
            // 5. 예외 처리 및 로깅
            Log.e("RemoteDataSource", "Failed to fetch restaurants: ${e.message}") // 예외 로그
            throw e // 예외를 다시 던져서 상위 레이어에서 처리하도록 함
        }
    }
}