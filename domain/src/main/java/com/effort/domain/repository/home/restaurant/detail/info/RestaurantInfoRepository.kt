package com.effort.domain.repository.home.restaurant.detail.info

import com.effort.domain.DataResource
import com.effort.domain.model.home.blog.BlogReview
import com.effort.domain.model.home.blog.BlogReviewMeta

interface RestaurantInfoRepository {

    suspend fun getBlogReviews(
        query: String,
        region: String,
        page: Int
    ): DataResource<Pair<List<BlogReview>, BlogReviewMeta?>>
}