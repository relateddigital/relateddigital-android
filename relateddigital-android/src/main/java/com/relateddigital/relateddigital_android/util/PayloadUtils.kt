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
        Log.d(LOG_TAG, "addPushMessage işlemi başlatıldı. Push ID: ${message.pushId}")
        val payloads: String = SharedPref.readString(context, Constants.PAYLOAD_SP_KEY)

        if (payloads.isNotEmpty()) {
            Log.d(LOG_TAG, "Mevcut bir payload bulundu, içerik işlenecek.")
            try {
                var jsonArray: JSONArray?

                if (payloads.trim().startsWith("[")) {
                    Log.w(LOG_TAG, "Payload doğrudan bir JSONArray olarak algılandı. Veri uyumluluk modunda işleniyor.")
                    jsonArray = JSONArray(payloads)
                } else {

                    Log.d(LOG_TAG, "Payload bir JSONObject olarak algılandı.")
                    val jsonObject = JSONObject(payloads)
                    jsonArray = jsonObject.optJSONArray(Constants.PAYLOAD_SP_ARRAY_KEY)
                }

                if (jsonArray == null) {
                    Log.w(LOG_TAG, "Payload içinden mesaj dizisi alınamadı. Yeni bir dizi oluşturuluyor.")
                    jsonArray = JSONArray()
                }

                if (isPushIdAvailable(context, jsonArray, message)) {
                    Log.w(LOG_TAG, "Bu Push ID (${message.pushId}) zaten kayıtlı. İşlem durduruldu.")
                    return
                }

                jsonArray = addNewOne(context, jsonArray, message)
                if (jsonArray == null) {
                    Log.e(LOG_TAG, "addNewOne fonksiyonu null döndürdü. Yeni mesaj eklenemedi. Push ID: ${message.pushId}")
                    return
                }
                Log.d(LOG_TAG, "Yeni mesaj başarıyla eklendi.")

                jsonArray = removeOldOnes(context, jsonArray)
                Log.d(LOG_TAG, "Eski mesajlar temizlendi.")


                val finalObject = JSONObject()
                finalObject.put(Constants.PAYLOAD_SP_ARRAY_KEY, jsonArray)
                val finalPayloadString = finalObject.toString()

                SharedPref.writeStringPayload(
                    context,
                    Constants.PAYLOAD_SP_KEY,
                    finalPayloadString
                )

                Log.i(LOG_TAG, "Push mesajı başarıyla kaydedildi. Push ID: ${message.pushId}. Veri doğru formatta güncellendi.")

            } catch (e: Exception) {
                Log.e(LOG_TAG, "Push mesajı SharedPreferences'e kaydedilirken KRİTİK BİR HATA oluştu!", e)
                Log.e(LOG_TAG, "Hata Detayları - Push ID: ${message.pushId}")
                Log.e(LOG_TAG, "Hata Tipi: ${e.javaClass.simpleName}")
                Log.e(LOG_TAG, "Hata Mesajı: ${e.message}")
            }
        } else {
            Log.d(LOG_TAG, "Mevcut payload bulunamadı. Bu ilk kayıt, yeni bir payload oluşturuluyor.")
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
                SharedPref.writeStringPayload(
                    context,
                    Constants.PAYLOAD_SP_ID_KEY,
                    finalObject.toString()
                )
            } catch (e: Exception) {
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
            message.status = "D"
            message.openDate = ""
            val userExVid: Map<String, Any?> =
                RelatedDigital.getRelatedDigitalModel(context).getExtra()
            if (userExVid.containsKey(Constants.RELATED_DIGITAL_USER_KEY) && userExVid[Constants.RELATED_DIGITAL_USER_KEY] != null) {
                val keyID = userExVid[Constants.RELATED_DIGITAL_USER_KEY]
                if (keyID is String) {
                    message.keyID = keyID.toString()
                }
            }
            if (userExVid.containsKey(Constants.EMAIL_KEY) && userExVid[Constants.EMAIL_KEY] != null) {
                val email = userExVid[Constants.EMAIL_KEY]
                if (email is String) {
                    message.email= email.toString()
                }
            }
            jsonArray!!.put(JSONObject(Gson().toJson(message)))
            jsonArray
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error saving push messages: ${e.message}", e)
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
            message.status = "D"
            message.openDate = ""
            val userExVid: Map<String, Any?> =
                RelatedDigital.getRelatedDigitalModel(context).getExtra()
            if (userExVid.containsKey(Constants.RELATED_DIGITAL_USER_KEY) && userExVid[Constants.RELATED_DIGITAL_USER_KEY] != null) {
                val keyID = userExVid[Constants.RELATED_DIGITAL_USER_KEY]
                if (keyID is String) {
                    message.keyID = keyID.toString()
                }
            }
            if (userExVid.containsKey(Constants.EMAIL_KEY) && userExVid[Constants.EMAIL_KEY] != null) {
                val email = userExVid[Constants.EMAIL_KEY]
                if (email is String) {
                    message.email= email.toString()
                }
            }
            jsonArray!!.put(JSONObject(Gson().toJson(message)))
            jsonArray
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Could not save the push message!")
            Log.e(LOG_TAG, e.message!!)
            null
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun removeOldOnes(context: Context, jsonArray: JSONArray): JSONArray {
        val newJsonArray = JSONArray()

        for (i in 0 until jsonArray.length()) {
            try {
                val jsonObject = jsonArray.getJSONObject(i)

                val hasValidDate = jsonObject.has("date") && !isOld(context, jsonObject.getString("date"))
                val hasPushId = jsonObject.has("pushId")

                if (hasValidDate && hasPushId) {
                    newJsonArray.put(jsonObject)
                }

            } catch (e: Exception) {
                Log.e(LOG_TAG, "Eski mesajlar temizlenirken bir öğe işlenemedi: ${e.message}")
            }
        }

        return newJsonArray
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
            message.status = "D"
            message.openDate = ""
            val userExVid: Map<String, Any?> =
                RelatedDigital.getRelatedDigitalModel(context).getExtra()
            if (userExVid.containsKey(Constants.RELATED_DIGITAL_USER_KEY) && userExVid[Constants.RELATED_DIGITAL_USER_KEY] != null) {
                val keyID = userExVid[Constants.RELATED_DIGITAL_USER_KEY]
                if (keyID is String) {
                    message.keyID = keyID.toString()
                }
            }
            if (userExVid.containsKey(Constants.EMAIL_KEY) && userExVid[Constants.EMAIL_KEY] != null) {
                val email = userExVid[Constants.EMAIL_KEY]
                if (email is String) {
                    message.email= email.toString()
                }
            }
            jsonArray.put(JSONObject(Gson().toJson(message)))
            jsonObject.put(Constants.PAYLOAD_SP_ARRAY_KEY, jsonArray)
            SharedPref.writeStringPayload(context, Constants.PAYLOAD_SP_KEY, jsonObject.toString())
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Could not save the push message!" + e.message)
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
            message.status = "D"
            message.openDate = ""
            val userExVid: Map<String, Any?> =
                RelatedDigital.getRelatedDigitalModel(context).getExtra()
            if (userExVid.containsKey(Constants.RELATED_DIGITAL_USER_KEY) && userExVid[Constants.RELATED_DIGITAL_USER_KEY] != null) {
                val keyID = userExVid[Constants.RELATED_DIGITAL_USER_KEY]
                if (keyID is String) {
                    message.keyID = keyID.toString()
                }
            }
            if (userExVid.containsKey(Constants.EMAIL_KEY) && userExVid[Constants.EMAIL_KEY] != null) {
                val email = userExVid[Constants.EMAIL_KEY]
                if (email is String) {
                    message.email= email.toString()
                }
            }
            jsonArray.put(JSONObject(Gson().toJson(message)))
            jsonObject.put(Constants.PAYLOAD_SP_ARRAY_ID_KEY, jsonArray)
            SharedPref.writeStringPayload(context, Constants.PAYLOAD_SP_ID_KEY, jsonObject.toString())
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Could not save the push message!" + e.message)
        }
    }

    fun updatePayload(context: Context, pushId: String?) {
        try {
            val jsonString = SharedPref.readString(context, Constants.PAYLOAD_SP_KEY, "")
            val jsonObject = JSONObject(jsonString)

            val payloadsArray = jsonObject.optJSONArray(Constants.PAYLOAD_SP_ARRAY_KEY)

            if (payloadsArray != null) {
                for (i in 0 until payloadsArray.length()) {
                    val payloadObject = payloadsArray.getJSONObject(i)
                    val existingPushId = payloadObject.optString("pushId", "")

                    if (existingPushId == pushId) {
                        // Güncelleme işlemlerini yap
                        payloadObject.put("status", "O")
                        payloadObject.put("openDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))

                        // Güncellenmiş JSON'ı kaydet
                        SharedPref.writeStringPayload(context, Constants.PAYLOAD_SP_KEY, jsonObject.toString())
                        return // Güncelleme işlemi tamamlandı, fonksiyondan çık
                    }
                }

                // Eğer bu noktaya gelinirse, belirtilen pushId ile bir payload bulunamamıştır.
                Log.e(LOG_TAG, "Payload with pushId $pushId not found!")
            } else {
                Log.e(LOG_TAG, "Payload array is null or empty!")
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Could not update the push message!" + e.message)
        }
    }

    fun readAllPushMessages(context: Context, pushId: String? = null) {
        try {
            val jsonString = SharedPref.readString(context, Constants.PAYLOAD_SP_KEY, "")
            val jsonObject = JSONObject(jsonString)

            val payloadsArray = jsonObject.optJSONArray(Constants.PAYLOAD_SP_ARRAY_KEY)

            if (payloadsArray != null) {
                for (i in 0 until payloadsArray.length()) {
                    val payloadObject = payloadsArray.getJSONObject(i)
                    val existingPushId = payloadObject.optString("pushId", "")
                    if (!pushId.isNullOrEmpty()){
                        if (existingPushId == pushId) {

                            payloadObject.put("status", "O")
                            payloadObject.put("openDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))


                            SharedPref.writeStringPayload(context, Constants.PAYLOAD_SP_KEY, jsonObject.toString())
                            return
                        }
                    }

                    else if (pushId.isNullOrEmpty()) {
                        payloadObject.put("status", "O")
                        payloadObject.put("openDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))

                        SharedPref.writeStringPayload(context, Constants.PAYLOAD_SP_KEY, jsonObject.toString())
                        return
                    }
                }

                Log.e(LOG_TAG, "Payload with pushId $pushId not found!")
            } else {
                Log.e(LOG_TAG, "Payload array is null or empty!")
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Could not update the push message!" + e.message)
        }
    }

    fun readPushMessagesWithPushId(context: Context, pushId: String? = null) {
        try {
            val jsonString = SharedPref.readString(context, Constants.PAYLOAD_SP_KEY, "")
            val jsonObject = JSONObject(jsonString)

            val payloadsArray = jsonObject.optJSONArray(Constants.PAYLOAD_SP_ARRAY_KEY)

            if (payloadsArray != null) {
                for (i in 0 until payloadsArray.length()) {
                    val payloadObject = payloadsArray.getJSONObject(i)
                    val existingPushId = payloadObject.optString("pushId", "")
                    if (!pushId.isNullOrEmpty()){
                        if (existingPushId == pushId) {

                            payloadObject.put("status", "O")
                            payloadObject.put("openDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))


                            SharedPref.writeStringPayload(context, Constants.PAYLOAD_SP_KEY, jsonObject.toString())
                            return
                        }
                    }
                }

                Log.e(LOG_TAG, "Payload with pushId $pushId not found!")
            } else {
                Log.e(LOG_TAG, "Payload array is null or empty!")
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Could not update the push message!" + e.message)
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
            Log.e(LOG_TAG, "Could not parse date!" + e.message)
        }
        return res
    }
}