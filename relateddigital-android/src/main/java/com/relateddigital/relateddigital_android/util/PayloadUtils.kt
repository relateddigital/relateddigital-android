package com.relateddigital.relateddigital_android.util

import android.content.Context
import android.util.Log
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

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getDefault() // cihaz saat dilimi
    }

    suspend fun addPushMessage(context: Context, message: Message) {
        Log.w(LOG_TAG, "addPushMessage işlemi başlatıldı. Push ID: ${message.pushId}")

        DataStoreManager.updatePayloads(context) { currentPayload ->
            var finalPayloadString = currentPayload

            Log.d(LOG_TAG, "updatePayloads lambda'sı çalıştırıldı.")
            try {
                // Her zaman JSONObject formatı bekleniyor
                Log.w(LOG_TAG, "Mevcut payload verisi kontrol ediliyor. Boyut: ${currentPayload.length}")
                val jsonObject = if (currentPayload.isNotEmpty()) {
                    JSONObject(currentPayload)
                } else {
                    JSONObject()
                }
                Log.w(LOG_TAG, "Payload JSONObject'e dönüştürüldü veya yeni bir obje oluşturuldu.")

                var jsonArray = jsonObject.optJSONArray(Constants.PAYLOAD_SP_ARRAY_KEY)
                if (jsonArray == null) {
                    jsonArray = JSONArray()
                    Log.w(LOG_TAG, "Payload içinde array bulunamadı, yeni bir JSONArray oluşturuldu.")
                } else {
                    Log.w(LOG_TAG, "Mevcut JSONArray bulundu, boyutu: ${jsonArray.length()}")
                }

                // Aynı pushId varsa ekleme
                if (isPushIdAvailable(jsonArray, message)) {
                    Log.w(LOG_TAG, "Bu Push ID (${message.pushId}) zaten kayıtlı. İşlem durduruldu.")
                    return@updatePayloads currentPayload
                }
                Log.w(LOG_TAG, "Yeni bir mesaj olduğu onaylandı.")

                // Yeni mesajı ekle
                jsonArray = addNewOne(context, jsonArray, message)
                Log.w(LOG_TAG, "Yeni mesaj başarıyla eklendi.")

                // Eski mesajları temizle (30 günden eski olanları)
                Log.w(LOG_TAG, "Eski mesajlar temizleniyor...")
                jsonArray = removeOldOnes(jsonArray)
                Log.w(LOG_TAG, "Eski mesajları temizleme işlemi tamamlandı. Yeni array boyutu: ${jsonArray.length()}")


                // Güncellenmiş array’i tekrar JSONObject içine koy
                jsonObject.put(Constants.PAYLOAD_SP_ARRAY_KEY, jsonArray)
                Log.w(LOG_TAG, "Güncellenmiş array JSONObject içine eklendi.")

                finalPayloadString = jsonObject.toString()
                Log.w(LOG_TAG, "Push mesajı başarıyla kaydedildi. Push ID: ${message.pushId}")

            } catch (e: Exception) {
                Log.e(LOG_TAG, "Push mesajı işlenirken KRİTİK BİR HATA oluştu!", e)
            }

            finalPayloadString
        }
    }

    suspend fun addPushMessageWithId(context: Context, message: Message, loginID: String) {
        DataStoreManager.updatePayloadsById(context) { currentPayload ->
            var finalPayloadString: String
            try {
                if (currentPayload.isNotEmpty()) {
                    val jsonObject = JSONObject(currentPayload)
                    var jsonArray = jsonObject.optJSONArray(Constants.PAYLOAD_SP_ARRAY_ID_KEY)
                    if (isPushIdAvailable(jsonArray, message)) {
                        return@updatePayloadsById currentPayload
                    }
                    jsonArray = addNewOneWithID(context, jsonArray, message, loginID)
                    //jsonArray = removeOldOnes(jsonArray)
                    val finalObject = JSONObject()
                    finalObject.put(Constants.PAYLOAD_SP_ARRAY_ID_KEY, jsonArray)
                    finalPayloadString = finalObject.toString()
                } else {
                    finalPayloadString = createNewPayloadStringWithID(context, message, loginID)
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Push mesajı (ID ile) işlenirken hata oluştu!", e)
                finalPayloadString = currentPayload
            }
            finalPayloadString
        }
    }

    fun orderPushMessages(messages: MutableList<Message>): List<Message> {
        messages.sortWith { msg1, msg2 ->
            compareDates(msg1.date!!, msg2.date!!)
        }
        return messages
    }

    fun sendUtmParametersEvent(context: Context, message: Message) {
        val params = message.getParams()
        val properties = HashMap<String, String>()

        if (params.isNotEmpty()) {
            for (param in params.entries) {
                if (param.key in listOf(Constants.UTM_SOURCE, Constants.UTM_MEDIUM, Constants.UTM_CAMPAIGN, Constants.UTM_CONTENT, Constants.UTM_TERM)) {
                    properties[param.key] = param.value
                }
            }
            if (properties.isNotEmpty()) {
                RelatedDigital.sendCampaignParameters(context, properties)
            }
        }
    }

    private fun isPushIdAvailable(jsonArray: JSONArray?, message: Message): Boolean {
        jsonArray ?: return false
        for (i in 0 until jsonArray.length()) {
            try {
                if (jsonArray.getJSONObject(i).optString("pushId") == message.pushId) {
                    return true
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "isPushIdAvailable kontrolünde hata", e)
            }
        }
        return false
    }

    private fun addNewOne(context: Context, jsonArray: JSONArray, message: Message): JSONArray {
        try {
            message.date = dateFormat.format(Date())
            message.status = "D"
            message.openDate = ""
            val userExVid: Map<String, Any?> = RelatedDigital.getRelatedDigitalModel(context).getExtra()
            message.keyID = userExVid[Constants.RELATED_DIGITAL_USER_KEY]?.toString()
            message.email = userExVid[Constants.EMAIL_KEY]?.toString()
            jsonArray.put(JSONObject(Gson().toJson(message)))
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Mesaj JSON'a eklenemedi!", e)
        }
        return jsonArray
    }

    private fun addNewOneWithID(context: Context, jsonArray: JSONArray?, message: Message, loginID: String): JSONArray {
        val finalJsonArray = jsonArray ?: JSONArray()
        try {
            message.loginID = loginID
            message.date = dateFormat.format(Date())
            message.status = "D"
            message.openDate = ""
            val userExVid: Map<String, Any?> = RelatedDigital.getRelatedDigitalModel(context).getExtra()
            message.keyID = userExVid[Constants.RELATED_DIGITAL_USER_KEY]?.toString()
            message.email = userExVid[Constants.EMAIL_KEY]?.toString()
            finalJsonArray.put(JSONObject(Gson().toJson(message)))
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Mesaj JSON'a (ID ile) eklenemedi!", e)
        }
        return finalJsonArray
    }

    private fun removeOldOnes(jsonArray: JSONArray): JSONArray {
        val newJsonArray = JSONArray()
        var discardedCount = 0

        for (i in 0 until jsonArray.length()) {
            try {
                val jsonObject = jsonArray.getJSONObject(i)

                val hasDate = jsonObject.has("date")
                val isDateValid = hasDate && !isOld(jsonObject.getString("date"))
                val hasPushId = jsonObject.has("pushId")

                if (isDateValid && hasPushId) {
                    newJsonArray.put(jsonObject)
                } else {
                    discardedCount++
                    when {
                        !hasDate -> {
                            Log.w(LOG_TAG, "Mesaj atıldı (date yok): $jsonObject")
                        }
                        hasDate && !isDateValid -> {
                            Log.w(LOG_TAG, "Mesaj atıldı (eski tarih): $jsonObject")
                        }
                        !hasPushId -> {
                            Log.w(LOG_TAG, "Mesaj atıldı (pushId yok): $jsonObject")
                        }
                    }
                }

            } catch (e: Exception) {
                discardedCount++
                Log.e(LOG_TAG, "JSON işlenirken hata oluştu, öğe atıldı", e)
            }
        }

        // Özet log
        Log.i(
            LOG_TAG,
            "removeOldOnes özeti -> toplam: ${jsonArray.length()}, tutuldu: ${newJsonArray.length()}, atıldı: $discardedCount"
        )

        return newJsonArray
    }

    private fun isOld(date: String): Boolean {
        try {
            val messageDate = dateFormat.parse(date) ?: return true
            val now = Date()
            val difference = now.time - messageDate.time
            return (difference / (1000 * 60 * 60 * 24)) > DATE_THRESHOLD
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Tarih parse edilemedi: $date", e)
            return true
        }
    }

    private fun createNewPayloadString(context: Context, message: Message): String {
        return try {
            val jsonObject = JSONObject()
            val jsonArray = JSONArray()
            message.date = dateFormat.format(Date())
            message.status = "D"
            message.openDate = ""
            val userExVid: Map<String, Any?> = RelatedDigital.getRelatedDigitalModel(context).getExtra()
            message.keyID = userExVid[Constants.RELATED_DIGITAL_USER_KEY]?.toString()
            message.email = userExVid[Constants.EMAIL_KEY]?.toString()
            jsonArray.put(JSONObject(Gson().toJson(message)))
            jsonObject.put(Constants.PAYLOAD_SP_ARRAY_KEY, jsonArray)
            jsonObject.toString()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Yeni payload oluşturulamadı!", e)
            ""
        }
    }

    private fun createNewPayloadStringWithID(context: Context, message: Message, loginID: String): String {
        return try {
            val jsonObject = JSONObject()
            val jsonArray = JSONArray()
            message.loginID = loginID
            message.date = dateFormat.format(Date())
            message.status = "D"
            message.openDate = ""
            val userExVid: Map<String, Any?> = RelatedDigital.getRelatedDigitalModel(context).getExtra()
            message.keyID = userExVid[Constants.RELATED_DIGITAL_USER_KEY]?.toString()
            message.email = userExVid[Constants.EMAIL_KEY]?.toString()
            jsonArray.put(JSONObject(Gson().toJson(message)))
            jsonObject.put(Constants.PAYLOAD_SP_ARRAY_ID_KEY, jsonArray)
            jsonObject.toString()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Yeni payload (ID ile) oluşturulamadı!", e)
            ""
        }
    }

    suspend fun updatePayload(context: Context, pushId: String?) {
        pushId ?: return
        DataStoreManager.updatePayloads(context) { currentPayload ->
            try {
                val jsonObject = JSONObject(currentPayload)
                val payloadsArray = jsonObject.optJSONArray(Constants.PAYLOAD_SP_ARRAY_KEY)
                payloadsArray?.let {
                    for (i in 0 until it.length()) {
                        val payloadObject = it.getJSONObject(i)
                        if (payloadObject.optString("pushId") == pushId) {
                            payloadObject.put("status", "O")
                            payloadObject.put("openDate", dateFormat.format(Date()))
                            return@updatePayloads jsonObject.toString()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Payload güncellenemedi!", e)
            }
            currentPayload
        }
    }

    suspend fun readAllPushMessages(context: Context, pushId: String? = null) {
        DataStoreManager.updatePayloads(context) { currentPayload ->
            try {
                val jsonObject = JSONObject(currentPayload)
                val payloadsArray = jsonObject.optJSONArray(Constants.PAYLOAD_SP_ARRAY_KEY)
                payloadsArray?.let {
                    for (i in 0 until it.length()) {
                        val payloadObject = it.getJSONObject(i)
                        val updateTime = dateFormat.format(Date())
                        if (pushId.isNullOrEmpty() || payloadObject.optString("pushId") == pushId) {
                            payloadObject.put("status", "O")
                            payloadObject.put("openDate", updateTime)
                            if (!pushId.isNullOrEmpty()) {
                                return@updatePayloads jsonObject.toString()
                            }
                        }
                    }
                    if (pushId.isNullOrEmpty()) {
                        return@updatePayloads jsonObject.toString()
                    }
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Tüm mesajlar okunmuş olarak işaretlenemedi!", e)
            }
            currentPayload
        }
    }

    private fun compareDates(str1: String, str2: String): Int {
        return try {
            val date1 = dateFormat.parse(str1)
            val date2 = dateFormat.parse(str2)
            date2!!.compareTo(date1)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Tarih karşılaştırılamadı!", e)
            0
        }
    }
}