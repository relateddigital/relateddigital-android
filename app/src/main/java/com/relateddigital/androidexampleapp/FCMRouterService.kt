package com.relateddigital.androidexampleapp // YOUR PROJECT PACKAGE NAME

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.relateddigital.relateddigital_android.push.services.RelatedDigitalFirebaseMessagingService

class FCMRouterService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        Log.d(TAG, "Message received. Data payload: $data")

        // 1. Related Digital mesajı mı?
        if (data.containsKey("emPushSp") && data["emPushSp"] != null) {
            Log.d(TAG, "Routing to Related Digital (Static Handler)...")
            try {
                // Artık doğrudan companion object'teki metodu çağırabilirsiniz
                RelatedDigitalFirebaseMessagingService.handlePushMessage(this, remoteMessage)
                Log.d(TAG, "Message forwarded to Related Digital static handler.")
            } catch (e: Exception) {
                Log.e(TAG, "Error calling Related Digital static handler", e)
            }

        } else {
            Log.w(TAG, "Message source not Related Digital")

        }
    }

    override fun onNewToken(token: String) {
        Log.i(TAG, "Refreshed FCM token in Router: $token")

        // Token'ı Related Digital'a gönder (yeni statik metot ile)
        try {
            RelatedDigitalFirebaseMessagingService.handleNewToken(this, token)
            Log.i(TAG,"Token sent to Related Digital static handler.")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending token via Related Digital static handler", e)
        }

    }


    companion object {
        private const val TAG = "FCMRouterService"
    }
}