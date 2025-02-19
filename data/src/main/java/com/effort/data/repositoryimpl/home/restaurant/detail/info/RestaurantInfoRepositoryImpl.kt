package com.effort.data.repositoryimpl.home.restaurant.detail.info

import com.effort.data.datasource.home.restaurant.detail.info.RestaurantInfoRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.blog.BlogReview
import com.effort.domain.model.home.restaurant.detail.blog.BlogReviewMeta
import com.effort.domain.repository.home.restaurant.detail.info.RestaurantInfoRepository
import timber.log.Timber
import javax.inject.Inject

class RestaurantInfoRepositoryImpl @Inject constructor(
    private val restaurantInfoRemoteDataSource: RestaurantInfoRemoteDataSource
) : RestaurantInfoRepository {

    override suspend fun getBlogReviews(
        query: String, region: String, page: Int
    ): DataResource<Pair<List<BlogReview>, BlogReviewMeta?>> {
        Timber.d("getBlogReviews() 호출됨 - query: $query, region: $region, page: $page")

        DataResource.loading<Pair<List<BlogReview>, BlogReviewMeta?>>()

        return try {
            val (remoteBlogReviews, meta) = restaurantInfoRemoteDataSource.getBlogReviews(
                query, region, page
            )

            Timber.d("getBlogReviews() 성공 - 결과 개수: ${remoteBlogReviews.size}, 메타 정보: $meta")
            DataResource.success(Pair(remoteBlogReviews.map { it.toDomain() }, meta?.toDomain()))
        } catch (e: Exception) {
            Timber.e(e, "getBlogReviews() 실패 - query: $query, region: $region, page: $page")
            DataResource.error(e)
        }
    }
}