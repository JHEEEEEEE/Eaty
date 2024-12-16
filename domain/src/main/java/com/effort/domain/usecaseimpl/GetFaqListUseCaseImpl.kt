package com.effort.domain.usecaseimpl

import com.effort.domain.DataResource
import com.effort.domain.model.Faq
import com.effort.domain.repository.FaqRepository
import com.effort.domain.usecase.GetFaqListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class GetFaqListUseCaseImpl @Inject constructor(
    private val faqRepository: FaqRepository
) : GetFaqListUseCase {
    override operator fun invoke(): Flow<DataResource<List<Faq>>> =
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