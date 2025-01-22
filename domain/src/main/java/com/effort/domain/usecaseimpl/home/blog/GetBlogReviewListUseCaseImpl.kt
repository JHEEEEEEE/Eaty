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
        // 1. repository를 통해 데이터를 가져오기
        val result = restaurantInfoRepository.getBlogReviews(query, region, page)

        // 2. 성공적인 데이터의 경우, 태그를 제거
        if (result is DataResource.Success) {
            val (reviews, meta) = result.data

            // 3. 태그 제거 후 새로운 데이터로 변환
            val processedReviews = reviews.map { review ->
                review.copy(
                    title = stripHtmlTags(review.title),  // 제목의 HTML 태그 제거
                    contents = stripHtmlTags(review.contents) // 내용의 HTML 태그 제거
                )
            }

            return DataResource.success(Pair(processedReviews, meta))
        }

        // 5. 오류나 기타 상태는 그대로 반환
        return result
    }

    private fun stripHtmlTags(input: String): String {
        // 1. HTML 엔티티 디코딩
        val decodedString = decodeHtmlEntities(input)

        // 2. HTML 태그 제거
        return decodedString.replace(Regex("<[^>]*>"), "") // 정규식으로 태그 제거
    }

    // HTML 엔티티 디코딩 함수
    private fun decodeHtmlEntities(input: String): String {
        val htmlEntities = mapOf(
            // 기본 HTML 엔티티
            "&lt;" to "<",
            "&gt;" to ">",
            "&amp;" to "&",
            "&quot;" to "\"",
            "&#39;" to "'",

            // 숫자형 HTML 엔티티
            "&#34;" to "\"", // 큰따옴표
            "&#38;" to "&",  // 앰퍼샌드
            "&#60;" to "<",  // Less than
            "&#62;" to ">",  // Greater than

            // 추가 HTML 엔티티
            "&nbsp;" to " ", // 공백
            "&copy;" to "©", // 저작권 표시
            "&reg;" to "®",  // 등록 상표
            "&trade;" to "™", // 상표
            "&euro;" to "€", // 유로 기호
            "&yen;" to "¥",  // 엔화 기호
            "&pound;" to "£", // 파운드 기호
            "&cent;" to "¢",  // 센트 기호

            // 기타 특수 기호
            "&hellip;" to "…", // 줄임표 (...)
            "&ndash;" to "–", // 짧은 대시
            "&mdash;" to "—", // 긴 대시
            "&apos;" to "'",   // 작은따옴표
            "&sect;" to "§",   // 절 (§)
            "&para;" to "¶",   // 단락
            "&laquo;" to "«",  // 왼쪽 꺾쇠
            "&raquo;" to "»"   // 오른쪽 꺾쇠
        )

        var decodedString = input
        for ((entity, char) in htmlEntities) {
            decodedString = decodedString.replace(entity, char)
        }

        return decodedString
    }
}