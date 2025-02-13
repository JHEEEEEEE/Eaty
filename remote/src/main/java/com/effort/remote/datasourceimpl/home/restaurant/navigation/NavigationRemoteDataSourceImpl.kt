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

            Log.d("NavigationRemoteDataSourceImpl", "Response: $response") // 🚀 API 응답 확인

            // `toData()`를 사용하여 `List<NavigationPathEntity>`로 변환!
            response.route.trafast.firstOrNull()?.toData() ?: emptyList()
        } catch (e: Exception) {
            Log.e("NavigationRemoteDataSourceImpl", "API Request Failed: ${e.message}", e)
            emptyList() // 에러 발생 시 빈 리스트 반환 (오류는 Repository에서 처리)
        }
    }
}