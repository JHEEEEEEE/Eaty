package com.effort.domain.model.home.restaurant.detail.parkinglot

data class ParkingLot(
    val id: String, // 주차장 관리 ID
    val name: String, // 주차장 이름
    val address: String, // 주차장 주소
    val latitude: String, // 위도
    val longitude: String, // 경도
    val capacity: Int // 총 주차 구획 수
)
