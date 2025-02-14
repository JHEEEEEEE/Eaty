package com.effort.remote.di

import javax.inject.Qualifier

// 기본 kakao Retrofit 인스턴스
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WeatherRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NaverMapRetrofit