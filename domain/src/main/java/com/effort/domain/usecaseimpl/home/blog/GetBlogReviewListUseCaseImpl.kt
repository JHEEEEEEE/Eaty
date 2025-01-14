package com.effort.domain.usecaseimpl.home.blog

import com.effort.domain.DataResource
import com.effort.domain.model.home.blog.BlogReview
import com.effort.domain.model.home.blog.BlogReviewMeta
import com.effort.domain.repository.home.detail.info.RestaurantInfoRepository
import com.effort.domain.usecase.home.restaurant.detail.blog.GetBlogReviewListUseCase
import javax.inject.Inject

class GetBlogReviewListUseCaseImpl @Inject constructor(
    private val restaurantInfoRepository: RestaurantInfoRepository
) : GetBlogReviewListUseCase {

    override suspend fun invoke(
        query: String,
        region: String,
        page: Int
    ): DataResource<Pair<List<BlogReview>, BlogReviewMeta?>> {
        return restaurantInfoRepository.getBlogReviews(query, region, page)
    }
}