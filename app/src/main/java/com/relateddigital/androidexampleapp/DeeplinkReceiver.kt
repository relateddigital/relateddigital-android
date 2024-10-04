package com.relateddigital.androidexampleapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DeeplinkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {


        val deeplink = intent?.getStringExtra("deeplink")
        // Deeplink ile yapılacak işlemler
        if (deeplink != null) {
            // Deeplink'i işleyin

        }
    }
}