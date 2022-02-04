package com.relateddigital.relateddigital_android.locationPermission

import android.content.Context
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.util.AppUtils
import java.util.*

object LocationPermissionHandler {

    fun sendLocationPermissionToTheServer(context: Context) {
        val locationPermissionStr: String = when (AppUtils.getLocationPermissionStatus(context)) {
            LocationPermission.ALWAYS -> {
                Constants.LOC_PERMISSION_ALWAYS_REQUEST_VAL
            }
            LocationPermission.APP_OPEN -> {
                Constants.LOC_PERMISSION_APP_OPEN_REQUEST_VAL
            }
            else -> {
                Constants.LOC_PERMISSION_NONE_REQUEST_VAL
            }
        }

        val parameters = HashMap<String, String>()
        parameters[Constants.LOCATION_PERMISSION_REQUEST_KEY] = locationPermissionStr
        RelatedDigital.customEvent(context, Constants.PAGE_NAME_REQUEST_VAL, parameters)
    }
}