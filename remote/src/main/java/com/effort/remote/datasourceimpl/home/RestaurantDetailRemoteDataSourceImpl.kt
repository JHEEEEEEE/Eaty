package com.effort.remote.datasourceimpl.home

import android.util.Log
import com.effort.data.datasource.home.RestaurantDetailRemoteDataSource
import com.effort.data.model.home.blog.BlogReviewEntity
import com.effort.data.model.home.blog.BlogReviewMetaEntity
import com.effort.remote.service.home.blog.BlogReviewService
import javax.inject.Inject

class RestaurantDetailRemoteDataSourceImpl @Inject constructor(
    private val blogReviewService: BlogReviewService
) : RestaurantDetailRemoteDataSource {

    override suspend fun getBlogReviews(
        query: String,
        page: Int
    ): Pair<List<BlogReviewEntity>, BlogReviewMetaEntity?> {
        return try {
            val response = blogReviewService.getBlogReviewList(query, page)
            val data = response.documents.map { it.toData() }
            val meta = response.meta.toData()

            Pair(data, meta)
        } catch (e: Exception) {
            // 4. 예외 처리
            Log.e("RestaurantDetailRemote", "API 요청 실패: ${e.message}", e)
            throw e // 예외 전파
        }
    }
}