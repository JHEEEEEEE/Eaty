package com.effort.domain.usecase.home.restaurant.navigation

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.navigation.NavigationPath

interface GetNavigationPathUseCase {

    suspend operator fun invoke(start: NavigationPath, end: NavigationPath): DataResource<List<NavigationPath>>
}