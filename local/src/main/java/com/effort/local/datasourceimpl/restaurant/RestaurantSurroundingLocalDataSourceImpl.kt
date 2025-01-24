package com.effort.local.datasourceimpl.restaurant

import android.content.Context
import android.util.Log
import com.effort.data.datasource.home.restaurant.detail.surrounding.RestaurantSurroundingLocalDataSource
import com.effort.data.model.home.restaurant.detail.parkinglot.ParkingLotEntity
import com.effort.local.dao.restaurant.parkinglot.ParkingLotDao
import com.effort.local.model.restaurant.parkinglot.ParkingLotWrapperLocal
import com.effort.local.model.restaurant.parkinglot.toData
import kotlinx.serialization.json.Json
import java.io.InputStreamReader
import javax.inject.Inject

class RestaurantSurroundingLocalDataSourceImpl @Inject constructor(
    private val parkingLotDao: ParkingLotDao,
    private val context: Context // context는 모듈에서 주입
) : RestaurantSurroundingLocalDataSource {

    override suspend fun isDatabaseEmpty(): Boolean {
        return parkingLotDao.countAll() == 0
    }

    override suspend fun initializeParkingLotsFromJson() {
        try {
            Log.d("LocalDataSource", "JSON 데이터 Room 초기화 시작...")

            // JSON 파일 읽기
            val inputStream = context.assets.open("seoul_parking_data.json")
            val reader = InputStreamReader(inputStream)

            // JSON 파싱
            val json = reader.use { it.readText() }
            val wrapper = Json.decodeFromString<ParkingLotWrapperLocal>(json)

            //Room에 데이터 저장
            parkingLotDao.insertAll(wrapper.parkingLots)

            Log.d("LocalDataSource", "JSON 데이터 Room 초기화 완료.")
        } catch (e: Exception) {
            Log.e("LocalDataSource", "초기화 실패: ${e.message}", e)
        }
    }

    override suspend fun getNearestParkingLots(
        latitude: String,
        longitude: String
    ): List<ParkingLotEntity> {
        return try {
            val lat = latitude.toDouble()
            val lon = longitude.toDouble()

            val result = parkingLotDao.getNearestParkingLots(lat, lon)
            result.toData()
        } catch (e: Exception) {
            Log.e("LocalDataSource", "조회 실패: ${e.message}", e)
            emptyList()
        }
    }
}