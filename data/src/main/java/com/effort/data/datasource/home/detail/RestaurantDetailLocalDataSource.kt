package com.effort.data.datasource.home.detail

import com.effort.data.model.home.parkinglot.ParkingLotEntity

interface RestaurantDetailLocalDataSource {

    suspend fun isDatabaseEmpty(): Boolean

    suspend fun initializeParkingLotsFromJson()

    // 가까운 주차장 5개 조회
    suspend fun getNearestParkingLots(latitude: String, longitude: String): List<ParkingLotEntity>
}