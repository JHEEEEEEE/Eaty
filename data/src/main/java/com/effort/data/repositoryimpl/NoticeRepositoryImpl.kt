package com.effort.data.repositoryimpl

import com.effort.data.datasource.NoticeRemoteDataSource
import com.effort.domain.DataResource
import com.effort.domain.model.Notice
import com.effort.domain.repository.NoticeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NoticeRepositoryImpl @Inject constructor(
    private val noticeRemoteDataSource: NoticeRemoteDataSource
) : NoticeRepository {

    override fun getNotices(): Flow<DataResource<List<Notice>>> = flow {
        emit(DataResource.Loading())

        try {
            val notices = noticeRemoteDataSource.getNotices().map { it.toDomain() }
            emit(DataResource.success(notices))
        } catch (e: Exception) {
            emit(DataResource.error(e))
        }
    }
}
