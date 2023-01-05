package com.relateddigital.androidexampleapp

import android.app.Application
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.huawei.agconnect.AGConnectOptionsBuilder
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.model.RDNotificationPriority
import com.relateddigital.relateddigital_android.util.GoogleUtils

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize RelatedDigital with mandatory information
        RelatedDigital.init(
            context = applicationContext,
            organizationId = "676D325830564761676D453D",
            profileId = "356467332F6533766975593D",
            dataSource = "visistore")

        /* dsource = mrhp
         oid = 394A48556A2F76466136733D
                 pid = 75763259366A3345686E303D

                 organizationId = "676D325830564761676D453D",
         profileId = "356467332F6533766975593D",
         dataSource = "visistore") */

        // Enable In-App Notifications
        RelatedDigital.setIsInAppNotificationEnabled(
            context = applicationContext,
            isInAppNotificationEnabled = true
        )

        // Enable Push Notifications
        if(GoogleUtils.checkPlayService(this)) {
            getFirebaseToken()
        } else {
            getHuaweiToken()
        }

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

    private fun getHuaweiToken() {
        object : Thread() {
            override fun run() {
                try {
                    val appId = AGConnectOptionsBuilder().build(applicationContext)
                        .getString("client/app_id")
                    val token = HmsInstanceId.getInstance(applicationContext).getToken(appId, "HCM")
                    if (TextUtils.isEmpty(token) || token == null) {
                        Log.e("Huawei Token : ", "Empty token!!!")
                        return
                    }
                    Log.i("Huawei Token", "" + token)

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
                } catch (e: ApiException) {
                    Log.e("Huawei Token", "Getting the token failed! $e")
                }
            }
        }.start()
    }
}