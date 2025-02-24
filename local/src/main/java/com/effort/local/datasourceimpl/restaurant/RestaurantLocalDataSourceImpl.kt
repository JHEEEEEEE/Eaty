package com.effort.local.datasourceimpl.restaurant

import com.effort.data.datasource.home.restaurant.RestaurantLocalDataSource
import com.effort.data.model.home.restaurant.RestaurantEntity
import com.effort.local.dao.restaurant.RestaurantDao
import com.effort.local.model.restaurant.toData
import com.effort.local.model.restaurant.toLocal
import timber.log.Timber
import javax.inject.Inject

class RestaurantLocalDataSourceImpl @Inject constructor(
    private val restaurantDao: RestaurantDao
) : RestaurantLocalDataSource {

    /**
     * 로컬 데이터베이스에서 식당 리스트를 조회한다.
     * - 검색어(query)를 기반으로 데이터를 조회
     * - 조회 성공 시 데이터 개수를 로그로 출력
     * - 예외 발생 시 오류 로그를 출력하고 빈 리스트 반환
     */
    override suspend fun getRestaurantList(query: String): List<RestaurantEntity> {
        return try {
            Timber.d("getRestaurantList() 호출 - 검색어: $query")
            val localData = restaurantDao.getRestaurants(query).toData()
            Timber.d("getRestaurantList() 성공 - 조회된 데이터 개수: ${localData.size}")
            localData
        } catch (e: Exception) {
            Timber.e(e, "getRestaurantList() 실패 - 데이터 조회 중 오류 발생")
            emptyList()
        }
    }

    /**
     * 로컬 데이터베이스에 식당 리스트를 저장한다.
     * - 저장할 데이터 개수를 로그로 출력
     * - 예외 발생 시 오류 로그 출력
     */
    override suspend fun insertRestaurantList(restaurants: List<RestaurantEntity>) {
        try {
            Timber.d("insertRestaurantList() 호출 - 저장할 데이터 개수: ${restaurants.size}")
            restaurantDao.insertRestaurants(restaurants.toLocal()) // Data → Local 변환 후 저장
            Timber.d("insertRestaurantList() 완료 - 데이터 저장 성공")
        } catch (e: Exception) {
            Timber.e(e, "insertRestaurantList() 실패 - 데이터 저장 중 오류 발생")
        }
    }
}