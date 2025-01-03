package com.effort.remote.service.home


import com.effort.remote.model.home.RestaurantWrapperResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantService {
    @GET("v2/local/search/keyword.json")
    suspend fun getRestaurantList(
        @Query("query") query: String,
        @Query("category_group_code") category: String = "FD6",
        @Query("x") longitude: String? = null,
        @Query("y") latitude: String? = null,
        @Query("radius") radius: Int? = 5000,
        @Query("size") size: Int = 15, // 기본값 15로 설정
        @Query("sort") sort: String = "distance"
    ): RestaurantWrapperResponse
}