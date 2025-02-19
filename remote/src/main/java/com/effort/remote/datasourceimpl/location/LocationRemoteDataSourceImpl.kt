package com.effort.remote.datasourceimpl.location

import com.effort.data.datasource.location.LocationRemoteDataSource
import com.effort.data.model.location.LocationEntity
import com.effort.remote.service.location.LocationService
import timber.log.Timber
import javax.inject.Inject

class LocationRemoteDataSourceImpl @Inject constructor(
    private val locationService: LocationService
) : LocationRemoteDataSource {

    override suspend fun getCurrentLocation(): LocationEntity {
        Timber.d("getCurrentLocation() 호출됨")

        return try {
            // 1. 서비스로부터 위치 정보 가져오기
            val locationResponse = locationService.getCurrentLocation()

            // 2. null 체크 및 로그 처리
            if (locationResponse != null) {
                Timber.d("현재 위치 수신 성공: $locationResponse")
                locationResponse.toData()
            } else {
                Timber.e("현재 위치 수신 실패")
                throw Exception("현재 위치 정보를 가져오지 못했습니다.")
            }
        } catch (e: Exception) {
            Timber.e(e, "getCurrentLocation() 예외 발생")
            throw e
        }
    }
}