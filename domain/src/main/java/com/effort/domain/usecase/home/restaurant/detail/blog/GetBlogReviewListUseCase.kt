package com.effort.domain.usecase.home.restaurant.detail.blog

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.blog.BlogReview
import com.effort.domain.model.home.restaurant.detail.blog.BlogReviewMeta

interface GetBlogReviewListUseCase {

    suspend operator fun invoke(
        query: String,
        region: String,
        page: Int
    ): DataResource<Pair<List<BlogReview>, BlogReviewMeta?>>
}