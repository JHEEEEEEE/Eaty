package com.effort.data.di

import com.effort.data.repositoryimpl.FaqRepositoryImpl
//import com.effort.data.repositoryimpl.NoticeRepositoryImpl
import com.effort.domain.repository.FaqRepository
//import com.effort.domain.repository.NoticeRepository
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
    abstract fun bindFaqRepository(repo: FaqRepositoryImpl): FaqRepository

/*    @Binds
    @Singleton
    abstract fun bindNoticeRepository(repo: NoticeRepositoryImpl): NoticeRepository*/
}
