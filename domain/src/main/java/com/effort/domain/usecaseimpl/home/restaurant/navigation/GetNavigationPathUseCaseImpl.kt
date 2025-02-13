package com.effort.domain.usecaseimpl.home.restaurant.navigation

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.navigation.NavigationPath
import com.effort.domain.repository.home.restaurant.navigation.NavigationRepository
import com.effort.domain.usecase.home.restaurant.navigation.GetNavigationPathUseCase
import javax.inject.Inject

class GetNavigationPathUseCaseImpl @Inject constructor(
    private val navigationRepository: NavigationRepository
): GetNavigationPathUseCase {

    override suspend fun invoke(start: NavigationPath, end: NavigationPath): DataResource<List<NavigationPath>> {
        return navigationRepository.getNavigationPath(start, end)
    }
}