package com.effort.recycrew.di

import com.effort.domain.usecase.mypage.detail.faq.GetFaqListUseCase
import com.effort.domain.usecase.mypage.detail.notice.GetNoticeListUseCase
import com.effort.domain.usecaseimpl.mypage.detail.faq.GetFaqListUseCaseImpl
import com.effort.domain.usecaseimpl.mypage.detail.notice.GetNoticeListUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    @Singleton
    abstract fun bindGetFaqListUseCase(getFaqListUseCaseImpl: GetFaqListUseCaseImpl): GetFaqListUseCase

    @Binds
    @Singleton
    abstract fun bindGetNoticeListUseCase(getNoticeListUseCaseImpl: GetNoticeListUseCaseImpl): GetNoticeListUseCase
}
