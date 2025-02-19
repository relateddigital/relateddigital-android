package com.relateddigital.relateddigital_android.api

import android.os.Build
import android.util.Log
import com.relateddigital.relateddigital_android.constants.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object JSApiClient {
    private var retrofit: Retrofit? = null
    fun getClient(connectTimeOutInSec: Int): Retrofit? {
        if (Build.VERSION.SDK_INT < Constants.SDK_MIN_API_VERSION) {
            Log.e("RelatedDigital", "RelatedDigital SDK requires min API level 21!")
            return null
        }
        if (retrofit == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(SafeInterceptor())
                .connectTimeout(connectTimeOutInSec.toLong(), TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
            synchronized(SApiClient::class.java) {
                if (retrofit == null) {
                    retrofit = Retrofit.Builder()
                        .baseUrl("https://mbls.visilabs.net/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient.build())
                        .build()
                }
            }
        }
        return retrofit
    }


}