package com.effort.remote.datasourceimpl

import com.effort.data.datasource.NoticeRemoteDataSource
import com.effort.data.model.FaqEntity
import com.effort.data.model.NoticeEntity
import com.effort.remote.service.FirebaseService
import javax.inject.Inject


class NoticeRemoteDataSourceImpl @Inject constructor(
    private val firebaseService: FirebaseService
): NoticeRemoteDataSource {

    override suspend fun getNotices(): List<NoticeEntity> {
        return firebaseService.getNotices().resultNotices.map { it.toData() }
    }

}