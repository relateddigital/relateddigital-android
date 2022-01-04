package com.relateddigital.relateddigital_android.push.services

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.model.Message
import com.relateddigital.relateddigital_android.util.LogUtils

class RelatedDigitalFirebaseMessagingService : FirebaseMessagingService() {
    companion object{
        private const val LOG_TAG = "RDFMessagingService"
    }
    override fun onNewToken(token: String) {
        Log.i(LOG_TAG, "On new token : $token")
        val googleAppAlias: String = RelatedDigital.getGoogleAppAlias(this)
        val huaweiAppAlias: String = RelatedDigital.getHuaweiAppAlias(this)
        RelatedDigital.setIsPushNotificationEnabled(
            this,
            true,
            googleAppAlias,
            huaweiAppAlias,
            token
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val remoteMessageData = remoteMessage.data
        if (remoteMessageData.isEmpty()) {
            Log.e(LOG_TAG, "Push message is empty!")
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                this,
                "e",
                "RDFMessagingService : " + "Push message is empty!",
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            return
        }
        val pushMessage = Message(this, remoteMessageData)
        if (pushMessage.emPushSp == null) {
            Log.i(
                LOG_TAG,
                "The push notification was not coming from Related Digital! Ignoring.."
            )
            return
        }
        Log.d(LOG_TAG, "FirebasePayload : " + Gson().toJson(pushMessage))
    }
}