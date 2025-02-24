package com.effort.local.di

import com.effort.data.datasource.auth.AuthLocalDataSource
import com.effort.data.datasource.home.restaurant.RestaurantLocalDataSource
import com.effort.data.datasource.mypage.MyPageLocalDataSource
import com.effort.local.datasourceimpl.user.AuthLocalDataSourceImpl
import com.effort.local.datasourceimpl.user.MyPageLocalDataSourceImpl
import com.effort.local.datasourceimpl.restaurant.RestaurantLocalDataSourceImpl
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

    @Binds
    @Singleton
    abstract fun bindRestaurantLocalDataSource(restaurantLocalDataSourceImpl: RestaurantLocalDataSourceImpl): RestaurantLocalDataSource
}