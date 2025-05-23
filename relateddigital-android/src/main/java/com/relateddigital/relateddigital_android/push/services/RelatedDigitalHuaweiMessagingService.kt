package com.relateddigital.relateddigital_android.push.services

import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Message
import com.relateddigital.relateddigital_android.model.PushType
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.RetentionRequest
import com.relateddigital.relateddigital_android.push.PushNotificationManager
import com.relateddigital.relateddigital_android.push.RetentionType
import com.relateddigital.relateddigital_android.util.*
import java.util.*

open class RelatedDigitalHuaweiMessagingService : HmsMessageService() {
    companion object {
        private const val LOG_TAG = "RDHMessagingService"
    }

    override fun onNewToken(token: String) {
        Log.i(LOG_TAG, "On new token : $token")
        val googleAppAlias: String = RelatedDigital.getGoogleAppAlias(this)
        val huaweiAppAlias: String = RelatedDigital.getHuaweiAppAlias(this)
        PushUtils.sendBroadCast(
            Constants.PUSH_REGISTER_EVENT,
            null,
            token,
            this
        )
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
        val remoteMessageData = remoteMessage.dataOfMap
        if (remoteMessageData.isEmpty()) {
            Log.e(LOG_TAG, "Push message is empty!")
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                this,
                "e",
                "RDHMessagingService : " + "Push message is empty!",
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

        if (!RelatedDigital.getRelatedDigitalModel(this).getIsPushNotificationEnabled()) {
            Log.e(
                LOG_TAG, "Push notification is not enabled." +
                        "Call RelatedDigital.setIsPushNotificationEnabled() first"
            )
            return
        }

        Log.d(LOG_TAG, "HuaweiPayload : " + Gson().toJson(pushMessage))

        PushUtils.sendBroadCast(
            Constants.PUSH_RECEIVE_EVENT,
            pushMessage,
            null,
            this
        )

        if(!pushMessage.silent.isNullOrEmpty() && pushMessage.silent.equals("true", true)) {
            Log.i("RDHuawei", "Silent Push")
            RetentionRequest.createRetentionRequest(
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
                    RetentionRequest.createRetentionRequest(
                        this, RetentionType.DELIVER,
                        pushMessage.pushId, pushMessage.emPushSp
                    )
                }

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