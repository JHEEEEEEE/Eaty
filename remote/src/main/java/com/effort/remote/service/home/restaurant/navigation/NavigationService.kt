package com.effort.remote.service.home.restaurant.navigation

import com.effort.remote.model.home.restaurant.navigation.NavigationPathResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NavigationService {
    @GET("map-direction/v1/driving")
    suspend fun getNavigationPath(
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String,
        @Query("start") start: String,
        @Query("goal") goal: String,
        @Query("option") option: String = "trafast" // 가장 빠른 경로 옵션
    ): NavigationPathResponse
}