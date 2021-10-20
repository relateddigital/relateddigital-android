package com.relateddigital.relateddigital_android.recommendation

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

object RecommendationUtils {
    const val LOG_TAG = "RecommendationUtils"
    fun formJsonObject(rawResponse: String): JSONObject {
        val initialArray = JSONArray(rawResponse)
        val finalArray = JSONArray()
        val finalObject = JSONObject()
        for (i in 0 until initialArray.length()) {
            val currentObject: JSONObject = initialArray.getJSONObject(i)
            currentObject.put("qs", getQueryString(currentObject.optString("dest_url")))
            finalArray.put(currentObject)
            if (i == 0) {
                try {
                    finalObject.put("title", currentObject.getString("wdt"))
                } catch (e: Exception) {
                    finalObject.put("title", "")
                }
            }
        }
        finalObject.put("recommendations", finalArray)
        return finalObject
    }

    fun formJsonArray(rawResponse: String): JSONArray {
        val initialArray = JSONArray(rawResponse)
        val finalArray = JSONArray()
        for (i in 0 until initialArray.length()) {
            val currentObject: JSONObject = initialArray.getJSONObject(i)
            currentObject.put("qs", getQueryString(currentObject.getString("dest_url")))
            finalArray.put(currentObject)
        }
        return finalArray
    }

    private fun getQueryString(destUrl: String?): String {
        val sb = StringBuilder()
        return if (destUrl != null && destUrl != "") {
            try {
                val tempQueryParameter = destUrl.split("\\?".toRegex(), 2).toTypedArray()
                val tempMultiQuery = tempQueryParameter[1].split("&".toRegex()).toTypedArray()
                for (s in tempMultiQuery) {
                    val tempQueryString = s.split("=".toRegex(), 2).toTypedArray()
                    if (tempQueryString.size == 2) {
                        if (tempQueryString[0] == "OM.zn" || tempQueryString[0] == "OM.zpc") {
                            sb.append(s).append("&")
                        }
                    }
                }
                sb.deleteCharAt(sb.length - 1)
                sb.toString()
            } catch (e: java.lang.Exception) {
                Log.w(LOG_TAG, "Could not parse dest url!")
                ""
            }
        } else {
            ""
        }
    }
}