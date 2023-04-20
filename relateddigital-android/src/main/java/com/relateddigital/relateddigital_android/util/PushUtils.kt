package com.relateddigital.relateddigital_android.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.relateddigital.relateddigital_android.model.Message

object PushUtils {

    private const val LOG_TAG = "PushUtils"

    fun sendBroadCast(eventType: String, message: Message?, token: String?, context: Context) {
        try {
            val broadCastIntent = Intent(eventType)
            val bundle = Bundle()
            if (message != null) {
                bundle.putSerializable("message", message)
            }
            if (token != null) {
                bundle.putString("token", token)
            }
            broadCastIntent.putExtras(bundle)
            context.sendBroadcast(broadCastIntent)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "sendBroadCast: ${e.message}")
        } catch (ex: Throwable) {
            Log.e(LOG_TAG, "sendBroadCast: ${ex.message}")
        }
    }
}