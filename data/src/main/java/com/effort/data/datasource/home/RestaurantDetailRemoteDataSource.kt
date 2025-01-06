package com.effort.data.datasource.home

import com.effort.data.model.home.blog.BlogReviewEntity
import com.effort.data.model.home.blog.BlogReviewMetaEntity

interface RestaurantDetailRemoteDataSource {

    suspend fun getBlogReviews(query: String, page: Int): Pair<List<BlogReviewEntity>, BlogReviewMetaEntity?>
}