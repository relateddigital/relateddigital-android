package com.relateddigital.relateddigital_android_core.inapp

/**
 * Represents the response data from Visilabs when an API call completes.
 */
interface VisilabsCallback {
    /**
     * Will be run if the target call is successful
     *
     * @param response the response data
     */
    fun success(response: VisilabsResponse?)

    /**
     * Will be run if the target call is failed
     *
     * @param response the response data
     */
    fun fail(response: VisilabsResponse?)
}