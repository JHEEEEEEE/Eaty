package com.effort.remote.datasourceimpl.home.restaurant.detail.info

import com.effort.data.datasource.home.restaurant.detail.info.RestaurantInfoRemoteDataSource
import com.effort.data.model.home.restaurant.detail.blog.BlogReviewEntity
import com.effort.data.model.home.restaurant.detail.blog.BlogReviewMetaEntity
import com.effort.remote.service.home.restaurant.detail.blog.BlogReviewService
import timber.log.Timber
import javax.inject.Inject

class RestaurantInfoRemoteDataSourceImpl @Inject constructor(
    private val blogReviewService: BlogReviewService,
) : RestaurantInfoRemoteDataSource {

    /**
     * 블로그 리뷰 데이터를 외부 API에서 가져온다.
     * - 검색어(query)와 지역(region)을 조합하여 API 요청
     * - 성공 시 리뷰 리스트와 메타데이터 반환
     * - 실패 시 예외 처리 및 로그 출력
     */
    override suspend fun getBlogReviews(
        query: String, region: String, page: Int
    ): Pair<List<BlogReviewEntity>, BlogReviewMetaEntity?> {
        val searchQuery = "$region \"$query\""

        return try {
            Timber.d("getBlogReviews() 호출 - query: $query, region: $region, page: $page")
            val response = blogReviewService.getBlogReviewList(searchQuery, page)

            Timber.d("API 응답 성공 - 리뷰 ${response.documents.size}개, 메타데이터: ${response.meta}")
            Pair(response.documents.map { it.toData() }, response.meta.toData())
        } catch (e: Exception) {
            Timber.e(e, "블로그 리뷰 API 요청 실패 - query: $query, region: $region, page: $page")
            throw e
        }
    }
}