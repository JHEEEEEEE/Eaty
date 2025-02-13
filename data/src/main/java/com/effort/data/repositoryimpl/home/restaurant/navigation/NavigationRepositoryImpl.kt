package com.effort.data.repositoryimpl.home.restaurant.navigation

import com.effort.data.datasource.home.restaurant.navigation.NavigationRemoteDataSource
import com.effort.data.model.home.restaurant.navigation.toData
import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.navigation.NavigationPath
import com.effort.domain.repository.home.restaurant.navigation.NavigationRepository
import javax.inject.Inject

class NavigationRepositoryImpl @Inject constructor(
    private val navigationRemoteDataSource: NavigationRemoteDataSource
) : NavigationRepository {

    override suspend fun getNavigationPath(
        start: NavigationPath,
        end: NavigationPath
    ): DataResource<List<NavigationPath>> {

        // 1. 로딩 상태 반환 (비동기 작업 시작)
        DataResource.loading<List<NavigationPath>>()

        return try {
            // 2. 도메인 데이터를 Data Layer의 모델로 변환
            val startData = start.toData()
            val endData = end.toData()

            // 3. RemoteDataSource를 통해 네이버 API에서 데이터 가져오기
            val remoteData = navigationRemoteDataSource.getNavigationPath(startData, endData)

            // 4. Data Layer의 모델을 다시 Domain Layer의 모델로 변환
            val domainData = remoteData.map { it.toDomain() }

            // 5. 성공적인 데이터 반환
            DataResource.Success(domainData)
        } catch (e: Exception) {
            // 6. 에러 발생 시 처리
            DataResource.error(e)
        }
    }
}