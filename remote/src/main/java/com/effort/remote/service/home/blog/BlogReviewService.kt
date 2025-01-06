package com.effort.remote.service.home.blog

import com.effort.remote.model.home.blog.BlogReviewWrapperResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BlogReviewService {
    @GET("v2/search/blog")
    suspend fun getBlogReviewList(
        @Query("query") query: String,                 // 검색어
        @Query("page") page: Int = 1,                   // 페이지 번호
        @Query("sort") sort: String = "accuracy",      // 정렬 기준 (정확도순)
        @Query("size") size: Int = 10                  // 페이지 크기
    ): BlogReviewWrapperResponse
}