package com.effort.data.datasource.mypage.detail.faq

import com.effort.data.model.mypage.detail.faq.FaqEntity

interface FaqRemoteDataSource {

    suspend fun getFaqs(): List<FaqEntity>
}