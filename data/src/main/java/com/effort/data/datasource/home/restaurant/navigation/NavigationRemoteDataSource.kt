package com.effort.data.datasource.home.restaurant.navigation

import com.effort.data.model.home.restaurant.navigation.NavigationPathEntity

interface NavigationRemoteDataSource {

    suspend fun getNavigationPath(start: NavigationPathEntity, end: NavigationPathEntity): List<NavigationPathEntity>
}