package com.effort.remote.datasourceimpl.home.restaurant.detail.info

import android.util.Log
import com.effort.data.datasource.home.restaurant.detail.info.RestaurantInfoRemoteDataSource
import com.effort.data.model.home.restaurant.detail.blog.BlogReviewEntity
import com.effort.data.model.home.restaurant.detail.blog.BlogReviewMetaEntity
import com.effort.remote.service.home.restaurant.detail.blog.BlogReviewService
import javax.inject.Inject

class RestaurantInfoRemoteDataSourceImpl @Inject constructor(
    private val blogReviewService: BlogReviewService,
) : RestaurantInfoRemoteDataSource {

    override suspend fun getBlogReviews(
        query: String,
        region: String,
        page: Int
    ): Pair<List<BlogReviewEntity>, BlogReviewMetaEntity?> {
        val searchQuery = "$region \"$query\""

        return try {
            val response = blogReviewService.getBlogReviewList(searchQuery, page)
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