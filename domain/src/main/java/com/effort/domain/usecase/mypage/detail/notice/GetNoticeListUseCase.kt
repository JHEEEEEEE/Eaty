package com.effort.domain.usecase.mypage.detail.notice

import com.effort.domain.DataResource
import com.effort.domain.model.mypage.detail.notice.Notice
import kotlinx.coroutines.flow.Flow

interface GetNoticeListUseCase {

    operator fun invoke(): Flow<DataResource<List<Notice>>>
}