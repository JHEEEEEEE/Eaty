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

        DataResource.loading<List<NavigationPath>>()

        return try {
            val startData = start.toData()
            val endData = end.toData()

            val remoteData = navigationRemoteDataSource.getNavigationPath(startData, endData)

            val domainData = remoteData.map { it.toDomain() }

            DataResource.Success(domainData)
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }
}