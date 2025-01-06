package com.effort.domain.usecase.home.blog

import com.effort.domain.DataResource
import com.effort.domain.model.home.blog.BlogReview
import com.effort.domain.model.home.blog.BlogReviewMeta

interface GetBlogReviewListUseCase {

    suspend operator fun invoke(query: String, page: Int): DataResource<Pair<List<BlogReview>, BlogReviewMeta?>>
}