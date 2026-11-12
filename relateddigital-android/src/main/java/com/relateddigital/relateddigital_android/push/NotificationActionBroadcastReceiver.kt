package com.relateddigital.relateddigital_android.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationActionBroadcastReceiver() : BroadcastReceiver() {
    private var callback: NotificationActionListener? = null

    constructor(callback: NotificationActionListener) : this() {
        this.callback = callback
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            var linkUri = ""
            if (intent?.action == "ACTION_CLICK") {
                linkUri = intent.getStringExtra("KEY_ACTION_ITEM").orEmpty()
            }

            (callback ?: listener)?.onNotificationActionClicked(linkUri)
        } catch (t: Throwable) {
            Log.e(LOG_TAG, "Notification action could not be handled : " + t.message)
        }
    }

    companion object {
        private const val LOG_TAG = "NotificationAction"

        @JvmStatic
        var listener: NotificationActionListener? = null
    }
}
