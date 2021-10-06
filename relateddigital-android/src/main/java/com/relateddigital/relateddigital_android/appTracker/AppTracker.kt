package com.relateddigital.relateddigital_android.appTracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import java.util.*

object AppTracker {
    /**
     * This method is used to send the list of the
     * applications installed from a store in the device to the server.
     * With Android 11, to get the list of the apps installed
     * in the device, you have 2 options:
     * 1-) You can add the package names of the applications
     * that you are interested in into the AndroidManifest.xml file
     * like below:
     * <manifest package="com.example.myApp">
     * <queries>
     * <package android:name="com.example.app1"></package>
     * <package android:name="com.example.app2"></package>
    </queries> *
     * ...
    </manifest> *
     * 2-) You can add the permission below to the
     * AndroidManifest.xml files like below:
     * <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" tools:ignore="QueryAllPackagesPermission"></uses-permission>
     *
     * For the 2nd method: Google might expect you to
     * explain why you need this permission when you upload
     * the app to Play Store.
     * https://developer.android.com/training/basics/intents/package-visibility
     */
    fun sendTheListOfAppsInstalled(context: Context) {
        val packageManager: PackageManager = context.packageManager
        @SuppressLint("QueryPermissionsNeeded") val appsInstalled = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val appsStrBuilder = StringBuilder()
        for (i in appsInstalled.indices) {
            val currentAppInfo = appsInstalled[i]
            if (isSystemApp(currentAppInfo)) {
                continue
            }
            if (!isFromStore(packageManager, currentAppInfo)) {
                continue
            }
            appsStrBuilder.append(currentAppInfo.loadLabel(packageManager)).append(";")
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
     * This method checks if the application
     * is a system application
     * @param applicationInfo : ApplicationInfo
     * @return true if it is a system app
     * false if it is not a system app
     */
    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    /**
     * This method checks if the application
     * is installed from a store like Play Store or Huawei App Gallery
     * @param packageManager : PackageManager
     * @param applicationInfo : ApplicationInfo
     * @return true if it is installed from a store
     * false if is is not installed from a store
     */
    private fun isFromStore(packageManager: PackageManager, applicationInfo: ApplicationInfo): Boolean {
        val validInstallers = ArrayList<String>()
        validInstallers.add("com.android.vending") // Play Store
        validInstallers.add("com.huawei.appmarket") // Huawei App Gallery
        validInstallers.add("com.amazon.venezia") // Amazon App Store
        val installerName = packageManager.getInstallerPackageName(applicationInfo.packageName)
        return installerName != null && validInstallers.contains(installerName)
    }
}