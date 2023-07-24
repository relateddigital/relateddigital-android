package com.relateddigital.relateddigital_android.util

import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.api.ApiMethods
import com.relateddigital.relateddigital_android.api.GraylogApiClient
import com.relateddigital.relateddigital_android.model.GraylogModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object LogUtils {
    private const val LOG_TAG = "LogUtils"

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