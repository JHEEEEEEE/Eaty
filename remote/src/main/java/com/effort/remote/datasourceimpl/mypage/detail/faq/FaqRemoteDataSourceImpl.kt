package com.effort.remote.datasourceimpl.mypage.detail.faq

import com.effort.data.datasource.mypage.detail.faq.FaqRemoteDataSource
import com.effort.data.model.mypage.detail.faq.FaqEntity
import com.effort.remote.service.mypage.MyPageService
import javax.inject.Inject

class FaqRemoteDataSourceImpl @Inject constructor(
    private val myPageService: MyPageService
) : FaqRemoteDataSource {

    override suspend fun getFaqs(): List<FaqEntity> {
        return myPageService.getFaqs().resultFaqs.map { it.toData() }
    }
}