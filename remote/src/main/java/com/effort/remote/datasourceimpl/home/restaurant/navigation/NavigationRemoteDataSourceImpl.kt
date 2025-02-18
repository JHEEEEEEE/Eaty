package com.effort.remote.datasourceimpl.home.restaurant.navigation

import android.util.Log
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
     * - 예외 발생 시 로그를 출력하고 빈 리스트 반환 (Repository에서 오류 처리)
     */
    override suspend fun getNavigationPath(
        start: NavigationPathEntity,
        end: NavigationPathEntity
    ): List<NavigationPathEntity> {
        Log.d(
            "NavigationRemoteDataSourceImpl",
            "${BuildConfig.NAVER_CLIENT_ID},${BuildConfig.NAVER_CLIENT_SECRET}, ${start.longitude},${start.latitude}, ${end.longitude},${end.latitude}"
        )

        return try {
            val response = navigationService.getNavigationPath(
                clientId = BuildConfig.NAVER_CLIENT_ID,
                clientSecret = BuildConfig.NAVER_CLIENT_SECRET,
                start = "${start.longitude},${start.latitude}",
                goal = "${end.longitude},${end.latitude}"
            )

            Log.d("NavigationRemoteDataSourceImpl", "Response: $response") // API 응답 확인

            response.route.trafast.firstOrNull()?.toData() ?: emptyList()
        } catch (e: Exception) {
            Log.e("NavigationRemoteDataSourceImpl", "API Request Failed: ${e.message}", e)
            emptyList()
        }
    }
}