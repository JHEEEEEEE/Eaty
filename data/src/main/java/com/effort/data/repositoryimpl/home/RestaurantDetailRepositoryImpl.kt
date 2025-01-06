package com.effort.data.repositoryimpl.home

import com.effort.data.datasource.home.RestaurantDetailRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.home.blog.BlogReview
import com.effort.domain.model.home.blog.BlogReviewMeta
import com.effort.domain.repository.home.RestaurantDetailRepository
import javax.inject.Inject

class RestaurantDetailRepositoryImpl @Inject constructor(
    private val restaurantDetailRemoteDataSource: RestaurantDetailRemoteDataSource
): RestaurantDetailRepository {

    override suspend fun getBlogReviews(query: String, page: Int): DataResource<Pair<List<BlogReview>, BlogReviewMeta?>> {

        return try {
            val (remoteBlogReviews, meta) = restaurantDetailRemoteDataSource.getBlogReviews(query, page)

            DataResource.success(Pair(remoteBlogReviews.map { it.toDomain() }, meta?.toDomain()))
        } catch (e: Exception) {
            DataResource.error(e)
        }
    }
}