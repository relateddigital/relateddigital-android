package com.relateddigital.relateddigital_android.network.requestHandler

import android.content.Context
import android.os.Build
import android.util.Log
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.RequestSender
import com.relateddigital.relateddigital_android.push.EuromessageCallback
import com.relateddigital.relateddigital_android.util.RetryCounterManager

object SyncRequest {
    private const val LOG_TAG = "SyncRequest"

    fun createSyncRequest(context: Context, callback: EuromessageCallback? = null) {
        if (Build.VERSION.SDK_INT < Constants.SDK_MIN_API_VERSION) {
            Log.e(LOG_TAG, "RelatedDigital SDK requires min API level 21!")
            return
        }

        val model = RelatedDigital.getRelatedDigitalModel(context)

        if (model.getToken().isEmpty() || (model.getGoogleAppAlias().isEmpty() && model.getHuaweiAppAlias().isEmpty()) ) {
            Log.e(LOG_TAG, "token or appKey cannot be null!")
            return
        }

        if(!model.isEqual(RelatedDigital.getPreviousModel()) && model.isValid(context)) {
            RelatedDigital.updatePreviousModel(context)

            RequestSender.sendSubscriptionRequest(context, model, RetryCounterManager.counterId, callback)
        }
    }
}