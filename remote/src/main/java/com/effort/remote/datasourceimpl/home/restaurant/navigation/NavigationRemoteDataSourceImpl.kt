package com.effort.remote.datasourceimpl.home.restaurant.navigation

import timber.log.Timber
import com.effort.data.datasource.home.restaurant.navigation.NavigationRemoteDataSource
import com.effort.data.model.home.restaurant.navigation.NavigationPathEntity
import com.effort.remote.BuildConfig
import com.effort.remote.service.home.restaurant.navigation.NavigationService
import javax.inject.Inject

class NavigationRemoteDataSourceImpl @Inject constructor(
    private val navigationService: NavigationService
) : NavigationRemoteDataSource {

    /**
     * 네이버 API를 이용하여 시작점과 도착점 간의 경로를 조회한다.
     * - API 요청 시 네이버 클라이언트 ID 및 시크릿을 포함하여 요청
     * - 최적 경로(trafast) 중 첫 번째 데이터를 반환 (없을 경우 빈 리스트 반환)
     * - 예외 발생 시 Timber.e()로 오류 로깅 후 빈 리스트 반환 (Repository에서 오류 처리)
     */
    override suspend fun getNavigationPath(
        start: NavigationPathEntity, end: NavigationPathEntity
    ): List<NavigationPathEntity> {
        return try {
            Timber.d(
                "getNavigationPath() 호출 - 출발지: (${start.latitude}, ${start.longitude}), " + "도착지: (${end.latitude}, ${end.longitude})"
            )

            val response = navigationService.getNavigationPath(
                clientId = BuildConfig.NAVER_CLIENT_ID,
                clientSecret = BuildConfig.NAVER_CLIENT_SECRET,
                start = "${start.longitude},${start.latitude}",
                goal = "${end.longitude},${end.latitude}"
            )

            val route = response.route.trafast.firstOrNull()?.toData()
            if (route != null) {
                Timber.d("네이버 길찾기 API 응답 성공 - 경로 데이터 존재")
            } else {
                Timber.w("네이버 길찾기 API 응답 성공 - 하지만 경로 데이터 없음")
            }

            route ?: emptyList()
        } catch (e: Exception) {
            Timber.e(
                e,
                "네이버 길찾기 API 요청 실패 - 출발지: (${start.latitude}, ${start.longitude}), 도착지: (${end.latitude}, ${end.longitude})"
            )
            emptyList()
        }
    }
}