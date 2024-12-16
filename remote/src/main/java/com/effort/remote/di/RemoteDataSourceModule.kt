package com.effort.remote.di

import com.effort.data.datasource.mypage.detail.faq.FaqRemoteDataSource
import com.effort.data.datasource.mypage.detail.notice.NoticeRemoteDataSource
import com.effort.remote.datasourceimpl.mypage.detail.faq.FaqRemoteDataSourceImpl
import com.effort.remote.datasourceimpl.mypage.detail.notice.NoticeRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteDataSourceModule {

    // Remote Layer의 FaqRemoteDataSourceImpl을 Data Layer의 FaqRemoteDataSource 인터페이스에 바인딩
    // Hilt가 이 설정을 기반으로 FaqRemoteDataSource 의존성을 제공
    @Binds
    @Singleton
    abstract fun bindFaqRemoteDataSource(faqRemoteDataSourceImpl: FaqRemoteDataSourceImpl): FaqRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindNoticeRemoteDataSource(noticeRemoteDataSourceImpl: NoticeRemoteDataSourceImpl): NoticeRemoteDataSource
}