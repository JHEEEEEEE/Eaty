package com.effort.remote.di

import com.effort.remote.service.FirebaseService
import com.effort.remote.service.FirebaseServiceImpl
import com.google.firebase.firestore.FirebaseFirestore
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

    // FirebaseService 인터페이스와 FirebaseServiceImpl 구현체를 연결
    // Hilt가 FirebaseService 의존성을 주입할 때 FirebaseServiceImpl 인스턴스를 제공
    @Provides
    @Singleton
    fun provideFirebaseService(
        firestore: FirebaseFirestore
    ): FirebaseService {
        return FirebaseServiceImpl(firestore)
    }
}