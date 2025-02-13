package com.effort.remote.di

import com.effort.remote.interceptor.KakaoApiInterceptor
import com.effort.remote.service.home.restaurant.detail.blog.BlogReviewService
import com.effort.remote.service.home.restaurant.RestaurantService
import com.effort.remote.service.home.restaurant.detail.subway.SubwayService
import com.effort.remote.service.home.restaurant.detail.weather.WeatherService
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
    @KakaoRetrofit
    fun provideKakaoRetrofit(
        okHttpClient: OkHttpClient, // 공통 클라이언트 사용
        kakaoApiInterceptor: KakaoApiInterceptor,
        json: Json
    ): Retrofit {
        val client = okHttpClient.newBuilder()
            .addInterceptor(kakaoApiInterceptor) // 인증 인터셉터 추가
            .build()

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/") // API 기본 URL
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideRestaurantService(@KakaoRetrofit retrofit: Retrofit): RestaurantService {
        return retrofit.create(RestaurantService::class.java)
    }

    @Provides
    @Singleton
    fun provideBlogReviewService(@KakaoRetrofit retrofit: Retrofit): BlogReviewService {
        return retrofit.create(BlogReviewService::class.java)
    }

    @Provides
    @Singleton
    fun provideSubwayService(@KakaoRetrofit retrofit: Retrofit): SubwayService {
        return retrofit.create(SubwayService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging) // 로깅 인터셉터 추가
            .connectTimeout(30, TimeUnit.SECONDS) // 타임아웃 설정
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    //Weather Service
    @Provides
    @Singleton
    fun provideWeatherService(@WeatherRetrofit retrofit: Retrofit): WeatherService {
        return retrofit.create(WeatherService::class.java)
    }

    @Provides
    @Singleton
    @WeatherRetrofit
    fun provideWeatherRetrofit(
        okHttpClient: OkHttpClient, // 공통 클라이언트 사용
        json: Json // JSON 직렬화 라이브러리 주입
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .client(okHttpClient) // 공통 클라이언트 적용
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}