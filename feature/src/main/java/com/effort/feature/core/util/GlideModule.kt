package com.effort.feature.core.util

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.io.InputStream
import java.util.concurrent.TimeUnit

@GlideModule
class GlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // OkHttpClient 설정
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .connectionSpecs(
                listOf(
                    ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_3)
                        .cipherSuites(
                            CipherSuite.TLS_AES_256_GCM_SHA384,
                            CipherSuite.TLS_AES_128_GCM_SHA256,
                            CipherSuite.TLS_CHACHA20_POLY1305_SHA256
                        )
                        .build()
                )
            )
            .retryOnConnectionFailure(true)
            .build()

        registry.replace(
            GlideUrl::class.java, InputStream::class.java,
            OkHttpUrlLoader.Factory(okHttpClient)
        )
    }
}
