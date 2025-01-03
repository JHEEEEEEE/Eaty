package com.effort.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.effort.data.model.home.RestaurantEntity

@Entity(tableName = "restaurants")
data class RestaurantLocal(
    @PrimaryKey
    val title: String,
    val lotNumberAddress: String,
    val roadNameAddress: String,
    val phoneNumber: String,
    val placeUrl: String,
    val distance: String,
    val longitude: String,
    val latitude: String,
) {
    // Local -> Data 변환 함수 (단일 객체 변환 지원)
    fun toData(): RestaurantEntity {
        return RestaurantEntity(
            title,
            lotNumberAddress,
            roadNameAddress,
            phoneNumber,
            placeUrl,
            distance,
            longitude,
            latitude
        )
    }
}

// Data -> Local 변환 함수 (단일 객체 변환 지원)
fun RestaurantEntity.toLocal(): RestaurantLocal {
    return RestaurantLocal(
        title,
        lotNumberAddress,
        roadNameAddress,
        phoneNumber,
        placeUrl,
        distance,
        longitude,
        latitude
    )
}

// 리스트 변환 (Local -> Data)
fun List<RestaurantLocal>.toData(): List<RestaurantEntity> {
    return this.map { it.toData() } // 단일 변환 함수 활용
}

// 리스트 변환 (Data -> Local)
fun List<RestaurantEntity>.toLocal(): List<RestaurantLocal> {
    return this.map { it.toLocal() } // 단일 변환 함수 활용
}