package com.effort.recycrew.di

import com.effort.domain.usecase.GetFaqListUseCase
import com.effort.domain.usecase.GetNoticeListUseCase
import com.effort.domain.usecaseimpl.GetFaqListUseCaseImpl
import com.effort.domain.usecaseimpl.GetNoticeListUseCaseImpl
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
