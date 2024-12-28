package com.effort.remote.di

import com.effort.remote.service.auth.AuthService
import com.effort.remote.service.auth.AuthServiceImpl
import com.effort.remote.service.mypage.FirebaseService
import com.effort.remote.service.mypage.FirebaseServiceImpl
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

    // Firestore 인스턴스를 Singleton으로 제공
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

    // FirebaseService 인터페이스와 FirebaseServiceImpl 구현체를 연결
    // Hilt가 FirebaseService 의존성을 주입할 때 FirebaseServiceImpl 인스턴스를 제공
    @Provides
    @Singleton
    fun provideFirebaseService(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): FirebaseService {
        return FirebaseServiceImpl(auth, firestore, storage)
    }

    @Provides
    @Singleton
    fun provideAuthService(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthService {
        return AuthServiceImpl(auth, firestore)
    }
}