package com.effort.domain.repository.home.restaurant.navigation

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.navigation.NavigationPath

interface NavigationRepository {

    suspend fun getNavigationPath(
        start: NavigationPath,
        end: NavigationPath
    ): DataResource<List<NavigationPath>>
}