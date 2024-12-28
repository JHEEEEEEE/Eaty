package com.effort.local.datasourceimpl

import android.util.Log
import com.effort.data.datasource.home.RestaurantLocalDataSource
import com.effort.data.model.home.RestaurantEntity
import com.effort.local.dao.RestaurantDao
import com.effort.local.model.toData
import com.effort.local.model.toLocal
import javax.inject.Inject

class RestaurantLocalDataSourceImpl @Inject constructor(
    private val restaurantDao: RestaurantDao
): RestaurantLocalDataSource {
    // Local DB에서 식당 리스트 조회
    override suspend fun getRestaurantList(query: String): List<RestaurantEntity> {
        return try {
            val localData = restaurantDao.getRestaurants(query)
            localData.toData() // Local → Data 변환
        } catch (e: Exception) {
            Log.e("RestaurantLocalDataSourceImpl", "Failed to fetch data: ${e.message}")
            emptyList() // 오류 발생 시 빈 리스트 반환
        }
    }

    // Local DB에 식당 리스트 저장
    override suspend fun insertRestaurantList(restaurants: List<RestaurantEntity>) {
        try {
            restaurantDao.insertRestaurants(restaurants.toLocal()) // Data → Local 변환 후 저장
        } catch (e: Exception) {
            Log.e("RestaurantLocalDataSourceImpl", "Failed to insert data: ${e.message}")
        }
    }
}