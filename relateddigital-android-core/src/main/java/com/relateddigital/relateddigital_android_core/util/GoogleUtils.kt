package com.relateddigital.relateddigital_android_core.util

import android.content.Context
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object GoogleUtils {
    private const val LOG_TAG = "Related Digital"
    fun checkPlayService(context: Context?): Boolean {
        var result = true
        when (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context!!)) {
            ConnectionResult.API_UNAVAILABLE -> {
                Log.e(LOG_TAG, "Google API Unavailable")
                result = false
            }
            ConnectionResult.NETWORK_ERROR -> {
                Log.e(LOG_TAG, "Google Network Error")
                result = false
            }
            ConnectionResult.RESTRICTED_PROFILE -> {
                Log.e(LOG_TAG, "Google Restricted")
                result = false
            }
            ConnectionResult.SERVICE_MISSING -> {
                //service is missing
                result = false
                Log.e(LOG_TAG, "Google Service is missing")
            }
            ConnectionResult.SIGN_IN_REQUIRED -> {
                //service available but user not signed in
                Log.e(LOG_TAG, "Google Sign in req")
                result = false
            }
            ConnectionResult.SERVICE_INVALID -> {
                Log.e(LOG_TAG, "Google Services invalid")
                result = false
            }
            ConnectionResult.SUCCESS -> {
                result = true
                Log.i(LOG_TAG, "Google Service is enable")
            }
        }
        return result
    }
}