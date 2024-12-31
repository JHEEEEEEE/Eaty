package com.effort.remote.datasourceimpl.location

import android.location.Location
import android.util.Log
import com.effort.data.datasource.location.LocationRemoteDataSource
import com.effort.remote.service.location.LocationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class LocationRemoteDataSourceImpl @Inject constructor(
    private val locationService: LocationService
) : LocationRemoteDataSource {

    override suspend fun getCurrentLocation(): Location? {
        Log.d("LocationRemoteDataSourceImpl", "getCurrentLocation() 호출됨")
        val location = locationService.getCurrentLocation()
        if (location != null) {
            Log.d("LocationRemoteDataSourceImpl", "현재 위치 수신 성공: $location")
        } else {
            Log.e("LocationRemoteDataSourceImpl", "현재 위치 수신 실패")
        }
        return location
    }

    override fun getLocationUpdates(): Flow<Location> {
        Log.d("LocationRemoteDataSourceImpl", "getLocationUpdates() 호출됨")
        return locationService.getLocationUpdates().onEach { location ->
            Log.d("LocationRemoteDataSourceImpl", "실시간 위치 업데이트 수신: $location")
        }.catch { e ->
            Log.e("LocationRemoteDataSourceImpl", "위치 업데이트 오류: ${e.message}")
        }
    }
}