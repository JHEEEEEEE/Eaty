package com.effort.remote.datasourceimpl.home.restaurant

import android.util.Log
import com.effort.data.datasource.home.restaurant.RestaurantRemoteDataSource
import com.effort.data.model.home.restaurant.RestaurantEntity
import com.effort.data.model.home.restaurant.RestaurantMetaEntity
import com.effort.remote.service.home.restaurant.RestaurantService
import com.effort.remote.service.location.LocationService
import javax.inject.Inject

class RestaurantRemoteDataSourceImpl @Inject constructor(
    private val restaurantService: RestaurantService,
    private val locationService: LocationService,
): RestaurantRemoteDataSource {

    override suspend fun getRestaurants(query: String, page: Int): Pair<List<RestaurantEntity>, RestaurantMetaEntity> {
        return try {
            // 1. 현재 위치 가져오기
            val location = locationService.getCurrentLocation() ?: throw IllegalStateException("위치 정보를 가져올 수 없습니다.")
            Log.d("LocationInfo", "위도: ${location.latitude}, 경도: ${location.longitude}")

            // 2. API 요청
            val response = restaurantService.getRestaurantList(
                query = query,
                latitude = location.latitude.toString(),
                longitude = location.longitude.toString(),
                page = page
            )


            // 3. 응답 처리
            if (response.documents.isNotEmpty()) { // 응답 결과 유효성 검사
                Log.d("RestaurantResponse", "${response.documents}")
                val data = response.documents.map { it.toData() }
                Pair(data, response.meta.toData()) // 데이터 + 메타 정보 반환

            } else {
                Log.e("RestaurantRemote", "API 응답 결과가 비어 있습니다.")
                throw IllegalStateException("검색 결과가 없습니다.") // 예외로 처리
            }

        } catch (e: Exception) {
            // 4. 예외 처리
            Log.e("RestaurantRemote", "API 요청 실패: ${e.message}", e)
            throw e // 예외 전파
        }
    }
}