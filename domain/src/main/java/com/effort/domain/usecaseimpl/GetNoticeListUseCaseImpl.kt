package com.effort.domain.usecaseimpl

import com.effort.domain.DataResource
import com.effort.domain.model.Notice
import com.effort.domain.repository.NoticeRepository
import com.effort.domain.usecase.GetNoticeListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNoticeListUseCaseImpl @Inject constructor(
    private val noticeRepository: NoticeRepository
): GetNoticeListUseCase {

    override fun invoke(): Flow<DataResource<List<Notice>>> =
        noticeRepository.getNotices().map { dataResource ->
            when (dataResource) {
                is DataResource.Success -> {
                    // 날짜 최신순으로 정렬
                    val sortedList = dataResource.data.sortedByDescending { it.timestamp }
                    DataResource.Success(sortedList)
                }

                else -> dataResource
            }
        }
}
