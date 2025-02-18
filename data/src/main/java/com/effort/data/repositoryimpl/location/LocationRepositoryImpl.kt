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

    /**
     * 현재 위치 정보를 가져온다.
     * - 위치 정보 요청 성공 시 위경도를 로그로 출력
     * - 실패 시 예외를 처리하고 오류 상태 반환
     */
    override suspend fun getCurrentLocation(): DataResource<Location> {
        Log.d("LocationRepositoryImpl", "현재 위치 요청 시작")

        return try {
            // 위치 정보 요청
            val location = locationRemoteDataSource.getCurrentLocation()
            Log.d("LocationRepositoryImpl", "위치 정보 요청 성공: $location")

            DataResource.success(location.toDomain())
        } catch (e: Exception) {
            Log.e("LocationRepositoryImpl", "위치 정보 요청 실패: ${e.message}", e)
            DataResource.error(e)
        }
    }
}
