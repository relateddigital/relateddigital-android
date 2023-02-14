package com.relateddigital.relateddigital_android_core.util

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.relateddigital.relateddigital_android_core.constants.Constants
import java.text.SimpleDateFormat
import java.util.*

object PersistentTargetManager {
    private const val LOG_TAG = "PersistentTargetManager"

    /**
     * Writes customEvent parameters to shared preferences.
     *
     * @param parameters customEvent Paramaters
     */
    fun saveParameters(context: Context, parameters: HashMap<String, String>?) {
        val now = Date()
        for (visilabsParameter in Constants.VISILABS_PARAMETERS!!) {
            val key: String = visilabsParameter.key
            val storeKey: String = visilabsParameter.storeKey
            val relatedKeys: List<String>? = visilabsParameter.relatedKeys
            val count: Int = visilabsParameter.count
            if (parameters!!.containsKey(key) && !parameters[key].isNullOrEmpty()) {
                val parameterValue = parameters[key]!!.trim { it <= ' ' }
                if (count == 1) {
                    if (!relatedKeys.isNullOrEmpty()) {
                        var parameterValueToStore = parameterValue
                        val relatedKey = relatedKeys[0]
                        parameterValueToStore = if (parameters.containsKey(relatedKey) &&
                                parameters[relatedKey] != null) {
                            parameterValueToStore + "|" + parameters[relatedKey]!!.trim { it <= ' ' }
                        } else {
                            "$parameterValueToStore|0"
                        }
                        parameterValueToStore = "$parameterValueToStore|" +
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                        .format(now)
                        SharedPref.writeString(context, storeKey, parameterValueToStore)
                    } else {
                        SharedPref.writeString(context, storeKey, parameterValue)
                    }
                } else if (count > 1) {
                    val previousParameterValue: String = SharedPref.readString(context, storeKey,
                            "")
                    val parameterValueToStore = StringBuilder(
                        "$parameterValue|" +
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    .format(now))
                    if (previousParameterValue.isNotEmpty()) {
                        val previousParameterValueParts = previousParameterValue
                                .split("~").toTypedArray()
                        var paramCounter = 1
                        for (i in previousParameterValueParts.indices) {
                            if (paramCounter == 10) {
                                break
                            }
                            val decodedPreviousParameterValuePart = previousParameterValueParts[i]
                            if (decodedPreviousParameterValuePart.split("\\|").toTypedArray().size == 2) {
                                if (decodedPreviousParameterValuePart.split("\\|")
                                        .toTypedArray()[0] == parameterValue) {
                                    continue
                                }
                                parameterValueToStore.append("~").append(decodedPreviousParameterValuePart)
                                paramCounter++
                            }
                        }
                    }
                    SharedPref.writeString(context, storeKey, parameterValueToStore.toString())
                }
            }
        }
    }

    fun getParameters(context: Context): HashMap<String, String> {
        val parameters = HashMap<String, String>()
        for (visilabsParameter in Constants.VISILABS_PARAMETERS!!) {
            val storeKey: String = visilabsParameter.storeKey
            val value: String = SharedPref.readString(context, storeKey, "")
            if (value.isNotEmpty()) {
                parameters[storeKey] = value
            }
        }
        return parameters
    }

    fun clearParameters(context: Context) {
        for (visilabsParameter in Constants.VISILABS_PARAMETERS!!) {
            SharedPref.clearKey(context, visilabsParameter.storeKey)
        }
        Log.i(LOG_TAG, "Parameters cleared.")
    }

    fun saveShownStory(context: Context, actId: String, title: String) {
        val shownStories = getShownStories(context)
        if (!shownStories.containsKey(actId)) {
            shownStories[actId] = ArrayList()
        }
        if (!shownStories[actId]!!.contains(title)) {
            shownStories[actId]!!.add(title)
        }
        SharedPref.writeString(context, Constants.SHOWN_STORIES_PREF_KEY,
                GsonBuilder().create().toJson(shownStories))
    }

    fun getShownStories(context: Context): MutableMap<String, MutableList<String>> {
        var shownStories: MutableMap<String, MutableList<String>> = HashMap()
        val shownStoriesJson: String = SharedPref.readString(context,
                Constants.SHOWN_STORIES_PREF_KEY, "")
        if (shownStoriesJson.isNotEmpty()) {
            val gson = Gson()
            val shownStoriesType = object : TypeToken<Map<String?, List<String?>?>?>() {}.type
            shownStories = gson.fromJson(shownStoriesJson, shownStoriesType)
        }
        return shownStories
    }

    fun clearStoryCache(context: Context) {
        SharedPref.clearKey(context, Constants.SHOWN_STORIES_PREF_KEY)
    }
}