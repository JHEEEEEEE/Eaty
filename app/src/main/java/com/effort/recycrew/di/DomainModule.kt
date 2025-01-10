package com.effort.recycrew.di

import com.effort.domain.usecase.auth.AuthenticateUserUseCase
import com.effort.domain.usecase.auth.CheckUserLoggedInUseCase
import com.effort.domain.usecase.mypage.ObserveUserUpdateUseCase
import com.effort.domain.usecase.mypage.detail.editprofile.CheckNicknameDuplicatedUseCase
import com.effort.domain.usecase.auth.SignOutUseCase
import com.effort.domain.usecase.home.blog.GetBlogReviewListUseCase
import com.effort.domain.usecase.home.parkinglot.GetParkingLotListUseCase
import com.effort.domain.usecase.home.restaurant.GetRestaurantListUseCase
import com.effort.domain.usecase.home.weather.GetWeatherDataUseCase
import com.effort.domain.usecase.mypage.detail.editprofile.UpdateNicknameUseCase
import com.effort.domain.usecase.mypage.detail.editprofile.UpdateProfilePicUseCase
import com.effort.domain.usecase.mypage.detail.faq.GetFaqListUseCase
import com.effort.domain.usecase.mypage.detail.notice.GetNoticeListUseCase
import com.effort.domain.usecaseimpl.auth.AuthenticateUserUseCaseImpl
import com.effort.domain.usecaseimpl.auth.CheckUserLoggedInUseCaseImpl
import com.effort.domain.usecaseimpl.mypage.ObserveUserUpdateUseCaseImpl
import com.effort.domain.usecaseimpl.mypage.detail.editprofile.CheckNicknameDuplicatedUseCaseImpl
import com.effort.domain.usecaseimpl.auth.SignOutUseCaseImpl
import com.effort.domain.usecaseimpl.home.blog.GetBlogReviewListUseCaseImpl
import com.effort.domain.usecaseimpl.home.parkinglot.GetParkingLotListUseCaseImpl
import com.effort.domain.usecaseimpl.home.restaurant.GetRestaurantListUseCaseImpl
import com.effort.domain.usecaseimpl.home.weather.GetWeatherDataUseCaseImpl
import com.effort.domain.usecaseimpl.mypage.detail.editprofile.UpdateNicknameUseCaseImpl
import com.effort.domain.usecaseimpl.mypage.detail.editprofile.UpdateProfilePicUseCaseImpl
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
    // Auth
    @Binds
    @Singleton
    abstract fun bindAuthenticateUserUseCase(authenticateUserUseCaseImpl: AuthenticateUserUseCaseImpl): AuthenticateUserUseCase

    @Binds
    @Singleton
    abstract fun bindCheckedLoggedInUseCase(checkedLoggedInUseCaseImpl: CheckUserLoggedInUseCaseImpl): CheckUserLoggedInUseCase

    // MyPage
    @Binds
    @Singleton
    abstract fun bindObserveUserUpdateUseCase(observeUserUpdateUseCaseImpl: ObserveUserUpdateUseCaseImpl): ObserveUserUpdateUseCase

    // Faq
    @Binds
    @Singleton
    abstract fun bindGetFaqListUseCase(getFaqListUseCaseImpl: GetFaqListUseCaseImpl): GetFaqListUseCase

    // Notice
    @Binds
    @Singleton
    abstract fun bindGetNoticeListUseCase(getNoticeListUseCaseImpl: GetNoticeListUseCaseImpl): GetNoticeListUseCase

    // EditProfile
    @Binds
    @Singleton
    abstract fun bindUpdateNicknameUseCase(updateNicknameUseCaseImpl: UpdateNicknameUseCaseImpl): UpdateNicknameUseCase

    @Binds
    @Singleton
    abstract fun bindUpdateProfilePicUseCase(updateProfilePicUseCaseImpl: UpdateProfilePicUseCaseImpl): UpdateProfilePicUseCase

    @Binds
    @Singleton
    abstract fun bindCheckNicknameDuplicatedUseCase(checkNicknameDuplicatedUseCaseImpl: CheckNicknameDuplicatedUseCaseImpl): CheckNicknameDuplicatedUseCase

    @Binds
    @Singleton
    abstract fun bindSignOutUseCase(signOutUseCaseImpl: SignOutUseCaseImpl): SignOutUseCase

    // Home
    @Binds
    @Singleton
    abstract fun bindGetRestaurantListUseCase(getRestaurantListUseCaseImpl: GetRestaurantListUseCaseImpl): GetRestaurantListUseCase

    @Binds
    @Singleton
    abstract fun bindGetBlogReviewListUseCase(getBlogReviewListUseCaseImpl: GetBlogReviewListUseCaseImpl): GetBlogReviewListUseCase

    @Binds
    @Singleton
    abstract fun bindGetParkingLotListUseCase(getParkingLotListUseCaseImpl: GetParkingLotListUseCaseImpl): GetParkingLotListUseCase

    @Binds
    @Singleton
    abstract fun bindGetWeatherDataUseCase(getWeatherDataUseCaseImpl: GetWeatherDataUseCaseImpl): GetWeatherDataUseCase
}

