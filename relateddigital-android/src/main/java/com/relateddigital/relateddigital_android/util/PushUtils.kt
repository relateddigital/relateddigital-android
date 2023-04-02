package com.relateddigital.relateddigital_android.util

import android.content.Context
import android.content.Intent
import android.util.Log
import com.relateddigital.relateddigital_android.model.Message

object PushUtils {

    private const val LOG_TAG = "PushUtils"

    fun sendBroadCast(eventType: String, message: Message?, token: String?, context: Context) {
        try {
            val broadCastIntent = Intent(eventType)
            if (message != null) {
                broadCastIntent.putExtra("message", message)
            }
            if (token != null) {
                broadCastIntent.putExtra("token", token)
            }
            context.sendBroadcast(broadCastIntent)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "sendBroadCast: ${e.message}")
        } catch (ex: Throwable) {
            Log.e(LOG_TAG, "sendBroadCast: ${ex.message}")
        }
    }
}