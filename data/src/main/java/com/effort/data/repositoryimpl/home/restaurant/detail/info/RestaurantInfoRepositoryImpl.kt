package com.effort.data.repositoryimpl.home.restaurant.detail.info

import com.effort.data.datasource.home.restaurant.detail.info.RestaurantInfoRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.home.blog.BlogReview
import com.effort.domain.model.home.blog.BlogReviewMeta
import com.effort.domain.repository.home.restaurant.detail.info.RestaurantInfoRepository
import javax.inject.Inject

class RestaurantInfoRepositoryImpl @Inject constructor(
    private val restaurantInfoRemoteDataSource: RestaurantInfoRemoteDataSource
) : RestaurantInfoRepository {

    override suspend fun getBlogReviews(
        query: String,
        region: String,
        page: Int
    ): DataResource<Pair<List<BlogReview>, BlogReviewMeta?>> {
        DataResource.loading<Pair<List<BlogReview>, BlogReviewMeta?>>()

        return try {
            val (remoteBlogReviews, meta) = restaurantInfoRemoteDataSource.getBlogReviews(
                query,
                region,
                page
            )

            DataResource.success(Pair(remoteBlogReviews.map { it.toDomain() }, meta?.toDomain()))
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }
}