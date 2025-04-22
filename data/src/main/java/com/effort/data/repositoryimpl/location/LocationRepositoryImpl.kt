package com.effort.data.repositoryimpl.location

import com.effort.data.datasource.location.LocationRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.location.Location
import com.effort.domain.repository.location.LocationRepository
import timber.log.Timber
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
        Timber.d("getCurrentLocation() 호출됨")

        return try {
            // 위치 정보 요청
            val location = locationRemoteDataSource.getCurrentLocation()
            Timber.d("위치 정보 요청 성공 - 위도: ${location.latitude}, 경도: ${location.longitude}")

            DataResource.success(location.toDomain())
        } catch (e: Exception) {
            Timber.e(e, "위치 정보 요청 실패")
            DataResource.error(e)
        }
    }
}