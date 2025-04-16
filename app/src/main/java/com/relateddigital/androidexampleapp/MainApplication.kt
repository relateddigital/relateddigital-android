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
import com.relateddigital.relateddigital_android.constants.Constants as SdkConstants


class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initSdk(isTest = false)

        // Enable In-App Notifications
        RelatedDigital.setIsInAppNotificationEnabled(
            context = applicationContext,
            isInAppNotificationEnabled = true
        )

        // Enable Push Notifications
        if (GoogleUtils.checkPlayService(this)) {
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

    private fun getFirebaseToken() {
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
                    notificationSmallIcon = R.drawable.relateddigital_ic,
                    notificationSmallIconDarkMode = R.drawable.relateddigital_ic,
                    isNotificationLargeIcon = true,
                    notificationLargeIcon = R.drawable.relateddigital_ic,
                    notificationLargeIconDarkMode = R.drawable.relateddigital_ic,
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
                        notificationSmallIcon = R.drawable.relateddigital_ic,
                        notificationSmallIconDarkMode = R.drawable.relateddigital_ic,
                        isNotificationLargeIcon = true,
                        notificationLargeIcon = R.drawable.relateddigital_ic,
                        notificationLargeIconDarkMode = R.drawable.relateddigital_ic,
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

    private fun initSdk(isTest: Boolean) {
        var organizationId = "676D325830564761676D453D"
        var profileId = "356467332F6533766975593D"
        var dataSource = "visistore"

        if (isTest) {
            organizationId = "394A48556A2F76466136733D"
            profileId = "75763259366A3345686E303D"
            dataSource = "mrhp"
            SdkConstants.ACTION_ENDPOINT = "http://tests.visilabs.net/"

            // ="@xml/network_security_config" make true
        }

        // Initialize RelatedDigital with mandatory information
        RelatedDigital.init(
            context = applicationContext,
            organizationId = organizationId,
            profileId = profileId,
            dataSource = dataSource
        )
    }


}