package com.relateddigital.relateddigital_android.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationActionBroadcastReceiver (private val callback: NotificationActionListener) : BroadcastReceiver() {
   override fun onReceive(context: Context?, intent: Intent?) {
        var linkUri= ""
        if (intent?.action=="ACTION_CLICK"){
            linkUri = intent.getStringExtra("KEY_ACTION_ITEM").toString()
        }

        callback.onNotificationActionClicked(linkUri)
    }
}