package com.effort.remote.datasourceimpl.home.restaurant

import com.effort.data.datasource.home.restaurant.RestaurantRemoteDataSource
import com.effort.data.model.home.restaurant.RestaurantEntity
import com.effort.data.model.home.restaurant.RestaurantMetaEntity
import com.effort.remote.service.home.restaurant.RestaurantService
import com.effort.remote.service.location.LocationService
import timber.log.Timber
import javax.inject.Inject

class RestaurantRemoteDataSourceImpl @Inject constructor(
    private val restaurantService: RestaurantService,
    private val locationService: LocationService,
) : RestaurantRemoteDataSource {

    /**
     * 현재 위치를 기반으로 레스토랑 데이터를 조회한다.
     * - 위치 정보를 가져온 후 API 요청을 수행 (위치 정보가 없으면 예외 발생)
     * - API 응답을 받아 유효성 검사 후 데이터를 반환
     * - 응답이 비어 있을 경우 예외를 발생시켜 상위 계층에서 처리
     * - 예외 발생 시 Timber.e()로 오류 로깅 후 예외 전파
     */
    override suspend fun getRestaurants(
        query: String, page: Int
    ): Pair<List<RestaurantEntity>, RestaurantMetaEntity> {
        return try {
            Timber.d("getRestaurants() 호출 - 검색어: $query, 페이지: $page")

            val location = locationService.getCurrentLocation()
                ?: throw IllegalStateException("위치 정보를 가져올 수 없습니다.")

            Timber.d("현재 위치 - 위도: ${location.latitude}, 경도: ${location.longitude}")

            val response = restaurantService.getRestaurantList(
                query = query,
                latitude = location.latitude.toString(),
                longitude = location.longitude.toString(),
                page = page
            )

            if (response.documents.isNotEmpty()) {
                Timber.d("레스토랑 데이터 수신 완료 - 총 ${response.documents.size}개")
                val data = response.documents.map { it.toData() }
                Pair(data, response.meta.toData())
            } else {
                Timber.w("레스토랑 데이터가 없음 - 검색어: $query")
                throw IllegalStateException("검색 결과가 없습니다.")
            }

        } catch (e: Exception) {
            Timber.e(e, "레스토랑 데이터 요청 실패 - 검색어: $query, 페이지: $page")
            throw e
        }
    }
}