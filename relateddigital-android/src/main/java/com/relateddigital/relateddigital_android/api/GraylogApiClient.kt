package com.relateddigital.relateddigital_android.api

import android.os.Build
import android.util.Log
import com.relateddigital.relateddigital_android.constants.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object GraylogApiClient {
    private var retrofit: Retrofit? = null
    val client: Retrofit?
        get() {
            if (Build.VERSION.SDK_INT < Constants.SDK_MIN_API_VERSION) {
                Log.e("Euromessage", "Euromessage SDK requires min API level 21!")
                return null
            }
            if (retrofit == null) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
                retrofit = Retrofit.Builder()
                    .baseUrl(Constants.GRAYLOG_ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
            }
            return retrofit
        }
}