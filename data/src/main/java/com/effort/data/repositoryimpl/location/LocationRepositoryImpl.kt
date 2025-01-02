package com.effort.data.repositoryimpl.location

import android.util.Log
import com.effort.data.datasource.location.LocationRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.location.Location
import com.effort.domain.repository.location.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationRemoteDataSource: LocationRemoteDataSource
) : LocationRepository {

    override suspend fun getCurrentLocation(): DataResource<Location> {
        Log.d("LocationRepositoryImpl", "현재 위치 요청 시작")

        return try {
            // 1. 위치 정보 요청
            val location = locationRemoteDataSource.getCurrentLocation()
            Log.d("LocationRepositoryImpl", "위치 정보 요청 성공: $location")

            // **현재 위치 위경도 로그 출력**
            Log.d(
                "LocationLog",
                "현재 위치 - 위도: ${location.latitude}, 경도: ${location.longitude}"
            )

            // 2. 성공 시 데이터 변환 및 반환
            DataResource.success(location.toDomain())
        } catch (e: Exception) {
            // 3. 실패 시 예외 처리 및 반환
            Log.e("LocationRepositoryImpl", "위치 정보 요청 실패: ${e.message}", e)
            DataResource.error(e)
        }
    }
}
