package com.effort.local.model.restaurant.parkinglot

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.effort.data.model.home.restaurant.detail.parkinglot.ParkingLotEntity

@Entity(tableName = "parkingLots")
data class ParkingLotLocal(
    @PrimaryKey
    val id: String, // 주차장 관리 ID
    val name: String, // 주차장 이름
    val address: String, // 주차장 주소
    val latitude: String, // 위도
    val longitude: String, // 경도
    val capacity: Int // 총 주차 구획 수
) {
    fun toData(): ParkingLotEntity {
        return ParkingLotEntity(
            id, name, address, latitude, longitude, capacity
        )
    }
}

// Data -> Local 변환 함수 (단일 객체 변환 지원)
fun ParkingLotEntity.toLocal(): ParkingLotLocal {
    return ParkingLotLocal(
        id, name, address, latitude, longitude, capacity
    )
}

// 리스트 변환 (Local -> Data)
fun List<ParkingLotLocal>.toData(): List<ParkingLotEntity> {
    return this.map { it.toData() } // 단일 변환 함수 활용
}

// 리스트 변환 (Data -> Local)
fun List<ParkingLotEntity>.toLocal(): List<ParkingLotLocal> {
    return this.map { it.toLocal() } // 단일 변환 함수 활용
}
