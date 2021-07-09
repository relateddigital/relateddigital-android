package com.relateddigital.androidexampleapp

import android.app.Application
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RelatedDigital.init(
                context = applicationContext,
                isPushNotificationEnabled = true,
                isInAppNotificationEnabled = true,
                isGeofenceEnabled = true,
                googleAppAlias = "visilabs-android-test",
                huaweiAppAlias = "visilabs-android-test",
                organizationId = "676D325830564761676D453D",
                profileId = "356467332F6533766975593D",
                dataSource = "visistore",
                requestTimeoutInSecond = 30,
                maxGeofenceCount = 100)
    }
}