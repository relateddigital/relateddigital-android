package com.relateddigital.relateddigital_android.api

import android.util.Log
import okhttp3.Response
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.ResponseBody
import java.io.IOException

class SafeInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            chain.proceed(chain.request()) // Normal isteği yap
        } catch (e: IOException) {
            Log.e("SDK", "Network hatası: ${e.message}, boş yanıt döndürülüyor.")
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(503) // Fake bir "Service Unavailable" yanıtı döndür
                .message("Fallback response")
                .body(ResponseBody.create("application/json".toMediaTypeOrNull(), "{}"))
                .build()
        }
    }
}