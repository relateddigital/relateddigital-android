package com.relateddigital.relateddigital_android.util

import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.api.ApiMethods
import com.relateddigital.relateddigital_android.api.GraylogApiClient
import com.relateddigital.relateddigital_android.api.LogConfigApiClient
import com.relateddigital.relateddigital_android.model.GraylogModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET

object LogUtils {
    private const val LOG_TAG = "LogUtils"

    // Log config için data class
    data class LogConfig(
        val isLoggingEnabled: Boolean,
        val excludedCustomerIds: List<String>
    )

    // Log config API interface
    interface LogConfigApi {
        @GET("log_rc.json")
        fun getLogConfig(): Call<LogConfig>
    }

    private fun shouldSendLog(context: Context): Boolean {
        return try {
            val apiService = LogConfigApiClient.client?.create(LogConfigApi::class.java)
            val call = apiService?.getLogConfig()
            val response = call?.execute()

            val config = response?.body() ?: return true // Config yoksa default true
            if (!config.isLoggingEnabled) return false
            var customerId = ""
            val googleAppAlias = RelatedDigital.getRelatedDigitalModel(context).getGoogleAppAlias()
            if (googleAppAlias.isNullOrEmpty()) {
                customerId = RelatedDigital.getRelatedDigitalModel(context).getHuaweiAppAlias()
            } else {
                customerId = googleAppAlias
            }

                ?: return true // customerId yoksa log gönder
            Log.i(LOG_TAG, "Customer ID: $customerId")

            !config.excludedCustomerIds.contains(customerId)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to fetch log config: ${e.message}")
            true // Hata durumunda default olarak log gönder
        }
    }

    private fun sendGraylogMessage(graylogModel: GraylogModel) {
        if (GraylogApiClient.client == null) {
            Log.e(LOG_TAG, "Euromessage SDK requires min API level 21!")
            return
        }
        val apiService: ApiMethods =
            GraylogApiClient.client!!.create(ApiMethods::class.java)
        val call: Call<Void> = apiService.sendLogToGraylog(graylogModel)
        call.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.isSuccessful) {
                    Log.i(LOG_TAG, "Sending the graylog message is successful")
                } else {
                    Log.i(LOG_TAG, "Sending the graylog message is failed")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.i(LOG_TAG, "Sending the graylog message is failed")
                t.printStackTrace()
            }
        })
    }

    fun formGraylogModel(
        context: Context, logLevel: String, logMessage: String,
        logPlace: String
    ) {
        if (!shouldSendLog(context)) {
            Log.i(LOG_TAG, "Log sending skipped due to configuration")
            return
        }
        val graylogModel = GraylogModel()
        graylogModel.logLevel = logLevel
        graylogModel.logMessage = logMessage
        graylogModel.logPlace = logPlace
        graylogModel.googleAppAlias = RelatedDigital.getRelatedDigitalModel(context).getGoogleAppAlias()
        graylogModel.huaweiAppAlias = RelatedDigital.getRelatedDigitalModel(context).getHuaweiAppAlias()
        graylogModel.token = RelatedDigital.getRelatedDigitalModel(context).getToken()
        graylogModel.appVersion = RelatedDigital.getRelatedDigitalModel(context).getAppVersion()
        graylogModel.sdkVersion = RelatedDigital.getRelatedDigitalModel(context).getSdkVersion()
        graylogModel.sdkType = RelatedDigital.getRelatedDigitalModel(context).getSdkType()
        graylogModel.osType = RelatedDigital.getRelatedDigitalModel(context).getOsType()
        graylogModel.osVersion = RelatedDigital.getRelatedDigitalModel(context).getOsVersion()
        graylogModel.deviceName = RelatedDigital.getRelatedDigitalModel(context).getDeviceName()
        graylogModel.userAgent = RelatedDigital.getRelatedDigitalModel(context).getUserAgent()
        graylogModel.identifierForVendor = RelatedDigital.getRelatedDigitalModel(context).getIdentifierForVendor()
        graylogModel.extra = RelatedDigital.getRelatedDigitalModel(context).getExtra()

        sendGraylogMessage(graylogModel)
    }
}