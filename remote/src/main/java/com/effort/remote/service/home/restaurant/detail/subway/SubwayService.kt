package com.effort.remote.service.home.restaurant.detail.subway

import com.effort.remote.model.home.restaurant.detail.subway.SubwayWrapperRespose
import retrofit2.http.GET
import retrofit2.http.Query

interface SubwayService {
    @GET("v2/local/search/keyword.json")
    suspend fun getSubwayStation(
        @Query("query") query: String = "지하철",
        @Query("x") longitude: String,  // 위경도는 기본값 없이 필수 전달
        @Query("y") latitude: String,
        @Query("category_group_code") category: String = "SW8",
        @Query("size") size: Int = 5,
        @Query("sort") sort: String = "distance"
    ): SubwayWrapperRespose
}