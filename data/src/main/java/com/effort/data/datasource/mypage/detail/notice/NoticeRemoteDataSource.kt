package com.effort.data.datasource.mypage.detail.notice

import com.effort.data.model.mypage.detail.notice.NoticeEntity

interface NoticeRemoteDataSource {

    suspend fun getNotices(): List<NoticeEntity>
}