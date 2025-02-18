package com.effort.remote.di

import com.effort.remote.service.auth.AuthService
import com.effort.remote.service.auth.AuthServiceImpl
import com.effort.remote.service.home.SuggestionService
import com.effort.remote.service.home.SuggestionServiceImpl
import com.effort.remote.service.home.restaurant.detail.comment.CommentService
import com.effort.remote.service.home.restaurant.detail.comment.CommentServiceImpl
import com.effort.remote.service.home.restaurant.favorites.FavoriteService
import com.effort.remote.service.home.restaurant.favorites.FavoriteServiceImpl
import com.effort.remote.service.mypage.MyPageService
import com.effort.remote.service.mypage.MyPageServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFireStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideMyPageService(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): MyPageService {
        return MyPageServiceImpl(auth, firestore, storage)
    }

    @Provides
    @Singleton
    fun provideAuthService(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthService {
        return AuthServiceImpl(auth, firestore)
    }

    @Provides
    @Singleton
    fun provideCommentService(
        firestore: FirebaseFirestore
    ): CommentService {
        return CommentServiceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideSuggestionService(
        firestore: FirebaseFirestore
    ): SuggestionService {
        return SuggestionServiceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideFavoriteService(
        firestore: FirebaseFirestore
    ): FavoriteService {
        return FavoriteServiceImpl(firestore)
    }
}