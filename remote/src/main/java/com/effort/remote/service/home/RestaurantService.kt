package com.effort.remote.service.home

import com.effort.remote.model.home.RestaurantWrapperResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantService {
    @GET("/v1/search/local.json")
    suspend fun getRestaurantList(
        @Query("query") query: String,
        @Query("display") display: Int = 5,
        @Query("start") start: Int = 1,
    ): Response<RestaurantWrapperResponse>
}