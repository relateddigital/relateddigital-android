package com.relateddigital.relateddigital_android.push.services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Message
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.push.RetentionType
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.LogUtils
import com.relateddigital.relateddigital_android.util.SharedPref
import java.lang.Exception

class RelatedDigitalOpenReportService : IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    constructor(name: String?) : super(name) {}
    constructor() : super("EuroMessageOpenReportService") {}

    override fun onHandleIntent(intent: Intent?) {
        if (intent!!.extras != null) {
            val message: Message? = intent.extras!!.getSerializable("message") as Message?
            if (message != null) {
                sendOpenReport(message)
                startTheRelatedActivity(message)
            } else {
                Log.e(LOG_TAG, "Could not send the open report since the payload is empty!!")
            }
        } else {
            Log.e("PushClick : ", "The payload is empty. The read report could not be sent!")
        }
    }

    private fun sendOpenReport(message: Message) {
        RequestHandler.createRetentionRequest(
            this, RetentionType.OPEN,
            message.pushId, message.emPushSp
        )
    }

    private fun startTheRelatedActivity(pushMessage: Message?) {
        val intentStr: String =
            SharedPref.readString(applicationContext, Constants.INTENT_NAME)
        var intent: Intent
        if (intentStr.isNotEmpty()) {
            try {
                intent = Intent(applicationContext, Class.forName(intentStr))
                intent.putExtra("message", pushMessage)
            } catch (e: Exception) {
                val element = Throwable().stackTrace[0]
                LogUtils.formGraylogModel(
                    this,
                    "e",
                    "Navigating to the activity of the customer : " + e.message,
                    element.className + "/" + element.methodName + "/" + element.lineNumber
                )
                Log.e("PushClick : ", "The class could not be found!")
                intent = AppUtils.getLaunchIntent(applicationContext, pushMessage)
            }
        } else {
            intent = AppUtils.getLaunchIntent(applicationContext, pushMessage)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    companion object {
        private const val LOG_TAG = "OpenReportService"
    }
}