package com.effort.data.datasource

import com.effort.data.model.NoticeEntity

interface NoticeRemoteDataSource {

    suspend fun getNotices(): List<NoticeEntity>
}