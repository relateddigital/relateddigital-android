package com.relateddigital.relateddigital_android.appTracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants

object AppTracker {
    private val TAG = "AppTracker"

    /**
     * This method is used to send the list of the applications installed from a store in the device to the server.
     * With Android 11, to get the list of the apps installed in the device, you have 2 options:
     * 1. You can add the package names of the applications that you are interested in into the AndroidManifest.xml file.
     * 2. You can add the permission below to the AndroidManifest.xml files:
     * <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" tools:ignore="QueryAllPackagesPermission"></uses-permission>
     *
     * For the 2nd method: Google might expect you to explain why you need this permission when you upload the app to Play Store.
     * https://developer.android.com/training/basics/intents/package-visibility
     */
    fun sendTheListOfAppsInstalled(context: Context) {
        val packageManager = context.packageManager

        @SuppressLint("QueryPermissionsNeeded")
        val appsInstalled = getInstalledApplications(packageManager)
        val appsStrBuilder = StringBuilder()

        appsInstalled.forEach { appInfo ->
            if (!isSystemApp(appInfo) && isFromStore(packageManager, appInfo)) {
                appsStrBuilder.append(appInfo.loadLabel(packageManager)).append(";")
                Log.d(TAG, "App from store: ${appInfo.packageName}")
            } else {
                Log.d(TAG, "Skipping app: ${appInfo.packageName}")
            }
        }

        if (appsStrBuilder.isNotEmpty()) {
            appsStrBuilder.deleteCharAt(appsStrBuilder.length - 1)
            val apps = appsStrBuilder.toString()
            val parameters = HashMap<String, String>()
            parameters[Constants.APP_TRACKER_REQUEST_KEY] = apps
            RelatedDigital.customEvent(context, Constants.PAGE_NAME_REQUEST_VAL, parameters)
        }
    }

    /**
     * Gets the list of installed applications.
     * @param packageManager: PackageManager
     * @return List<ApplicationInfo>
     */
    private fun getInstalledApplications(packageManager: PackageManager): List<ApplicationInfo> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
                .map { it.applicationInfo }
                .filterNotNull() // EKLENDİ - Null olabilecek değerleri listeden kaldırır
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }
    }

    /**
     * Checks if the application is a system application.
     * @param applicationInfo: ApplicationInfo
     * @return true if it is a system app, false otherwise
     */
    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    /**
     * Checks if the application is installed from a store like Play Store, Huawei App Gallery, or Amazon App Store.
     * @param packageManager: PackageManager
     * @param applicationInfo: ApplicationInfo
     * @return true if it is installed from a store, false otherwise
     */

    private fun isFromStore(packageManager: PackageManager, applicationInfo: ApplicationInfo): Boolean {
        val validInstallers = setOf("com.android.vending", "com.huawei.appmarket", "com.amazon.venezia")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API 30) and above
            try {
                val installSourceInfo = packageManager.getInstallSourceInfo(applicationInfo.packageName)
                validInstallers.contains(installSourceInfo.initiatingPackageName)
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(TAG, "Package not found: ${applicationInfo.packageName}", e)
                false
            }
        } else {
            // For Android 10 (API 29) and below
            val installerName = packageManager.getInstallerPackageName(applicationInfo.packageName)
            installerName != null && validInstallers.contains(installerName)
        }
    }
}