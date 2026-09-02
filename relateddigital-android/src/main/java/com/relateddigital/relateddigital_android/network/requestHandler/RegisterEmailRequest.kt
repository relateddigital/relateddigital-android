package com.relateddigital.relateddigital_android.network.requestHandler

import android.content.Context
import android.os.Build
import android.util.Log
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.RelatedDigitalModel
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.RequestSender
import com.relateddigital.relateddigital_android.util.PushDiagnostics
import com.relateddigital.relateddigital_android.util.RetryCounterManager

object RegisterEmailRequest {
    private const val LOG_TAG = "RegisterEmailRequest"

    fun createRegisterEmailRequest(context: Context, registerEmailModel: RelatedDigitalModel) {
        if (Build.VERSION.SDK_INT < Constants.SDK_MIN_API_VERSION) {
            Log.e(LOG_TAG, "RelatedDigital SDK requires min API level 21!")
            return
        }

        val tokenMissing = registerEmailModel.getToken().isEmpty()
        val appAliasMissing =
            PushDiagnostics.getEffectiveAppAlias(context, registerEmailModel).isEmpty()

        if (tokenMissing || appAliasMissing) {
            val reason = when {
                tokenMissing && appAliasMissing ->
                    "both the push token and the appAlias (appKey) are empty"
                tokenMissing -> "the push token is empty"
                else -> "the appAlias (appKey) used by this device is empty"
            }
            PushDiagnostics.logBlocked(
                context,
                registerEmailModel,
                "cannot register the email address because $reason",
                "Call RelatedDigital.setIsPushNotificationEnabled(context, true, " +
                        "googleAppAlias, huaweiAppAlias, token) with a valid token and appAlias " +
                        "before registering an email address."
            )
            return
        }

        RequestSender.sendSubscriptionRequest(
            context,
            registerEmailModel,
            RetryCounterManager.counterId,
            null
        )
    }
}