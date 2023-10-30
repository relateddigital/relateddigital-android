package com.relateddigital.relateddigital_android.util

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat


interface NotificationPermissionCallback {
    fun onPermissionResult(granted: Boolean)
}

class NotificationPermissionActivity : ComponentActivity() {

    companion object {
        var callback: NotificationPermissionCallback? = null
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        callback?.onPermissionResult(isGranted)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        val permission = "android.permission.POST_NOTIFICATIONS"
        val granted = NotificationManagerCompat.from(this).areNotificationsEnabled()

        if (!granted) {
            requestPermissionLauncher.launch(permission)
        } else {
            callback?.onPermissionResult(granted)
            finish()
        }
    }
}
