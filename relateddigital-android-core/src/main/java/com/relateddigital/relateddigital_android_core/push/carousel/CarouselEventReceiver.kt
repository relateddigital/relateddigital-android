package com.relateddigital.relateddigital_android_core.push.carousel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.relateddigital.relateddigital_android_core.constants.Constants
import com.relateddigital.relateddigital_android_core.model.Carousel
import com.relateddigital.relateddigital_android_core.model.Message
import com.relateddigital.relateddigital_android_core.network.RequestHandler
import com.relateddigital.relateddigital_android_core.push.RetentionType

class CarouselEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        if (bundle != null) {
            val message: Message? = bundle.getSerializable("message") as Message?
            val notificationId = bundle.getInt(Constants.NOTIFICATION_ID)
            val carouselEvent = bundle.getInt(Constants.EVENT_CAROUSAL_ITEM_CLICKED_KEY)
            val carousel: Carousel? = bundle.getParcelable(Constants.CAROUSAL_SET_UP_KEY)
            if (carouselEvent > Constants.EVENT_RIGHT_ARROW_CLICKED) {
                if (message != null) {
                    sendOpenReport(message, context)
                } else {
                    Log.e(LOG_TAG, "Could not send the open report since the payload is empty!!")
                }
            }
            if (carouselEvent > 0 && carousel != null) CarouselBuilder.with(
                context,
                notificationId
            )!!
                .handleClickEvent(carouselEvent, carousel)
        }
    }

    private fun sendOpenReport(message: Message, context: Context) {
        RequestHandler.createRetentionRequest(
            context, RetentionType.OPEN,
            message.pushId, message.emPushSp
        )
    }

    companion object {
        private const val LOG_TAG = "CarouselEventReceiver"
    }
}