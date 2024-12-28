package com.effort.remote.interceptor

import com.effort.remote.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Naver-Client-Id", BuildConfig.NAVER_CLIENT_ID)
            .addHeader("X-Naver-Client-Secret", BuildConfig.NAVER_CLIENT_SECRET)
            .build()
        return chain.proceed(request)
    }
}