package com.effort.remote.di

import javax.inject.Qualifier

// 기본 kakao Retrofit 인스턴스 (기타 API)
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WeatherRetrofit