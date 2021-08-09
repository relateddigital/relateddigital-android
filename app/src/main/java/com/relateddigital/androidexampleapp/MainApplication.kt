package com.relateddigital.androidexampleapp

import android.app.Application
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital

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
    }
}