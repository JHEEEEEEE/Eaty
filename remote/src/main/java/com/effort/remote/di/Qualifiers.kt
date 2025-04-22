package com.effort.remote.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WeatherRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NaverMapRetrofit