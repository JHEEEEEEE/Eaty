package com.effort.remote.interceptor

import com.effort.remote.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class KakaoApiInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "KakaoAK ${BuildConfig.KAKAO_API_KEY}")
            .build()
        return chain.proceed(request)
    }
}