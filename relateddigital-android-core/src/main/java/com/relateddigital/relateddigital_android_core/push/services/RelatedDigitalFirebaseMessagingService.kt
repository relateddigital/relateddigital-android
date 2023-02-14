package com.relateddigital.relateddigital_android_core.push.services

import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.relateddigital.relateddigital_android_core.RelatedDigital
import com.relateddigital.relateddigital_android_core.constants.Constants
import com.relateddigital.relateddigital_android_core.model.Message
import com.relateddigital.relateddigital_android_core.model.PushType
import com.relateddigital.relateddigital_android_core.network.RequestHandler
import com.relateddigital.relateddigital_android_core.push.PushNotificationManager
import com.relateddigital.relateddigital_android_core.push.RetentionType
import com.relateddigital.relateddigital_android_core.util.AppUtils
import com.relateddigital.relateddigital_android_core.util.LogUtils
import com.relateddigital.relateddigital_android_core.util.PayloadUtils
import com.relateddigital.relateddigital_android_core.util.SharedPref
import java.util.*

class RelatedDigitalFirebaseMessagingService : FirebaseMessagingService() {
    companion object{
        private const val LOG_TAG = "RDFMessagingService"
    }
    override fun onNewToken(token: String) {
        Log.i(LOG_TAG, "On new token : $token")
        val googleAppAlias: String = RelatedDigital.getGoogleAppAlias(this)
        val huaweiAppAlias: String = RelatedDigital.getHuaweiAppAlias(this)
        RelatedDigital.setIsPushNotificationEnabled(
            this,
            true,
            googleAppAlias,
            huaweiAppAlias,
            token
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val remoteMessageData = remoteMessage.data
        if (remoteMessageData.isEmpty()) {
            Log.e(LOG_TAG, "Push message is empty!")
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                this,
                "e",
                "RDFMessagingService : " + "Push message is empty!",
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            return
        }
        val pushMessage = Message(this, remoteMessageData)
        if (pushMessage.emPushSp == null) {
            Log.i(
                LOG_TAG,
                "The push notification was not coming from Related Digital! Ignoring.."
            )
            return
        }

        if(!RelatedDigital.getRelatedDigitalModel(this).getIsPushNotificationEnabled()) {
            Log.e(
                LOG_TAG, "Push notification is not enabled." +
                        "Call RelatedDigital.setIsPushNotificationEnabled() first"
            )
            return
        }

        Log.d(LOG_TAG, "FirebasePayload : " + Gson().toJson(pushMessage))

        if(!pushMessage.silent.isNullOrEmpty() && pushMessage.silent == "true") {
            Log.i("RDFirebase", "Silent Push")
            RequestHandler.createRetentionRequest(
                this, RetentionType.SILENT,
                pushMessage.pushId, pushMessage.emPushSp
            )
        } else {

            if (pushMessage.getPushType() != null && pushMessage.pushId != null) {
                val pushNotificationManager = PushNotificationManager()
                val notificationId = Random().nextInt()
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
                    val channelName: String =
                        SharedPref.readString(this, Constants.NOTIFICATION_CHANNEL_NAME_KEY)
                    val channelDescription: String =
                        SharedPref.readString(this, Constants.NOTIFICATION_CHANNEL_DESCRIPTION_KEY)
                    val channelSound: String =
                        SharedPref.readString(this, Constants.NOTIFICATION_CHANNEL_SOUND_KEY)
                    if (channelName != PushNotificationManager.getChannelName(this) ||
                        channelDescription != PushNotificationManager.getChannelDescription(this) ||
                        channelSound != pushMessage.sound
                    ) {
                        val oldChannelId: String =
                            SharedPref.readString(this, Constants.NOTIFICATION_CHANNEL_ID_KEY)
                        if (oldChannelId.isNotEmpty()) {
                            notificationManager.deleteNotificationChannel(oldChannelId)
                        }
                        AppUtils.getNotificationChannelId(this, true)
                    } else {
                        AppUtils.getNotificationChannelId(this, false)
                    }

                    SharedPref.writeString(
                        this,
                        Constants.NOTIFICATION_CHANNEL_NAME_KEY,
                        PushNotificationManager.getChannelName(this)
                    )
                    SharedPref.writeString(
                        this,
                        Constants.NOTIFICATION_CHANNEL_DESCRIPTION_KEY,
                        PushNotificationManager.getChannelDescription(this)
                    )

                    if (!pushMessage.sound.isNullOrEmpty()) {
                        SharedPref.writeString(
                            this,
                            Constants.NOTIFICATION_CHANNEL_SOUND_KEY,
                            pushMessage.sound!!
                        )
                    }
                }

                when (pushMessage.getPushType()) {
                    PushType.Image -> if (pushMessage.getElements() != null) {
                        pushNotificationManager.generateCarouselNotification(
                            this,
                            pushMessage,
                            notificationId
                        )
                    } else {
                        pushNotificationManager.generateNotification(
                            this,
                            pushMessage,
                            AppUtils.getBitMapFromUri(this, pushMessage.mediaUrl!!),
                            notificationId
                        )
                    }
                    PushType.Text -> pushNotificationManager.generateNotification(
                        this,
                        pushMessage,
                        null,
                        notificationId
                    )
                    PushType.Video -> {}
                    else -> pushNotificationManager.generateNotification(
                        this,
                        pushMessage,
                        null,
                        notificationId
                    )
                }

                if (pushMessage.deliver != null &&
                    pushMessage.deliver!!.lowercase() == "true"
                ) {
                    RequestHandler.createRetentionRequest(
                        this, RetentionType.DELIVER,
                        pushMessage.pushId, pushMessage.emPushSp
                    )
                }

                PayloadUtils.sendUtmParametersEvent(this, pushMessage)

                val notificationLoginId: String =
                    SharedPref.readString(this, Constants.NOTIFICATION_LOGIN_ID_KEY)

                if (notificationLoginId.isEmpty()) {
                    PayloadUtils.addPushMessage(this, pushMessage)
                } else {
                    PayloadUtils.addPushMessageWithId(this, pushMessage, notificationLoginId)
                }
            } else {
                Log.d(LOG_TAG, "remoteMessageData transform problem")
            }
        }
    }
}