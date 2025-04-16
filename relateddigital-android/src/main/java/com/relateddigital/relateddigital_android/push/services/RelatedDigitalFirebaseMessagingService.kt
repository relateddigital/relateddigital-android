package com.relateddigital.relateddigital_android.push.services

import android.app.NotificationManager
import android.content.Context // Context'i import et
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
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

open class RelatedDigitalFirebaseMessagingService : FirebaseMessagingService() {

    // Instance metodu artık sadece companion object'teki metodu çağıracak
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage) // İsterseniz üst sınıfın metodunu çağırabilirsiniz
        // İşi companion object'teki metoda devredin
        handlePushMessage(this, remoteMessage)
    }

    override fun onNewToken(token: String) {
        // Benzer şekilde onNewToken mantığını da companion object'e taşıyabilirsiniz
        // Şimdilik mevcut haliyle bırakalım veya aşağıdaki gibi yapalım:
        handleNewToken(this, token)
    }

    companion object {
        private const val LOG_TAG = "RDFMessagingService" // LOG_TAG burada kalabilir

        // YENİ "STATİK" METOT: Asıl işi bu metot yapacak
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @JvmStatic // Java'dan da kolayca çağrılabilmesi için (opsiyonel)
        fun handlePushMessage(context: Context, remoteMessage: RemoteMessage) {
            Log.d(LOG_TAG, "Handling push message statically")
            val remoteMessageData = remoteMessage.data
            if (remoteMessageData.isEmpty()) {
                Log.e(LOG_TAG, "Push message is empty!")
                val element = Throwable().stackTrace[0]
                // LogUtils 'this' yerine 'context' alacak
                LogUtils.formGraylogModel(
                    context,
                    "e",
                    "RDFMessagingService : Push message is empty!",
                    element.className + "/" + element.methodName + "/" + element.lineNumber
                )
                return
            }

            // Message nesnesi 'this' yerine 'context' alacak
            val pushMessage = Message(context, remoteMessageData)
            if (pushMessage.emPushSp == null) {
                Log.i(
                    LOG_TAG,
                    "The push notification was not coming from Related Digital! Ignoring.."
                )
                return
            }

            // RelatedDigitalModel 'this' yerine 'context' alacak
            if (!RelatedDigital.getRelatedDigitalModel(context).getIsPushNotificationEnabled()) {
                Log.e(
                    LOG_TAG, "Push notification is not enabled." +
                            "Call RelatedDigital.setIsPushNotificationEnabled() first"
                )
                return
            }

            Log.d(LOG_TAG, "FirebasePayload : " + Gson().toJson(pushMessage))

            if (!pushMessage.silent.isNullOrEmpty() && pushMessage.silent == "true") {
                Log.i("RDFirebase", "Silent Push")
                // RetentionRequest 'this' yerine 'context' alacak
                RetentionRequest.createRetentionRequest(
                    context, RetentionType.SILENT,
                    pushMessage.pushId, pushMessage.emPushSp
                )
            } else {
                try {
                    if (pushMessage.getPushType() != null && pushMessage.pushId != null) {
                        val pushNotificationManager = PushNotificationManager()
                        val notificationId = Random().nextInt()
                        // getSystemService 'this' yerine 'context' üzerinden çağrılacak
                        val notificationManager =
                            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
                            // SharedPref 'this' yerine 'context' alacak
                            val channelName: String =
                                SharedPref.readString(context, Constants.NOTIFICATION_CHANNEL_NAME_KEY)
                            val channelDescription: String =
                                SharedPref.readString(context, Constants.NOTIFICATION_CHANNEL_DESCRIPTION_KEY)
                            val channelSound: String =
                                SharedPref.readString(context, Constants.NOTIFICATION_CHANNEL_SOUND_KEY)

                            // PushNotificationManager metotları 'this' yerine 'context' alacak
                            if (channelName != PushNotificationManager.getChannelName(context) ||
                                channelDescription != PushNotificationManager.getChannelDescription(context) ||
                                channelSound != pushMessage.sound
                            ) {
                                val oldChannelId: String =
                                    SharedPref.readString(context, Constants.NOTIFICATION_CHANNEL_ID_KEY)
                                if (oldChannelId.isNotEmpty()) {
                                    notificationManager.deleteNotificationChannel(oldChannelId)
                                }
                                // AppUtils metotları 'this' yerine 'context' alacak
                                AppUtils.getNotificationChannelId(context, true)
                            } else {
                                AppUtils.getNotificationChannelId(context, false)
                            }

                            SharedPref.writeString(
                                context,
                                Constants.NOTIFICATION_CHANNEL_NAME_KEY,
                                PushNotificationManager.getChannelName(context)
                            )
                            SharedPref.writeString(
                                context,
                                Constants.NOTIFICATION_CHANNEL_DESCRIPTION_KEY,
                                PushNotificationManager.getChannelDescription(context)
                            )

                            if (!pushMessage.sound.isNullOrEmpty()) {
                                SharedPref.writeString(
                                    context,
                                    Constants.NOTIFICATION_CHANNEL_SOUND_KEY,
                                    pushMessage.sound!!
                                )
                            }
                        }

                        when (pushMessage.getPushType()) {
                            PushType.Image -> if (pushMessage.getElements() != null) {
                                // pushNotificationManager metotları 'this' yerine 'context' alacak
                                pushNotificationManager.generateCarouselNotification(
                                    context,
                                    pushMessage,
                                    notificationId
                                )
                            } else {
                                pushMessage.mediaUrl?.let {
                                    pushNotificationManager.generateNotification(
                                        context,
                                        pushMessage,
                                        // AppUtils metotları 'this' yerine 'context' alacak
                                        AppUtils.getBitMapFromUri(context, it),
                                        notificationId
                                    )
                                }
                            }
                            PushType.Text, PushType.Video -> pushNotificationManager.generateNotification(
                                context,
                                pushMessage,
                                null,
                                notificationId
                            )
                            else -> pushNotificationManager.generateNotification(
                                context,
                                pushMessage,
                                null,
                                notificationId
                            )
                        }

                        if (pushMessage.deliver != null &&
                            pushMessage.deliver!!.lowercase() == "true"
                        ) {
                            RetentionRequest.createRetentionRequest(
                                context, RetentionType.DELIVER,
                                pushMessage.pushId, pushMessage.emPushSp
                            )
                        }

                        //PayloadUtils.sendUtmParametersEvent(context, pushMessage)

                        val notificationLoginId: String =
                            SharedPref.readString(context, Constants.NOTIFICATION_LOGIN_ID_KEY)

                        // PayloadUtils metotları 'this' yerine 'context' alacak
                        if (notificationLoginId.isEmpty()) {
                            PayloadUtils.addPushMessage(context, pushMessage)
                        } else {
                            PayloadUtils.addPushMessageWithId(context, pushMessage, notificationLoginId)
                        }
                    } else {
                        Log.d(LOG_TAG, "remoteMessageData transform problem")
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "Error handling push message: ${e.localizedMessage}")
                    // Hata loglaması için de context gerekebilir
                    LogUtils.formGraylogModel(
                        context,
                        "e",
                        "RDFMessagingService : Error handling push message : $e",
                        Throwable().stackTrace[0].let { "${it.className}/${it.methodName}/${it.lineNumber}" }
                    )
                }
            }
        }

        // onNewToken için de benzer bir statik metot
        @JvmStatic
        fun handleNewToken(context: Context, token: String) {
            Log.i(LOG_TAG, "Handling new token statically: $token")
            // RelatedDigital metotları 'this' yerine 'context' alacak
            val googleAppAlias: String = RelatedDigital.getGoogleAppAlias(context)
            val huaweiAppAlias: String = RelatedDigital.getHuaweiAppAlias(context)
            RelatedDigital.setIsPushNotificationEnabled(
                context,
                true,
                googleAppAlias,
                huaweiAppAlias,
                token
            )
            // PushUtils metotları 'this' yerine 'context' alacak
            PushUtils.sendBroadCast(
                Constants.PUSH_REGISTER_EVENT,
                null,
                token,
                context
            )
        }
    }
}