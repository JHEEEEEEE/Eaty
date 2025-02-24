package com.effort.domain.usecaseimpl.home.restaurant.detail.blog

import com.effort.domain.DataResource
import com.effort.domain.model.home.restaurant.detail.blog.BlogReview
import com.effort.domain.model.home.restaurant.detail.blog.BlogReviewMeta
import com.effort.domain.repository.home.restaurant.detail.info.RestaurantInfoRepository
import com.effort.domain.usecase.home.restaurant.detail.blog.GetBlogReviewListUseCase
import javax.inject.Inject

class GetBlogReviewListUseCaseImpl @Inject constructor(
    private val restaurantInfoRepository: RestaurantInfoRepository
) : GetBlogReviewListUseCase {

    /**
     * 블로그 리뷰 데이터를 가져오고, HTML 태그를 제거하여 반환한다.
     * - HTML 태그 제거를 통해 UI에서 깔끔한 텍스트를 표시
     * - HTML 엔티티(&lt;, &gt; 등)도 디코딩 처리
     */
    override suspend fun invoke(
        query: String,
        region: String,
        page: Int
    ): DataResource<Pair<List<BlogReview>, BlogReviewMeta?>> {
        val result = restaurantInfoRepository.getBlogReviews(query, region, page)

        // 데이터가 성공적으로 반환된 경우, HTML 태그를 제거하여 정제
        if (result is DataResource.Success) {
            val (reviews, meta) = result.data

            val processedReviews = reviews.map { review ->
                review.copy(
                    title = stripHtmlTags(review.title),  // 제목 HTML 태그 제거
                    contents = stripHtmlTags(review.contents) // 내용 HTML 태그 제거
                )
            }

            return DataResource.success(Pair(processedReviews, meta))
        }

        return result
    }

    /**
     * HTML 태그 제거한다.
     * - HTML 엔티티를 먼저 디코딩한 후, 태그 제거
     */
    private fun stripHtmlTags(input: String): String {
        val decodedString = decodeHtmlEntities(input) // HTML 엔티티 디코딩
        return decodedString.replace(Regex("<[^>]*>"), "") // HTML 태그 제거
    }

    /**
     * HTML 엔티티 디코딩한다.
     * - HTML 특수 문자(&lt;, &amp; 등)를 실제 기호로 변환
     * - 블로그 데이터에서 HTML 인코딩된 텍스트를 정제하는 목적
     */
    private fun decodeHtmlEntities(input: String): String {
        val htmlEntities = mapOf(
            "&lt;" to "<","&gt;" to ">","&amp;" to "&","&quot;" to "\"","&#39;" to "'",
            "&#34;" to "\"","&#38;" to "&","&#60;" to "<","&#62;" to ">",
            "&nbsp;" to " ","&copy;" to "©","&reg;" to "®","&trade;" to "™",
            "&euro;" to "€","&yen;" to "¥","&pound;" to "£","&cent;" to "¢",
            "&hellip;" to "…","&ndash;" to "–","&mdash;" to "—","&apos;" to "'",
            "&sect;" to "§","&para;" to "¶","&laquo;" to "«","&raquo;" to "»"
        )

        var decodedString = input
        for ((entity, char) in htmlEntities) {
            decodedString = decodedString.replace(entity, char)
        }

        return decodedString
    }
}