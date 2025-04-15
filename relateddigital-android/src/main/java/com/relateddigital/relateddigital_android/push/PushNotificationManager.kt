package com.relateddigital.relateddigital_android.push

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Actions
import com.relateddigital.relateddigital_android.model.CarouselItem
import com.relateddigital.relateddigital_android.model.Element
import com.relateddigital.relateddigital_android.model.Message
import com.relateddigital.relateddigital_android.push.carousel.CarouselBuilder
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.ImageUtils
import com.relateddigital.relateddigital_android.util.LogUtils
import com.relateddigital.relateddigital_android.util.SharedPref

class PushNotificationManager {
    var intent: Intent? = null
    fun generateCarouselNotification(context: Context, pushMessage: Message, notificationId: Int) {
        val elements: ArrayList<Element> = pushMessage.getElements()!!
        val carouselBuilder: CarouselBuilder =
            CarouselBuilder.with(context, notificationId)!!.beginTransaction()
        carouselBuilder.setContentTitle(pushMessage.title)
            .setContentText(pushMessage.message)
        for (item in elements) {
            val cItem =
                CarouselItem(item.id, item.title, item.content, item.picture)
            carouselBuilder.addCarouselItem(cItem)
        }
        carouselBuilder.buildCarousel(pushMessage)
    }

