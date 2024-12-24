package com.effort.local.di

import com.effort.data.datasource.auth.AuthLocalDataSource
import com.effort.data.datasource.mypage.MyPageLocalDataSource
import com.effort.local.datasourceimpl.AuthLocalDataSourceImpl
import com.effort.local.datasourceimpl.MyPageLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LocalDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindAuthLocalDataSource(authLocalDataSourceImpl: AuthLocalDataSourceImpl): AuthLocalDataSource

    @Binds
    @Singleton
    abstract fun bindMyPageLocalDataSource(myPageLocalDataSourceImpl: MyPageLocalDataSourceImpl): MyPageLocalDataSource
}