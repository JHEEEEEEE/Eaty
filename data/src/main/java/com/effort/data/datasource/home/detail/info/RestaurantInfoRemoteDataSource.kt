package com.effort.data.datasource.home.detail.info

import com.effort.data.model.home.blog.BlogReviewEntity
import com.effort.data.model.home.blog.BlogReviewMetaEntity

interface RestaurantInfoRemoteDataSource {

    suspend fun getBlogReviews(
        query: String,
        region: String,
        page: Int
    ): Pair<List<BlogReviewEntity>, BlogReviewMetaEntity?>
}