package com.effort.data.di

import com.effort.data.repositoryimpl.auth.AuthRepositoryImpl
import com.effort.data.repositoryimpl.home.RestaurantRepositoryImpl
import com.effort.data.repositoryimpl.home.detail.info.RestaurantInfoRepositoryImpl
import com.effort.data.repositoryimpl.home.detail.review.RestaurantReviewRepositoryImpl
import com.effort.data.repositoryimpl.home.detail.surrounding.RestaurantSurroundingRepositoryImpl
import com.effort.data.repositoryimpl.location.LocationRepositoryImpl
import com.effort.data.repositoryimpl.mypage.MyPageRepositoryImpl
import com.effort.data.repositoryimpl.mypage.detail.editprofile.EditProfileRepositoryImpl
import com.effort.data.repositoryimpl.mypage.detail.faq.FaqRepositoryImpl
import com.effort.data.repositoryimpl.mypage.detail.notice.NoticeRepositoryImpl
import com.effort.domain.repository.auth.AuthRepository
import com.effort.domain.repository.home.RestaurantRepository
import com.effort.domain.repository.home.detail.info.RestaurantInfoRepository
import com.effort.domain.repository.home.detail.review.RestaurantReviewRepository
import com.effort.domain.repository.home.detail.surrounding.RestaurantSurroundingRepository
import com.effort.domain.repository.location.LocationRepository
import com.effort.domain.repository.mypage.MyPageRepository
import com.effort.domain.repository.mypage.detail.editprofile.EditProfileRepository
import com.effort.domain.repository.mypage.detail.faq.FaqRepository
import com.effort.domain.repository.mypage.detail.notice.NoticeRepository
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

    @Binds
    @Singleton
    abstract fun bindMyPageRepository(myPageRepositoryImpl: MyPageRepositoryImpl): MyPageRepository

    @Binds
    @Singleton
    abstract fun bindEditProfileRepository(editProfileRepositoryImpl: EditProfileRepositoryImpl): EditProfileRepository

    @Binds
    @Singleton
    abstract fun bindRestaurantRepository(restaurantRepositoryImpl: RestaurantRepositoryImpl): RestaurantRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindRestaurantInfoRepository(restaurantInfoRepositoryImpl: RestaurantInfoRepositoryImpl): RestaurantInfoRepository

    @Binds
    @Singleton
    abstract fun bindRestaurantReviewRepository(restaurantReviewRepositoryImpl: RestaurantReviewRepositoryImpl): RestaurantReviewRepository

    @Binds
    @Singleton
    abstract fun bindRestaurantSurroundingRepository(restaurantSurroundingRepositoryImpl: RestaurantSurroundingRepositoryImpl): RestaurantSurroundingRepository
}
