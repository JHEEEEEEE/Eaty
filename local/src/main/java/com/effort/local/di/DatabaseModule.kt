package com.effort.local.di

import android.content.Context
import androidx.room.Room
import com.effort.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): UserRoomDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            UserRoomDatabase::class.java,
            "user" // 데이터베이스 이름
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: UserRoomDatabase): UserDao {
        return database.userDao() // UserDao 반환
    }
}