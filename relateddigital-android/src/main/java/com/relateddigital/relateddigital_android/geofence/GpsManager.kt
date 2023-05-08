package com.relateddigital.relateddigital_android.geofence

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.model.GeoFenceEntity
import com.relateddigital.relateddigital_android.model.GeofenceListResponse
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.GeofenceGetListResponseRequest
import com.relateddigital.relateddigital_android.util.GeoFencesUtils
import java.util.*

class GpsManager(context: Context) {
    companion object {
        private const val TAG = "Related_GpsManager"
    }
    val mActiveGeoFenceEntityList: MutableList<GeoFenceEntity> =
        ArrayList<GeoFenceEntity>()
    private val mAllGeoFenceEntityList: MutableList<GeoFenceEntity> =
        ArrayList<GeoFenceEntity>()
    private val mToAddGeoFenceEntityList: MutableList<GeoFenceEntity> =
        ArrayList<GeoFenceEntity>()
    private val mToRemoveGeoFenceEntityList: MutableList<GeoFenceEntity> =
        ArrayList<GeoFenceEntity>()
    private val mApplication: Context
    private var mIsManagerActive = false
    private var mIsManagerStarting = false
    private var mLastKnownLocation: Location? = null
    private var mFirstServerCheck = false
    private var mLastServerCheck = Calendar.getInstance()
    private var mGeofencingClient: GeofencingClient? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mGeofencePendingIntent: PendingIntent? = null
    private var mLocationCallback: LocationCallback? = null
    fun start() {
        if (mIsManagerActive || mIsManagerStarting) return
        mIsManagerStarting = true
        initGpsService()
        startGpsService()
        GeofenceAlarm.singleton.setAlarmCheckIn(mApplication)
    }

