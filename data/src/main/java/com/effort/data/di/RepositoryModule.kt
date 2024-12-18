package com.effort.data.di

import com.effort.data.repositoryimpl.auth.AuthRepositoryImpl
import com.effort.data.repositoryimpl.mypage.detail.faq.FaqRepositoryImpl
import com.effort.data.repositoryimpl.mypage.detail.notice.NoticeRepositoryImpl
import com.effort.domain.repository.auth.AuthRepository
//import com.effort.data.repositoryimpl.notice.NoticeRepositoryImpl
import com.effort.domain.repository.mypage.detail.faq.FaqRepository
import com.effort.domain.repository.mypage.detail.notice.NoticeRepository
//import com.effort.domain.repository.mypage.notice.NoticeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

internal abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFaqRepository(faqRepositoryImpl: FaqRepositoryImpl): FaqRepository

    @Binds
    @Singleton
    abstract fun bindNoticeRepository(noticeRepositoryImpl: NoticeRepositoryImpl): NoticeRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
}
