package com.effort.remote.di

import com.effort.remote.interceptor.ApiInterceptor
import com.effort.remote.service.home.RestaurantService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Json 인스턴스를 싱글톤으로 정의
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true // 정의되지 않은 키 무시
        isLenient = true         // 관대한 파싱 허용
        encodeDefaults = true   // 기본값 허용
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/") // API 기본 URL
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType)) // Json 인스턴스 재사용
            .build()
    }

    @Provides
    @Singleton
    fun provideRestaurantService(retrofit: Retrofit): RestaurantService {
        return retrofit.create(RestaurantService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(ApiInterceptor()) // 인증 인터셉터 추가
            .addInterceptor(logging)         // 로깅 인터셉터 추가
            .connectTimeout(30, TimeUnit.SECONDS) // 타임아웃 설정
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}