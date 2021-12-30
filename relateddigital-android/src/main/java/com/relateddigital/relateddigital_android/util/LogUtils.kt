package com.relateddigital.relateddigital_android.util

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.api.ApiMethods
import com.relateddigital.relateddigital_android.api.GraylogApiClient
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.GraylogModel
import com.relateddigital.relateddigital_android.model.RelatedDigitalModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object LogUtils {
    private const val LOG_TAG = "LogUtils"

    fun sendGraylogMessage(graylogModel: GraylogModel) {
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
        val modelStr = SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY, "")

        if (modelStr.isNotEmpty()) {
            val model = Gson().fromJson(modelStr, RelatedDigitalModel::class.java)
            RelatedDigital.init(
                context,
                model.getOrganizationId(),
                model.getProfileId(),
                model.getDataSource()
            )
            val graylogModel = GraylogModel()
            graylogModel.logLevel = logLevel
            graylogModel.logMessage = logMessage
            graylogModel.logPlace = logPlace
            graylogModel.googleAppAlias = RelatedDigital.getRelatedDigitalModel()!!.getGoogleAppAlias()
            graylogModel.huaweiAppAlias = RelatedDigital.getRelatedDigitalModel()!!.getHuaweiAppAlias()
            graylogModel.token = RelatedDigital.getRelatedDigitalModel()!!.getToken()
            graylogModel.appVersion = RelatedDigital.getRelatedDigitalModel()!!.getAppVersion()
            graylogModel.sdkVersion = RelatedDigital.getRelatedDigitalModel()!!.getSdkVersion()
            graylogModel.osType = RelatedDigital.getRelatedDigitalModel()!!.getOsType()
            graylogModel.osVersion = RelatedDigital.getRelatedDigitalModel()!!.getOsVersion()
            graylogModel.deviceName = RelatedDigital.getRelatedDigitalModel()!!.getDeviceName()
            graylogModel.userAgent = RelatedDigital.getRelatedDigitalModel()!!.getUserAgent()
            graylogModel.identifierForVendor = RelatedDigital.getRelatedDigitalModel()!!.getIdentifierForVendor()
            graylogModel.extra = RelatedDigital.getRelatedDigitalModel()!!.getExtra()

            sendGraylogMessage(graylogModel)
        }
    }
}