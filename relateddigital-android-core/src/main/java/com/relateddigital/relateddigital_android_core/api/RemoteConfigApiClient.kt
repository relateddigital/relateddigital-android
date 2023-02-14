package com.relateddigital.relateddigital_android_core.api

import android.os.Build
import android.util.Log
import com.relateddigital.relateddigital_android_core.constants.Constants
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RemoteConfigApiClient {
    private var retrofit: Retrofit? = null
    val client: Retrofit?
        get() {
            if (Build.VERSION.SDK_INT < Constants.SDK_MIN_API_VERSION) {
                Log.e("RelatedDigital", "RelatedDigital SDK requires min API level 21!")
                return null
            }
            if (retrofit == null) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                synchronized(RemoteConfigApiClient::class.java) {
                    if (retrofit == null) {
                        retrofit = Retrofit.Builder()
                            .baseUrl(Constants.REMOTE_CONFIG_ENDPOINT)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(httpClient.build())
                            .build()
                    }
                }
            }
            return retrofit
        }
}