package com.effort.remote.datasourceimpl.mypage.detail.notice

import com.effort.data.datasource.mypage.detail.notice.NoticeRemoteDataSource
import com.effort.data.model.mypage.detail.notice.NoticeEntity
import com.effort.remote.service.mypage.MyPageService
import javax.inject.Inject


class NoticeRemoteDataSourceImpl @Inject constructor(
    private val myPageService: MyPageService
) : NoticeRemoteDataSource {

    override suspend fun getNotices(): List<NoticeEntity> {
        return myPageService.getNotices().resultNotices.map { it.toData() }
    }
}