    fun generateNotification(
        context: Context,
        pushMessage: Message,
        image: Bitmap?,
        notificationId: Int
    ) {
        try {
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager != null) {
                createNotificationChannel(mNotificationManager, pushMessage.sound, context)
            }
            intent = AppUtils.getStartActivityIntent(context, pushMessage)
            val contentIntent: PendingIntent = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                PendingIntent.getActivity(
                    context, notificationId,
                    intent!!, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getActivity(
                    context, notificationId,
                    intent!!, PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            val mBuilder = createNotificationBuilder(context, image, pushMessage, contentIntent)
            mNotificationManager?.notify(notificationId, mBuilder.build())
        } catch (e: Exception) {
            Log.d(LOG_TAG, "Generate notification : " + e.message)
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                context,
                "e",
                "Creating notification : " + e.message,
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
        }
    }

    fun createNotificationBuilder(
        context: Context, contentTitle: String?,
        contentText: String?, pushMessage: Message,
        notificationId: Int
    ): NotificationCompat.Builder {
        val title = if (TextUtils.isEmpty(contentTitle)) " " else contentTitle!!
        val largeIconBitmap: Bitmap?
        val willLargeIconBeUsed: Boolean =
            SharedPref.readBoolean(context, Constants.NOTIFICATION_USE_LARGE_ICON)
        if (willLargeIconBeUsed) {
            var largeIcon: Int
            if (isInDarkMode(context)) {
                largeIcon =
                    SharedPref.readInt(context, Constants.NOTIFICATION_LARGE_ICON_DARK_MODE)
                if (largeIcon == 0 || !AppUtils.isResourceAvailable(context, largeIcon)) {
                    largeIcon = SharedPref.readInt(context, Constants.NOTIFICATION_LARGE_ICON)
                }
            } else {
                largeIcon = SharedPref.readInt(context, Constants.NOTIFICATION_LARGE_ICON)
            }
            largeIconBitmap =
                if (largeIcon == 0 || !AppUtils.isResourceAvailable(context, largeIcon)) {
                    BitmapFactory.decodeResource(
                        context.resources,
                        ImageUtils.getAppIcon(context)
                    )
                } else {
                    BitmapFactory.decodeResource(
                        context.resources,
                        largeIcon
                    )
                }
        } else {
            largeIconBitmap = null
        }
        intent = AppUtils.getStartActivityIntent(context, pushMessage)
        val contentIntent: PendingIntent = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            PendingIntent.getActivity(
                context, notificationId,
                intent!!, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context, notificationId,
                intent!!, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val priority: String =
            SharedPref.readString(context, Constants.NOTIFICATION_PRIORITY_KEY)
        val importance = if (priority == "high") {
            NotificationCompat.PRIORITY_HIGH
        } else if (priority == "low") {
            NotificationCompat.PRIORITY_LOW
        } else {
            NotificationCompat.PRIORITY_DEFAULT
        }

        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, AppUtils.getNotificationChannelId(context, false))
        mBuilder.setContentTitle(title)
            .setContentText(contentText)
            .setLargeIcon(largeIconBitmap)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setDefaults(Notification.DEFAULT_VIBRATE or Notification.FLAG_SHOW_LIGHTS)
            .setPriority(importance)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
        setNumber(mBuilder, context)
        setNotificationSmallIcon(mBuilder, context)
        return mBuilder
    }

    private fun createNotificationBuilder(
        context: Context,
        pushImage: Bitmap?, pushMessage: Message, contentIntent: PendingIntent
    ): NotificationCompat.Builder {
        val title: String = pushMessage.title.toString()
        val largeIconBitmap: Bitmap?
        val willLargeIconBeUsed: Boolean =
            SharedPref.readBoolean(context, Constants.NOTIFICATION_USE_LARGE_ICON)
        if (willLargeIconBeUsed) {
            var largeIcon: Int
            if (isInDarkMode(context)) {
                largeIcon =
                    SharedPref.readInt(context, Constants.NOTIFICATION_LARGE_ICON_DARK_MODE)
                if (largeIcon == 0 || !AppUtils.isResourceAvailable(context, largeIcon)) {
                    largeIcon = SharedPref.readInt(context, Constants.NOTIFICATION_LARGE_ICON)
                }
            } else {
                largeIcon = SharedPref.readInt(context, Constants.NOTIFICATION_LARGE_ICON)
            }
            largeIconBitmap =
                if (largeIcon == 0 || !AppUtils.isResourceAvailable(context, largeIcon)) {
                    BitmapFactory.decodeResource(
                        context.resources,
                        ImageUtils.getAppIcon(context)
                    )
                } else {
                    BitmapFactory.decodeResource(
                        context.resources,
                        largeIcon
                    )
                }
        } else {
            largeIconBitmap = null
        }

        val priority: String =
            SharedPref.readString(context, Constants.NOTIFICATION_PRIORITY_KEY)
        val importance = if (priority == "high") {
            NotificationCompat.PRIORITY_HIGH
        } else if (priority == "low") {
            NotificationCompat.PRIORITY_LOW
        } else {
            NotificationCompat.PRIORITY_DEFAULT
        }



       val actionList = ArrayList<NotificationCompat.Action>()
        val actions: ArrayList<Actions>? = pushMessage.getActions()

        if (actions != null && actions.isNotEmpty()) {
            actions.forEach { actionItem ->
                val linkUri = Uri.parse(actionItem?.Url)
                val actionIntent = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R){
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent(context,NotificationActionBroadcastReceiver::class.java).setAction("ACTION_CLICK").putExtra("KEY_ACTION_ITEM", linkUri),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                } else {
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent(context,NotificationActionBroadcastReceiver::class.java).setAction("ACTION_CLICK").putExtra("KEY_ACTION_ITEM", linkUri),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                var actionIcon = R.drawable.ic_carousel_icon
                    if (!actionItem.Icon.isNullOrEmpty()){
                   actionIcon = actionItem.Icon!!.toInt()
                }
                var actionTitle = actionItem.Title ?: "Default Title"
                if (!actionItem.Title.isNullOrEmpty()){
                    actionTitle = actionItem.Title!!
                }

                val action = NotificationCompat.Action.Builder(
                    actionIcon,
                    actionTitle,
                    actionIntent
                ).build()
                actionList.add(action)
            }
        }

        val style = if (pushImage == null) NotificationCompat.BigTextStyle()
            .bigText(pushMessage.message) else NotificationCompat.BigPictureStyle()
            .bigPicture(pushImage).setSummaryText(pushMessage.message)
        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, AppUtils.getNotificationChannelId(context, false))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(style)
                .setLargeIcon(largeIconBitmap)
                .setColorized(false)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE or Notification.FLAG_SHOW_LIGHTS)
                .setPriority(importance)
                .setContentText(pushMessage.message)
        if (!title.isNullOrEmpty()) {
            mBuilder.setContentTitle(title)
        }
                // TODO !!
        if (actions != null && actions.isNotEmpty()) {
            for (action in actionList) {
                mBuilder.addAction(action)
            }
        }
        setNumber(mBuilder, context)
        setNotificationSmallIcon(mBuilder, context)
        if (pushMessage.sound != null) {
            mBuilder.setSound(AppUtils.getSound(context, pushMessage.sound))
        } else {
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }
        mBuilder.setContentIntent(contentIntent)
        return mBuilder
    }

    private fun setNumber(mBuilder: NotificationCompat.Builder, context: Context) {
        if (SharedPref.readInt(context, Constants.BADGE) == Constants.ACTIVE) {
            mBuilder.setNumber(1).setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        }
    }

    private fun setNotificationSmallIcon(builder: NotificationCompat.Builder, context: Context) {
        var transparentSmallIcon: Int
        if (isInDarkMode(context)) {
            transparentSmallIcon = SharedPref.readInt(
                context,
                Constants.NOTIFICATION_TRANSPARENT_SMALL_ICON_DARK_MODE
            )
            if (transparentSmallIcon == 0 || !AppUtils.isResourceAvailable(
                    context,
                    transparentSmallIcon
                )
            ) {
                transparentSmallIcon =
                    SharedPref.readInt(context, Constants.NOTIFICATION_TRANSPARENT_SMALL_ICON)
            }
        } else {
            transparentSmallIcon =
                SharedPref.readInt(context, Constants.NOTIFICATION_TRANSPARENT_SMALL_ICON)
        }
        if (transparentSmallIcon == 0 || !AppUtils.isResourceAvailable(
                context,
                transparentSmallIcon
            )
        ) {
            transparentSmallIcon = ImageUtils.getAppIcon(context)
        }
        builder.setSmallIcon(transparentSmallIcon)
        if (!SharedPref.readString(context, Constants.NOTIFICATION_COLOR).equals("")) {
            val color: String = SharedPref.readString(context, Constants.NOTIFICATION_COLOR)
            builder.color = Color.parseColor(color)
        }
    }

    private fun isInDarkMode(context: Context): Boolean {
        return context.resources.getString(R.string.mode) == "Night"
    }

    companion object {
        private const val LOG_TAG = "PushNManager"
        @TargetApi(Build.VERSION_CODES.O)
        fun createNotificationChannel(
            notificationManager: NotificationManager,
            sound: String?,
            context: Context
        ) {
            val priority: String =
                SharedPref.readString(context, Constants.NOTIFICATION_PRIORITY_KEY)
            val importance = if (priority == "high") {
                NotificationManager.IMPORTANCE_HIGH
            } else if (priority == "low") {
                NotificationManager.IMPORTANCE_LOW
            } else {
                NotificationManager.IMPORTANCE_DEFAULT
            }

            val notificationChannel = NotificationChannel(
                AppUtils.getNotificationChannelId(context, false),
                getChannelName(context),
                importance
            )
            notificationChannel.description = getChannelDescription(context)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            if (sound != null) {
                val soundUri: Uri = AppUtils.getSound(context, sound)
                val attributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
                notificationChannel.setSound(soundUri, attributes)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }

        fun getChannelDescription(context: Context): String {
            return AppUtils.getApplicationName(context)
        }
        /*
        fun tryPush(context: Context) {


        }
        */
        fun getChannelName(context: Context): String {
            return if (SharedPref.readString(context, Constants.CHANNEL_NAME) != "") {
                SharedPref.readString(context, Constants.CHANNEL_NAME)
            } else AppUtils.getApplicationName(context)
        }
    }
}