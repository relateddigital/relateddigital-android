package com.relateddigital.relateddigital_android.util

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.util.Log

class RelatedDigitalInstallTracker private constructor(context: Context) {
    private val appContext: Context = context.applicationContext ?: context
    private var installType: String = TYPE_FIRST_INSTALL
    private val lock = Any()

    init {
        evaluateInstallStatus()
    }

    private fun evaluateInstallStatus() {
        synchronized(lock) {
            val prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val hasInstalled = prefs.getBoolean(KEY_INSTALLED, false)

            if (hasInstalled) {
                installType = prefs.getString(KEY_INSTALL_TYPE, TYPE_FIRST_INSTALL) ?: TYPE_FIRST_INSTALL
                return
            }

            var isReinstall = false
            try {
                val pInfo: PackageInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
                if (pInfo.firstInstallTime != pInfo.lastUpdateTime) {
                    isReinstall = true
                }
            } catch (e: Exception) {
                Log.w(LOG_TAG, "PackageInfo error: ${e.message}")
            }

            installType = if (isReinstall) TYPE_REINSTALL else TYPE_FIRST_INSTALL
            prefs.edit()
                .putBoolean(KEY_INSTALLED, true)
                .putString(KEY_INSTALL_TYPE, installType)
                .putLong(KEY_FIRST_INSTALL_TIME, System.currentTimeMillis())
                .apply()

            Log.i(LOG_TAG, "Install status evaluated: $installType")
        }
    }

    val isFirstInstall: Boolean
        get() = synchronized(lock) { TYPE_FIRST_INSTALL == installType }

    val isReinstall: Boolean
        get() = synchronized(lock) { TYPE_REINSTALL == installType }

    fun isFirstInstallValue(tvc: Int): String = synchronized(lock) {
        if (TYPE_FIRST_INSTALL == installType && tvc <= 1) "1" else "0"
    }

    fun isReinstallValue(tvc: Int): String = synchronized(lock) {
        if (TYPE_REINSTALL == installType && tvc <= 1) "1" else "0"
    }

    fun getInstallType(): String = synchronized(lock) { installType }

    fun resetForTesting() {
        synchronized(lock) {
            val prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            installType = TYPE_FIRST_INSTALL
        }
    }

    companion object {
        private const val LOG_TAG = "RDInstallTracker"
        private const val PREF_NAME = "RelatedDigitalInstallPreferences"
        private const val KEY_INSTALLED = "RelatedDigital.installed"
        private const val KEY_INSTALL_TYPE = "RelatedDigital.installType"
        private const val KEY_FIRST_INSTALL_TIME = "RelatedDigital.firstInstallTime"

        const val TYPE_FIRST_INSTALL = "first_install"
        const val TYPE_REINSTALL = "reinstall"

        @Volatile
        private var instance: RelatedDigitalInstallTracker? = null

        @JvmStatic
        fun getInstance(context: Context? = null): RelatedDigitalInstallTracker? {
            if (instance == null && context != null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = RelatedDigitalInstallTracker(context)
                    }
                }
            }
            return instance
        }

        @JvmStatic
        fun isExistingInstall(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val hasInstalled = prefs.getBoolean(KEY_INSTALLED, false)
            if (hasInstalled) {
                return true
            }
            try {
                val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                if (pInfo.firstInstallTime != pInfo.lastUpdateTime) {
                    return true
                }
            } catch (e: Exception) {
                Log.w(LOG_TAG, "PackageInfo error: ${e.message}")
            }
            return false
        }
    }
}
