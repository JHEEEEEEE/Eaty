package com.effort.recycrew.di

import com.effort.domain.usecase.auth.AuthenticateUserUseCase
import com.effort.domain.usecase.auth.CheckUserLoggedInUseCase
import com.effort.domain.usecase.auth.SignOutUseCase
import com.effort.domain.usecase.home.suggestion.GetSuggestionListUseCase
import com.effort.domain.usecase.home.restaurant.GetRestaurantListUseCase
import com.effort.domain.usecase.home.restaurant.detail.blog.GetBlogReviewListUseCase
import com.effort.domain.usecase.home.restaurant.detail.comment.AddCommentUseCase
import com.effort.domain.usecase.home.restaurant.detail.comment.GetCommentUseCase
import com.effort.domain.usecase.home.restaurant.detail.parkinglot.GetParkingLotListUseCase
import com.effort.domain.usecase.home.restaurant.detail.subway.GetSubwayStationUseCase
import com.effort.domain.usecase.home.restaurant.detail.weather.GetWeatherDataUseCase
import com.effort.domain.usecase.home.restaurant.favorites.AddRestaurantToFavoritesUseCase
import com.effort.domain.usecase.home.restaurant.favorites.CheckIfRestaurantIsFavoriteUseCase
import com.effort.domain.usecase.home.restaurant.favorites.GetFavoriteListUseCase
import com.effort.domain.usecase.home.restaurant.favorites.RemoveRestaurantFromFavoritesUseCase
import com.effort.domain.usecase.home.restaurant.navigation.GetNavigationPathUseCase
import com.effort.domain.usecase.share.ShareUseCase
import com.effort.domain.usecase.mypage.ObserveUserUpdateUseCase
import com.effort.domain.usecase.mypage.detail.editprofile.CheckNicknameDuplicatedUseCase
import com.effort.domain.usecase.mypage.detail.editprofile.UpdateNicknameUseCase
import com.effort.domain.usecase.mypage.detail.editprofile.UpdateProfilePicUseCase
import com.effort.domain.usecase.mypage.detail.faq.GetFaqListUseCase
import com.effort.domain.usecase.mypage.detail.notice.GetNoticeListUseCase
import com.effort.domain.usecaseimpl.auth.AuthenticateUserUseCaseImpl
import com.effort.domain.usecaseimpl.auth.CheckUserLoggedInUseCaseImpl
import com.effort.domain.usecaseimpl.auth.SignOutUseCaseImpl
import com.effort.domain.usecaseimpl.home.GetSuggestionListUseCaseImpl
import com.effort.domain.usecaseimpl.home.restaurant.GetRestaurantListUseCaseImpl
import com.effort.domain.usecaseimpl.home.restaurant.detail.blog.GetBlogReviewListUseCaseImpl
import com.effort.domain.usecaseimpl.home.restaurant.detail.comment.AddCommentUseCaseImpl
import com.effort.domain.usecaseimpl.home.restaurant.detail.comment.GetCommentUseCaseImpl
import com.effort.domain.usecaseimpl.home.restaurant.detail.parkinglot.GetParkingLotListUseCaseImpl
import com.effort.domain.usecaseimpl.home.restaurant.detail.subway.GetSubwayStationUseCaseImpl
import com.effort.domain.usecaseimpl.home.restaurant.detail.weather.GetWeatherDataUseCaseImpl
import com.effort.domain.usecaseimpl.home.restaurant.favorites.AddRestaurantToFavoritesUseCaseImpl
import com.effort.domain.usecaseimpl.home.restaurant.favorites.CheckIfRestaurantIsFavoriteUseCaseImpl
import com.effort.domain.usecaseimpl.home.restaurant.favorites.GetFavoriteListUseCaseImpl
import com.effort.domain.usecaseimpl.home.restaurant.favorites.RemoveRestaurantFromFavoritesUseCaseImpl
import com.effort.domain.usecaseimpl.home.restaurant.navigation.GetNavigationPathUseCaseImpl
import com.effort.domain.usecaseimpl.share.ShareUseCaseImpl
import com.effort.domain.usecaseimpl.mypage.ObserveUserUpdateUseCaseImpl
import com.effort.domain.usecaseimpl.mypage.detail.editprofile.CheckNicknameDuplicatedUseCaseImpl
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

    // RestaurantList
    @Binds
    @Singleton
    abstract fun bindGetRestaurantListUseCase(getRestaurantListUseCaseImpl: GetRestaurantListUseCaseImpl): GetRestaurantListUseCase


    // BlogReview
    @Binds
    @Singleton
    abstract fun bindGetBlogReviewListUseCase(getBlogReviewListUseCaseImpl: GetBlogReviewListUseCaseImpl): GetBlogReviewListUseCase

    // ParkingLot
    @Binds
    @Singleton
    abstract fun bindGetParkingLotListUseCase(getParkingLotListUseCaseImpl: GetParkingLotListUseCaseImpl): GetParkingLotListUseCase

    // Weather
    @Binds
    @Singleton
    abstract fun bindGetWeatherDataUseCase(getWeatherDataUseCaseImpl: GetWeatherDataUseCaseImpl): GetWeatherDataUseCase

    // Comment
    @Binds
    @Singleton
    abstract fun bindGetCommentUseCase(getCommentUseCaseImpl: GetCommentUseCaseImpl): GetCommentUseCase

    @Binds
    @Singleton
    abstract fun bindAddCommentUseCase(addCommentUseCaseImpl: AddCommentUseCaseImpl): AddCommentUseCase

    // Home
    @Binds
    @Singleton
    abstract fun bindGetSuggestionsUseCase(getSuggestionUseCaseImpl: GetSuggestionListUseCaseImpl): GetSuggestionListUseCase

    // Favorites
    @Binds
    @Singleton
    abstract fun bindAddRestaurantToFavoritesUseCase(addRestaurantToFavoritesUseCaseImpl: AddRestaurantToFavoritesUseCaseImpl): AddRestaurantToFavoritesUseCase

    @Binds
    @Singleton
    abstract fun bindRemoveRestaurantToFavoritesUseCase(removeRestaurantFromFavoritesUseCaseImpl: RemoveRestaurantFromFavoritesUseCaseImpl): RemoveRestaurantFromFavoritesUseCase

    @Binds
    @Singleton
    abstract fun bindCheckIfRestaurantIsFavoriteUseCase(checkIfRestaurantIsFavoriteUseCaseImpl: CheckIfRestaurantIsFavoriteUseCaseImpl): CheckIfRestaurantIsFavoriteUseCase

    @Binds
    @Singleton
    abstract fun bindGetFavoriteListUseCase(getFavoriteListUseCaseImpl: GetFavoriteListUseCaseImpl): GetFavoriteListUseCase

    // Share
    @Binds
    @Singleton
    abstract fun bindShareUseCase(shareUseCaseImpl: ShareUseCaseImpl): ShareUseCase

    // Navigation
    @Binds
    @Singleton
    abstract fun bindGetNavigationUseCase(getNavigationPathUseCaseImpl: GetNavigationPathUseCaseImpl): GetNavigationPathUseCase

    // SubwayStation
    @Binds
    @Singleton
    abstract fun bindGetSubwayStationUseCase(getSubwayStationUseCaseImpl: GetSubwayStationUseCaseImpl): GetSubwayStationUseCase
}

