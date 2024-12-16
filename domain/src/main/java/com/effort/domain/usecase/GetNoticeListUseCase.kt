package com.effort.domain.usecase

import com.effort.domain.DataResource
import com.effort.domain.model.Notice
import kotlinx.coroutines.flow.Flow

interface GetNoticeListUseCase {

    operator fun invoke(): Flow<DataResource<List<Notice>>>
}