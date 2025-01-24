package com.effort.data.datasource.home.restaurant.detail.info

import com.effort.data.model.home.restaurant.detail.blog.BlogReviewEntity
import com.effort.data.model.home.restaurant.detail.blog.BlogReviewMetaEntity

interface RestaurantInfoRemoteDataSource {

    suspend fun getBlogReviews(
        query: String,
        region: String,
        page: Int
    ): Pair<List<BlogReviewEntity>, BlogReviewMetaEntity?>
}