package com.relateddigital.relateddigital_android.util

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Message
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object PayloadUtils {
    private const val LOG_TAG = "PayloadUtils"
    private const val DATE_THRESHOLD: Long = 30
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun addPushMessage(context: Context, message: Message) {
        val payloads: String = SharedPref.readString(context, Constants.PAYLOAD_SP_KEY)
        if (payloads.isNotEmpty()) {
            try {
                val jsonObject = JSONObject(payloads)
                var jsonArray = jsonObject.optJSONArray(Constants.PAYLOAD_SP_ARRAY_KEY)
                if (isPushIdAvailable(context, jsonArray, message)) {
                    return
                }
                jsonArray = addNewOne(context, jsonArray, message)
                if (jsonArray == null) {
                    return
                }
                jsonArray = removeOldOnes(context, jsonArray)
                val finalObject = JSONObject()
                finalObject.put(Constants.PAYLOAD_SP_ARRAY_KEY, jsonArray)
                SharedPref.writeString(
                    context,
                    Constants.PAYLOAD_SP_KEY,
                    finalObject.toString()
                )
            } catch (e: Exception) {
                val element = Throwable().stackTrace[0]
                LogUtils.formGraylogModel(
                    context,
                    "e",
                    "Serializing push message : " + e.message,
                    element.className + "/" + element.methodName + "/" + element.lineNumber
                )
                Log.e(
                    LOG_TAG,
                    "Something went wrong when adding the push message to shared preferences!"
                )
                Log.e(LOG_TAG, e.message!!)
            }
        } else {
            createAndSaveNewOne(context, message)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun addPushMessageWithId(context: Context, message: Message, loginID: String) {
        val payloads: String = SharedPref.readString(context, Constants.PAYLOAD_SP_ID_KEY)
        if (payloads.isNotEmpty()) {
            try {
                val jsonObject = JSONObject(payloads)
                var jsonArray = jsonObject.optJSONArray(Constants.PAYLOAD_SP_ARRAY_ID_KEY)
                if (isPushIdAvailable(context, jsonArray, message)) {
                    return
                }
                jsonArray = addNewOneWithID(context, jsonArray, message, loginID)
                if (jsonArray == null) {
                    return
                }
                jsonArray = removeOldOnes(context, jsonArray)
                val finalObject = JSONObject()
                finalObject.put(Constants.PAYLOAD_SP_ARRAY_ID_KEY, jsonArray)
                SharedPref.writeString(
                    context,
                    Constants.PAYLOAD_SP_ID_KEY,
                    finalObject.toString()
                )
            } catch (e: Exception) {
                val element = Throwable().stackTrace[0]
                LogUtils.formGraylogModel(
                    context,
                    "e",
                    "Serializing push message : " + e.message,
                    element.className + "/" + element.methodName + "/" + element.lineNumber
                )
                Log.e(
                    LOG_TAG,
                    "Something went wrong when adding the push message to shared preferences!"
                )
                Log.e(LOG_TAG, e.message!!)
            }
        } else {
            createAndSaveNewOneWithID(context, message, loginID)
        }
    }

    fun orderPushMessages(context: Context, messages: MutableList<Message>): List<Message> {
        for (i in messages.indices) {
            for (j in 0 until messages.size - 1 - i) {
                if (compareDates(context, messages[j].date!!, messages[j + 1].date!!)) {
                    val temp: Message = messages[j]
                    messages[j] = messages[j + 1]
                    messages[j + 1] = temp
                }
            }
        }
        return messages
    }

    fun sendUtmParametersEvent(context: Context, message: Message) {
        val params = message.getParams()
        val properties = HashMap<String, String>()

        if(params.isNotEmpty()) {
            for (param in params.entries) {
                if (param.key == Constants.UTM_SOURCE || param.key == Constants.UTM_MEDIUM ||
                    param.key == Constants.UTM_CAMPAIGN || param.key == Constants.UTM_CONTENT ||
                    param.key == Constants.UTM_TERM
                ) {
                    properties[param.key] = param.value
                }
            }

            if (properties.isNotEmpty()) {
                RelatedDigital.sendCampaignParameters(context, properties)
            }
        }
    }

    private fun isPushIdAvailable(
        context: Context,
        jsonArray: JSONArray?,
        message: Message
    ): Boolean {
        var res = false
        for (i in 0 until jsonArray!!.length()) {
            try {
                if (jsonArray.getJSONObject(i).has("pushId")&&
                    jsonArray.getJSONObject(i).getString("pushId") == message.pushId) {
                    res = true
                    break
                }
            } catch (e: Exception) {
                val element = Throwable().stackTrace[0]
                LogUtils.formGraylogModel(
                    context,
                    "e",
                    "Getting pushId from JSONArray : " + e.message,
                    element.className + "/" + element.methodName + "/" + element.lineNumber
                )
                Log.e(LOG_TAG, e.message!!)
            }
        }
        return res
    }

    private fun addNewOne(context: Context, jsonArray: JSONArray?, message: Message): JSONArray? {
        return try {
            message.date =
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())
            jsonArray!!.put(JSONObject(Gson().toJson(message)))
            jsonArray
        } catch (e: Exception) {
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                context,
                "e",
                "Serializing push message : " + e.message,
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            Log.e(LOG_TAG, "Could not save the push message!")
            Log.e(LOG_TAG, e.message!!)
            null
        }
    }

    private fun addNewOneWithID(context: Context, jsonArray: JSONArray?, message: Message,
    loginID: String): JSONArray? {
        return try {
            message.loginID = loginID
            message.date =
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())
            jsonArray!!.put(JSONObject(Gson().toJson(message)))
            jsonArray
        } catch (e: Exception) {
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                context,
                "e",
                "Serializing push message : " + e.message,
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            Log.e(LOG_TAG, "Could not save the push message!")
            Log.e(LOG_TAG, e.message!!)
            null
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun removeOldOnes(context: Context, jsonArray: JSONArray): JSONArray {
        var i = 0
        while (i < jsonArray.length()) {
            try {
                val jsonObject = jsonArray.getJSONObject(i)
                if (!jsonObject.has("date") || ((jsonObject.has("date") && isOld(context, jsonObject.getString("date"))))) {
                    jsonArray.remove(i)
                    i--
                }
                if (!jsonObject.has("pushId")) {
                    jsonArray.remove(i)
                    i--
                }
            } catch (e: Exception) {
                val element = Throwable().stackTrace[0]
                LogUtils.formGraylogModel(
                    context,
                    "e",
                    "Removing push message from JSONArray : " + e.message,
                    element.className + "/" + element.methodName + "/" + element.lineNumber
                )
                Log.e(LOG_TAG, e.message!!)
            }
            i++
        }
        return jsonArray
    }

    private fun isOld(context: Context, date: String): Boolean {
        var res = false
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        try {
            val messageDate = dateFormat.parse(date)
            val now = Date()
            val difference = now.time - messageDate!!.time
            if (difference / (1000 * 60 * 60 * 24) > DATE_THRESHOLD) { //30 days
                res = true
            }
        } catch (e: Exception) {
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                context,
                "e",
                "Comparing 2 dates : " + e.message,
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            Log.e(LOG_TAG, "Could not parse date!")
            Log.e(LOG_TAG, e.message!!)
        }
        return res
    }

    private fun createAndSaveNewOne(context: Context, message: Message) {
        try {
            val jsonObject = JSONObject()
            val jsonArray = JSONArray()
            message.date =
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())
            jsonArray.put(JSONObject(Gson().toJson(message)))
            jsonObject.put(Constants.PAYLOAD_SP_ARRAY_KEY, jsonArray)
            SharedPref.writeString(context, Constants.PAYLOAD_SP_KEY, jsonObject.toString())
        } catch (e: Exception) {
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                context,
                "e",
                "Forming and serializing push message string : " + e.message,
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            Log.e(LOG_TAG, "Could not save the push message!")
            Log.e(LOG_TAG, e.message!!)
        }
    }

    private fun createAndSaveNewOneWithID(context: Context, message: Message, loginID: String) {
        try {
            val jsonObject = JSONObject()
            val jsonArray = JSONArray()
            message.loginID = loginID
            message.date =
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())
            jsonArray.put(JSONObject(Gson().toJson(message)))
            jsonObject.put(Constants.PAYLOAD_SP_ARRAY_ID_KEY, jsonArray)
            SharedPref.writeString(context, Constants.PAYLOAD_SP_ID_KEY, jsonObject.toString())
        } catch (e: Exception) {
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                context,
                "e",
                "Forming and serializing push message string : " + e.message,
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            Log.e(LOG_TAG, "Could not save the push message!")
            Log.e(LOG_TAG, e.message!!)
        }
    }

    private fun compareDates(context: Context, str1: String, str2: String): Boolean {
        var res = false
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        try {
            val date1 = dateFormat.parse(str1)
            val date2 = dateFormat.parse(str2)
            if (date1!!.time - date2!!.time < 0) {
                res = true
            }
        } catch (e: Exception) {
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                context,
                "e",
                "Comparing 2 dates : " + e.message,
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            Log.e(LOG_TAG, "Could not parse date!")
            Log.e(LOG_TAG, e.message!!)
        }
        return res
    }
}