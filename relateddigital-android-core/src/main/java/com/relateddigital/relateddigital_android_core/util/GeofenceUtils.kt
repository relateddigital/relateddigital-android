package com.relateddigital.relateddigital_android_core.util

import kotlin.math.*


object GeoFencesUtils {
    private const val R = 6372.8 // In kilometers

    /**
     * Calculates the distance between two points
     * @param lat1 the latitude of the first point
     * @param lon1 the longitude of the first point
     * @param lat2 the latitude of the second point
     * @param lon2 the longitude of the second point
     * @return double - the distance in km
     */
    fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        var locLat1 = lat1
        var locLat2 = lat2
        val dLat = Math.toRadians(locLat2 - locLat1)
        val dLon = Math.toRadians(lon2 - lon1)
        locLat1 = Math.toRadians(locLat1)
        locLat2 = Math.toRadians(locLat2)
        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(locLat1) * cos(locLat2)
        val c = 2 * asin(sqrt(a))
        return R * c
    }
}