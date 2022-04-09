package com.relateddigital.relateddigital_android.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.geofence.GeofenceStarter
import com.relateddigital.relateddigital_android.locationPermission.LocationPermission

class PermissionActivity : Activity() {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 20
        private const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 21
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION permission has been already granted
        val accessFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val accessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!(accessFineLocationPermission || accessCoarseLocationPermission)) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Check if the ACCESS_BACKGROUND_LOCATION has been already granted
            val locationPermission: LocationPermission = AppUtils.getLocationPermissionStatus(this)
            if (locationPermission != LocationPermission.ALWAYS) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                grantResults.size > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {

                // Check if the ACCESS_BACKGROUND_LOCATION has been already granted
                val locationPermission: LocationPermission =
                    AppUtils.getLocationPermissionStatus(this)
                if (locationPermission != LocationPermission.ALWAYS) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                        )
                    }
                }
            } else {
                finish()
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finish()
                if(RelatedDigital.getIsGeofenceEnabled(this)) {
                    GeofenceStarter.startGpsManager(this)
                }
            } else {
                finish()
            }
        }
    }
}