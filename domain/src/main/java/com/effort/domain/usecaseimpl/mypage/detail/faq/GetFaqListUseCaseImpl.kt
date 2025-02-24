package com.effort.domain.usecaseimpl.mypage.detail.faq

import com.effort.domain.DataResource
import com.effort.domain.model.mypage.detail.faq.Faq
import com.effort.domain.repository.mypage.detail.faq.FaqRepository
import com.effort.domain.usecase.mypage.detail.faq.GetFaqListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFaqListUseCaseImpl @Inject constructor(
    private val faqRepository: FaqRepository
) : GetFaqListUseCase {

    /**
     * FAQ 데이터를 가져와 최신순으로 정렬하여 반환한다.
     * - 서버에서 가져온 데이터를 클라이언트에서 정렬 처리
     */
    override fun invoke(): Flow<DataResource<List<Faq>>> =
        faqRepository.getFaqs().map { dataResource ->
            when (dataResource) {
                is DataResource.Success -> {
                    // 최신순 정렬 (timestamp 기준)
                    val sortedList = dataResource.data.sortedByDescending { it.timestamp }
                    DataResource.Success(sortedList)
                }
                else -> dataResource // 기존 상태 유지
            }
        }
}