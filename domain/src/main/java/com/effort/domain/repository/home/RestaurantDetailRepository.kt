package com.effort.domain.repository.home

import com.effort.domain.DataResource
import com.effort.domain.model.home.blog.BlogReview
import com.effort.domain.model.home.blog.BlogReviewMeta

interface RestaurantDetailRepository {

    suspend fun getBlogReviews(query: String, page: Int): DataResource<Pair<List<BlogReview>, BlogReviewMeta?>>
}