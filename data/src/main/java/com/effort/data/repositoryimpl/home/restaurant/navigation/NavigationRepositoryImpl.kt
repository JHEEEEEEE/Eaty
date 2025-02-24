package com.effort.data.repositoryimpl.home.restaurant.navigation

import com.effort.data.datasource.home.restaurant.navigation.NavigationRemoteDataSource
import com.effort.data.model.home.restaurant.navigation.toData
import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.navigation.NavigationPath
import com.effort.domain.repository.home.restaurant.navigation.NavigationRepository
import timber.log.Timber
import javax.inject.Inject

class NavigationRepositoryImpl @Inject constructor(
    private val navigationRemoteDataSource: NavigationRemoteDataSource
) : NavigationRepository {

    override suspend fun getNavigationPath(
        start: NavigationPath, end: NavigationPath
    ): DataResource<List<NavigationPath>> {

        Timber.d("getNavigationPath() 호출됨 - 시작점: $start, 도착점: $end")

        DataResource.loading<List<NavigationPath>>()

        return try {
            val startData = start.toData()
            val endData = end.toData()

            Timber.d("getNavigationPath() 변환 완료 - startData: $startData, endData: $endData")

            val remoteData = navigationRemoteDataSource.getNavigationPath(startData, endData)

            val domainData = remoteData.map { it.toDomain() }

            Timber.d("getNavigationPath() 성공 - 반환된 경로 개수: ${domainData.size}")

            DataResource.Success(domainData)
        } catch (e: Exception) {
            Timber.e(e, "getNavigationPath() 실패 - 시작점: $start, 도착점: $end")
            DataResource.error(e)
        }
    }
}