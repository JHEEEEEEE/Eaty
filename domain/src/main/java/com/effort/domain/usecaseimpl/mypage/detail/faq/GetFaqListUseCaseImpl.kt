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
    override fun invoke(): Flow<DataResource<List<Faq>>> =
        faqRepository.getFaqs().map { dataResource ->
            when (dataResource) {
                is DataResource.Success -> {
                    // 날짜 최신순으로 정렬
                    val sortedList = dataResource.data.sortedByDescending { it.timestamp }
                    DataResource.Success(sortedList)
                }

                else -> dataResource// 상태 그대로 전달
            }
        }
}