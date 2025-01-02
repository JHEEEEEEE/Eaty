package com.effort.remote.datasourceimpl.location

import android.location.Location
import android.util.Log
import com.effort.data.datasource.location.LocationRemoteDataSource
import com.effort.data.model.location.LocationEntity
import com.effort.remote.service.location.LocationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class LocationRemoteDataSourceImpl @Inject constructor(
    private val locationService: LocationService
) : LocationRemoteDataSource {

    override suspend fun getCurrentLocation(): LocationEntity {
        Log.d("LocationRemoteDataSourceImpl", "getCurrentLocation() 호출됨")

        // 1. 서비스로부터 위치 정보 가져오기
        val locationResponse = locationService.getCurrentLocation()

        // 2. null 체크 및 로그 처리
        if (locationResponse != null) {
            Log.d("LocationRemoteDataSourceImpl", "현재 위치 수신 성공: $locationResponse")
        } else {
            Log.e("LocationRemoteDataSourceImpl", "현재 위치 수신 실패")
            throw Exception("현재 위치 정보를 가져오지 못했습니다.")
        }

        // 3. LocationResponse → LocationEntity 변환 후 반환
        return locationResponse.toData()
    }

    /*override fun getLocationUpdates(): Flow<Location> {
        Log.d("LocationRemoteDataSourceImpl", "getLocationUpdates() 호출됨")
        return locationService.getLocationUpdates().onEach { location ->
            Log.d("LocationRemoteDataSourceImpl", "실시간 위치 업데이트 수신: $location")
        }.catch { e ->
            Log.e("LocationRemoteDataSourceImpl", "위치 업데이트 오류: ${e.message}")
        }
    }*/
}