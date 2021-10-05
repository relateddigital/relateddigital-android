package com.relateddigital.relateddigital_android.inapp

import org.json.JSONArray
import org.json.JSONObject

/**
 * Represents the response data from Visilabs when an API call completes.
 */
class VisilabsResponse(pResults: JSONObject?, pResultArray: JSONArray?, val resultOtherThanJSON: String?, e: Throwable?, pError: String?) {
    private val mResults: JSONObject? = pResults
    private val mResultArray: JSONArray? = pResultArray
    val error: Throwable? = e
    private val errorMessage: String? = pError
    val json: JSONObject?
        get() = mResults
    val array: JSONArray?
        get() = mResultArray
    val rawResponse: String
        get() = (mResults?.toString()
                ?: (mResultArray?.toString()
                        ?: if (resultOtherThanJSON != null && resultOtherThanJSON != "") {
                            resultOtherThanJSON
                        } else {
                            errorMessage
                        })) as String

}