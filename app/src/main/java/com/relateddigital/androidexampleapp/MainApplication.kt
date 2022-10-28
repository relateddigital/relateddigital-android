package com.relateddigital.androidexampleapp

import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.model.RDNotificationPriority

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize RelatedDigital with mandatory information
        RelatedDigital.init(
            context = applicationContext,
            organizationId = "676D325830564761676D453D",
            profileId = "356467332F6533766975593D",
            dataSource = "visistore")

        // Enable In-App Notifications
        RelatedDigital.setIsInAppNotificationEnabled(
            context = applicationContext,
            isInAppNotificationEnabled = true
        )

        // Enable Push Notifications
        getFirebaseToken()

        // Enable Geofencing
        RelatedDigital.setIsGeofenceEnabled(
            context = applicationContext,
            isGeofenceEnabled = true
        )
    }

    private fun getFirebaseToken(){
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("Firebase Token : ", "Getting the token failed!!!")
                    return@OnCompleteListener
                }
                val token = task.result

                // Enable Push Notifications
                RelatedDigital.setIsPushNotificationEnabled(
                    context = applicationContext,
                    isPushNotificationEnabled = true,
                    googleAppAlias = Constants.GOOGLE_APP_ALIAS,
                    huaweiAppAlias = Constants.HUAWEI_APP_ALIAS,
                    token = token,
                    notificationSmallIcon = R.drawable.text_icon,
                    notificationSmallIconDarkMode = R.drawable.text_icon_dark_mode,
                    isNotificationLargeIcon = true,
                    notificationLargeIcon = R.mipmap.ic_launcher,
                    notificationLargeIconDarkMode = R.mipmap.ic_launcher,
                    notificationPushIntent = "com.relateddigital.androidexampleapp.PushNotificationActivity",
                    notificationChannelName = "relateddigital-android-test",
                    notificationColor = "#d1dbbd",
                    notificationPriority = RDNotificationPriority.NORMAL
                )
            })
    }

}