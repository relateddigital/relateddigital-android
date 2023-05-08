package com.relateddigital.relateddigital_android.network.requestHandler

import android.content.Context
import android.os.Build
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Retention
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.RequestSender
import com.relateddigital.relateddigital_android.push.RetentionType
import com.relateddigital.relateddigital_android.util.GoogleUtils
import com.relateddigital.relateddigital_android.util.RetryCounterManager

object RetentionRequest {
    private const val LOG_TAG = "RetentionRequest"

    private var latestDeliverPushId: String = ""
    private var latestOpenPushId: String = ""

    fun createRetentionRequest(
        context: Context, type: RetentionType,
        pushId: String?, emPushSp: String?
    ) {
        if (Build.VERSION.SDK_INT < Constants.SDK_MIN_API_VERSION) {
            Log.e(LOG_TAG, "RelatedDigital SDK requires min API level 21!")
            return
        }

        val latestPushId: String = when(type) {
            RetentionType.DELIVER, RetentionType.SILENT -> {
                latestDeliverPushId
            } else -> {
                latestOpenPushId
            }
        }

        if(!pushId.isNullOrEmpty() && pushId != latestPushId) {
            when (type) {
                RetentionType.DELIVER, RetentionType.SILENT -> {
                    latestDeliverPushId = pushId
                }
                else -> {
                    latestOpenPushId = pushId
                }
            }

            val retention = Retention()

            if(GoogleUtils.checkPlayService(context)) {
                retention.key = RelatedDigital.getRelatedDigitalModel(context).getGoogleAppAlias()
            } else {
                retention.key = RelatedDigital.getRelatedDigitalModel(context).getHuaweiAppAlias()
            }

            retention.pushId = pushId

            when (type) {
                RetentionType.DELIVER -> {
                    retention.status = "D"
                    retention.deliver = 1
                }
                RetentionType.SILENT -> {
                    retention.status = "S"
                    retention.deliver = 1
                }
                else -> {
                    retention.status = "O"
                    retention.deliver = 0
                }
            }

            retention.token = RelatedDigital.getRelatedDigitalModel(context).getToken()
            retention.actionBtn = 0
            retention.isMobile = 1

            if(!emPushSp.isNullOrEmpty()) {
                retention.emPushSp = emPushSp
            }

            RequestSender.sendRetentionRequest(context, retention, RetryCounterManager.counterId)
        }
    }
}
