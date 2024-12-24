package com.effort.domain.repository.mypage.detail.notice

import com.effort.domain.DataResource
import com.effort.domain.model.mypage.detail.notice.Notice
import kotlinx.coroutines.flow.Flow

interface NoticeRepository {

    fun getNotices(): Flow<DataResource<List<Notice>>>
}
