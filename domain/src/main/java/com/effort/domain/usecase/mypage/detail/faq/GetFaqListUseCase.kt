package com.effort.domain.usecase.mypage.detail.faq

import com.effort.domain.DataResource
import com.effort.domain.model.mypage.detail.faq.Faq
import kotlinx.coroutines.flow.Flow


interface GetFaqListUseCase {

    operator fun invoke(): Flow<DataResource<List<Faq>>>
}