    private fun initGpsService() {
        mGeofencingClient = LocationServices.getGeofencingClient(mApplication)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mApplication)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location: Location? in locationResult.locations) {
                    if (location != null) {
                        lastKnownLocation = location
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startGpsService() {
        val accessFineLocationPermission = ContextCompat.checkSelfPermission(
            mApplication,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val accessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            mApplication,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (accessFineLocationPermission || accessCoarseLocationPermission) {
            mFusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lastKnownLocation = location
                } else {
                    val locationRequest = LocationRequest.create()
                    locationRequest.priority =
                        LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                    locationRequest.interval = 10000
                    mFusedLocationClient!!.requestLocationUpdates(
                        locationRequest,
                        mLocationCallback!!,
                        Looper.getMainLooper()
                    )
                }
            }
        }
    }

    private fun areGeoFenceEntitiesAreTheSame(
        geoFenceEntity1: GeoFenceEntity,
        geoFenceEntity2: GeoFenceEntity
    ): Boolean {
        if (!geoFenceEntity1.latitude.equals(geoFenceEntity2.latitude)) return false
        if (!geoFenceEntity1.longitude.equals(geoFenceEntity2.longitude)) return false
        if (geoFenceEntity1.radius != geoFenceEntity2.radius) return false
        return if (!geoFenceEntity1.name
                .equals(geoFenceEntity2.name)
        ) false else geoFenceEntity1.type.equals(geoFenceEntity2.type)
    }

    private fun setupGeofences() {
        val callback: GeofenceGetListCallback = object : GeofenceGetListCallback {
            override fun success(response: List<GeofenceListResponse?>?, url: String) {
                Log.i(TAG, "Success Request : $url")
                val geoFences: MutableList<GeoFenceEntity> =
                    ArrayList<GeoFenceEntity>()
                if (!response.isNullOrEmpty()) {
                    for (i in response.indices) {
                        val geofenceListResponse: GeofenceListResponse =
                            response[i]!!
                        for (j in geofenceListResponse.geofences!!.indices) {
                            val visilabsGeoFenceEntity = GeoFenceEntity()
                            visilabsGeoFenceEntity.guid =
                                geofenceListResponse.actId.toString() +
                                        "_" + j + "_" + geofenceListResponse.geofences!![j].id

                            visilabsGeoFenceEntity.latitude =
                                geofenceListResponse.geofences!![j]
                                    .latitude.toString()

                            visilabsGeoFenceEntity.longitude =
                                geofenceListResponse.geofences!![j]
                                    .longitude.toString()

                            visilabsGeoFenceEntity.radius =
                                geofenceListResponse.geofences!![j].radius!!
                                    .toInt()

                            visilabsGeoFenceEntity.type = geofenceListResponse.trgevt
                            visilabsGeoFenceEntity.durationInSeconds =
                                geofenceListResponse.distance!!
                            visilabsGeoFenceEntity.geoid =
                                geofenceListResponse.geofences!![j].id!!
                            geoFences.add(visilabsGeoFenceEntity)
                        }
                    }
                    setupGeofencesCallback(geoFences)
                }
            }

            override fun fail(t: Throwable, url: String) {
                Log.e(TAG, "Fail Request : $url")
                Log.e(TAG, "Fail Request Message : " + t.message)
            }
        }
            GeofenceGetListResponseRequest.createGeofenceGetListResponseRequest(mApplication,
            mLastKnownLocation!!.latitude, mLastKnownLocation!!.longitude, callback
        )
    }

    private fun setupGeofencesCallback(geoFences: List<GeoFenceEntity>?) {
        if (geoFences == null) {
            return
        }
        mAllGeoFenceEntityList.clear()
        mAllGeoFenceEntityList.addAll(geoFences)
        val lat1 = mLastKnownLocation!!.latitude
        val long1 = mLastKnownLocation!!.longitude
        for (entity: GeoFenceEntity in mAllGeoFenceEntityList) {
            entity.distance =
                GeoFencesUtils.haversine(
                    lat1,
                    long1,
                    entity.latitude!!.toDouble(),
                    entity.longitude!!.toDouble()
                )
             //difference btw two points
        }
        Collections.sort(mAllGeoFenceEntityList, DistanceComparator())
        mToAddGeoFenceEntityList.clear()
        mToRemoveGeoFenceEntityList.clear()
        if (mActiveGeoFenceEntityList.isNotEmpty()) {
            if (mGeofencingClient != null) removeGeofences(mActiveGeoFenceEntityList)
            mActiveGeoFenceEntityList.clear()
        }
        if (mActiveGeoFenceEntityList.isEmpty()) {
            if (mAllGeoFenceEntityList.size > 100) {
                mToAddGeoFenceEntityList.addAll(mAllGeoFenceEntityList.subList(0, 100))
            } else {
                mToAddGeoFenceEntityList.addAll(mAllGeoFenceEntityList)
            }
        }
        if (mGeofencingClient == null) return
        if (mToRemoveGeoFenceEntityList.isNotEmpty()) {
            removeGeofences(mToRemoveGeoFenceEntityList)
            val it: MutableIterator<GeoFenceEntity> = mActiveGeoFenceEntityList.iterator()
            while (it.hasNext()) {
                val geofence: GeoFenceEntity = it.next()
                for (entityToRemove: GeoFenceEntity in mToRemoveGeoFenceEntityList) {
                    if (areGeoFenceEntitiesAreTheSame(geofence, entityToRemove)) {
                        it.remove()
                    }
                }
            }
        }
        if (mToAddGeoFenceEntityList.isNotEmpty()) {
            addGeofences(mToAddGeoFenceEntityList)
            mActiveGeoFenceEntityList.addAll(mToAddGeoFenceEntityList)
        }
    }

    private fun removeGeofences(geoFencesToRemove: List<GeoFenceEntity>) {
        val idsToRemove: MutableList<String> = ArrayList()
        for (geofenceEntity: GeoFenceEntity in geoFencesToRemove) {
            idsToRemove.add(geofenceEntity.guid!!)
        }
        if (idsToRemove.isEmpty()) return
        mGeofencingClient!!.removeGeofences(idsToRemove).addOnSuccessListener {
            Log.v(
                TAG,
                "Removing geofences success "
            )
        }
            .addOnFailureListener { e ->
                Log.e(
                    TAG,
                    "Removing geofence failed: " + e.message,
                    e
                )
            }
    }

    private fun getAddGeofencingRequest(geofences: List<Geofence>): GeofencingRequest {
        val builder = GeofencingRequest.Builder()
        builder.addGeofences(geofences)
        return builder.build()
    }

    @SuppressLint("MissingPermission")
    private fun addGeofences(geoFencesToAdd: List<GeoFenceEntity>) {
        val geofences: MutableList<Geofence> = ArrayList()
        for (geoFenceEntity: GeoFenceEntity in geoFencesToAdd) {
            val newGf: Geofence = geoFenceEntity.toGeofence()
            geofences.add(newGf)
        }
        val accessFineLocationPermission = ContextCompat.checkSelfPermission(
            mApplication,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val accessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            mApplication,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (accessFineLocationPermission || accessCoarseLocationPermission) {
            mGeofencingClient!!.addGeofences(
                getAddGeofencingRequest(geofences),
                geofencePendingIntent!!
            )
                .addOnSuccessListener { Log.v(TAG, "Registering geofence success ") }
                .addOnFailureListener { e ->
                    Log.e(
                        TAG,
                        "Registering geofence failed: " + e.message,
                        e
                    )
                }
        }
    }

    private val geofencePendingIntent: PendingIntent?
        get() {
            if (mGeofencePendingIntent != null) {
                return mGeofencePendingIntent
            }
            val intent = Intent(mApplication, GeofenceBroadcastReceiver::class.java)
            mGeofencePendingIntent = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                PendingIntent.getBroadcast(
                    mApplication,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    mApplication,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            return mGeofencePendingIntent
        }

    private class DistanceComparator() :
        Comparator<GeoFenceEntity> {
        override fun compare(
            object1: GeoFenceEntity,
            object2: GeoFenceEntity
        ): Int {
            val position1: Double = object1.distance
            val position2: Double = object2.distance
            return position1.compareTo(position2)
        }
    }

    // current date/time
    var lastKnownLocation: Location?
        get() = mLastKnownLocation
        set(location) {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
            val minutesBefore = Calendar.getInstance() // current date/time
            val amount = RelatedDigital.getRelatedDigitalModel(mApplication).getGeofencingIntervalInMinute() * -1
            minutesBefore.add(Calendar.MINUTE, amount)
            if (mLastKnownLocation == null && location == null) return
            if (mLastKnownLocation == null) {
                mLastKnownLocation = location
            } else {
                if (location != null) {
                    val lat1 = mLastKnownLocation!!.latitude
                    val long1 = mLastKnownLocation!!.longitude
                    val lat2 = location.latitude
                    val long2 = location.longitude
                    if (GeoFencesUtils.haversine(lat1, long1, lat2, long2) > 1) {
                        mLastKnownLocation = location
                    }
                }
            }
            if (!mFirstServerCheck || mLastServerCheck.before(minutesBefore)) {
                setupGeofences()
                mLastServerCheck = Calendar.getInstance()
                mFirstServerCheck = true
            }
        }

    init {
        Injector.INSTANCE.initGpsManager(this)
        mApplication = context
    }
}