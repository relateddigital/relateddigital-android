package com.relateddigital.relateddigital_android.push.carousel

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Carousel
import com.relateddigital.relateddigital_android.model.CarouselItem
import com.relateddigital.relateddigital_android.model.Message
import com.relateddigital.relateddigital_android.push.PushNotificationManager
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.ImageUtils
import com.relateddigital.relateddigital_android.util.LogUtils
import com.relateddigital.relateddigital_android.util.SharedPref
import java.io.Serializable
import java.lang.Exception
import java.util.ArrayList

class CarouselBuilder private constructor(private val context: Context, notificationId: Int) :
    Serializable {
    private var carouselItems: ArrayList<CarouselItem>? = null
    private var contentTitle: String? = null
    private var contentText //title and text while it is small
            : String? = null
    private var bigContentTitle: String? = null
    private var bigContentText //title and text when it becomes large
            : String? = null
    private var leftItemTitle: String? = null
    private var leftItemDescription: String? = null
    private var rightItemTitle: String? = null
    private var rightItemDescription: String? = null
    var message: Message? = null
    private var mBuilder: NotificationCompat.Builder
    private var carouselNotificationId =
        9873715 //Random id for notification. Will cancel any notification that have existing same id.
    private var leftItem: CarouselItem? = null
    private var rightItem: CarouselItem? = null
    private var leftItemBitmap: Bitmap? = null
    private var rightItemBitmap: Bitmap? = null
    private var carousel: Carousel? = null
    private var smallIconPath: String? = null
    private var largeIconPath: String? = null
    private var placeHolderImagePath //Stores path of these images if set by user
            : String? = null
    private var isImagesInCarousel = true
    fun beginTransaction(): CarouselBuilder {
        clearCarouselIfExists()
        return this
    }

    fun addCarouselItem(carouselItem: CarouselItem?) {
        if (carouselItem != null) {
            if (carouselItems == null) {
                carouselItems = ArrayList<CarouselItem>()
            }
            carouselItems!!.add(carouselItem)
        } else {
            Log.e(TAG, "Null carousel can't be added!")
        }
    }

    fun setContentTitle(title: String?): CarouselBuilder {
        if (title != null) {
            contentTitle = title
        } else {
            Log.e(TAG, "Null parameter")
        }
        return this
    }

    fun setContentText(contentText: String?) {
        if (contentText != null) {
            this.contentText = contentText
        } else {
            Log.e(TAG, "Null parameter")
        }
    }

    fun setBigContentText(bigContentText: String?) {
        if (bigContentText != null) {
            this.bigContentText = bigContentText
        } else {
            Log.e(TAG, "Null parameter")
        }
    }

    fun setBigContentTitle(bigContentTitle: String?) {
        if (bigContentTitle != null) {
            this.bigContentTitle = bigContentTitle
        } else {
            Log.e(TAG, "Null parameter")
        }
    }

    fun setNotificationPriority(priority: Int): CarouselBuilder {
        if (!(priority >= NotificationCompat.PRIORITY_MIN && priority <= NotificationCompat.PRIORITY_MAX)) {
            Log.i(TAG, "Invalid priority")
        }
        return this
    }

    fun setSmallIconResource(resourceId: Int): CarouselBuilder {
        try {
            smallIcon = BitmapFactory.decodeResource(context.resources, resourceId)
        } catch (e: Exception) {
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                context,
                "e",
                "Getting carousel small icon bitmap : " + e.message,
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            smallIcon = null
            Log.e(TAG, "Unable to decode resource")
        }
        if (smallIcon != null) {  //meaning a valid resource
            smallIconResourceId = resourceId
        }
        return this
    }

    fun setLargeIcon(resourceId: Int): CarouselBuilder {
        try {
            largeIcon = BitmapFactory.decodeResource(context.resources, resourceId)
        } catch (e: Exception) {
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                context,
                "e",
                "Getting carousel large icon bitmap : " + e.message,
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            Log.e(TAG, "Unable to decode resource")
        }
        return this
    }

    fun setLargeIcon(large: Bitmap?): CarouselBuilder {
        if (large != null) {
            largeIcon = large
        } else {
            largeIcon = null
            Log.i(TAG, "Null parameter")
        }
        return this
    }

    fun setCarouselPlaceHolder(resourceId: Int) {
        try {
            caraousalPlaceholder = BitmapFactory.decodeResource(context.resources, resourceId)
        } catch (e: Exception) {
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                context,
                "e",
                "Getting carousel place holder bitmap : " + e.message,
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            caraousalPlaceholder = null
            Log.e(TAG, "Unable to decode resource")
        }
    }

    fun buildCarousel(message: Message?) {
        this.message = message
        var isImagesInCarous = false
        var numberofImages = 0
        if (carouselItems != null && carouselItems!!.size > 0) {
            for (item in carouselItems!!) {
                if (!TextUtils.isEmpty(item.photoUrl)) {
                    isImagesInCarous = true
                    numberofImages++
                }
            }
            if (isImagesInCarous) {
                val carouselImageDownloaderManager = CarouselImageDownloaderManager(
                    context,
                    carouselItems,
                    numberofImages,
                    object : CarouselImageDownloaderManager.OnDownloadsCompletedListener {
                        override fun onComplete() {
                            initiateCarouselTransaction()
                        }
                    })
                carouselImageDownloaderManager.startAllDownloads()
            } else {
                isImagesInCarousel = false
                initiateCarouselTransaction()
            }
        }
    }

    private fun initiateCarouselTransaction() {
        currentStartIndex = 0
        if (carouselItems != null && carouselItems!!.size > 0) {
            if (carouselItems!!.size == 1) {
                prepareVariablesForCarouselAndShow(carouselItems!![currentStartIndex], null)
            } else {
                prepareVariablesForCarouselAndShow(
                    carouselItems!![currentStartIndex],
                    carouselItems!![currentStartIndex + 1]
                )
            }
        }
    }

    private fun prepareVariablesForCarouselAndShow(
        leftItem: CarouselItem?,
        rightItem: CarouselItem?
    ) {
        if (this.leftItem == null) {
            this.leftItem = CarouselItem()
        }
        if (this.rightItem == null) {
            this.rightItem = CarouselItem()
        }
        if (leftItem != null) {
            this.leftItem = leftItem
            leftItemTitle = leftItem.title
            leftItemDescription = leftItem.description
            leftItemBitmap = getCarouselBitmap(leftItem)
        }
        if (rightItem != null) {
            this.rightItem = rightItem
            rightItemTitle = rightItem.title
            rightItemDescription = rightItem.description
            rightItemBitmap = getCarouselBitmap(rightItem)
        }
        showCarousel()
    }

    private fun showCarousel() {
        if (carouselItems != null && carouselItems!!.size > 0) {
            if (carousel == null || carousel!!.carouselNotificationId != carouselNotificationId) {
                //First save this set up into a carousel setup item
                carousel = saveCarouselSetUp()
            } else {
                carousel!!.currentStartIndex = currentStartIndex
                carousel!!.leftItem = leftItem
                carousel!!.rightItem = rightItem
            }
            setUpCarouselIcons()
            setUpCarouselTitles()
            val bigView = RemoteViews(
                context.applicationContext.packageName,
                R.layout.carousel_notification_item
            )
            setUpCarouselVisibilities(bigView)
            setUpCarouselItems(bigView)
            setPendingIntents(bigView)
            val mNotifyManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotifyManager != null) {
                PushNotificationManager.createNotificationChannel(
                    mNotifyManager, message!!.sound,
                    context
                )
            }
            val pushNotificationManager = PushNotificationManager()
            mBuilder = pushNotificationManager.createNotificationBuilder(
                context,
                contentTitle,
                contentText,
                message,
                carouselNotificationId
            )

            // TODO : Check the number of buttons and related
            // pending intents here when BE gets ready and
            // set them accordingly.
            /*
            mBuilder.addAction(R.drawable.notification_button, "Open" , contentIntent);
            */
            val foregroundNote = mBuilder.build()
            foregroundNote.bigContentView = bigView
            mNotifyManager?.notify(carouselNotificationId, foregroundNote)
        } else {
            Log.e(TAG, "Empty item array or of length less than 2")
        }
    }

    private fun getCarouselBitmap(item: CarouselItem?): Bitmap? {
        var bitmap: Bitmap? = null
        if (item != null) {
            if (!TextUtils.isEmpty(item.imageFileName) && !TextUtils.isEmpty(item.imageFileLocation)) {
                bitmap = ImageUtils.loadImageFromStorage(
                    context,
                    item.imageFileLocation,
                    item.imageFileName!!
                )
                if (bitmap != null) return bitmap
            }
            if (caraousalPlaceholder != null) return caraousalPlaceholder else if (appIcon != null) return appIcon
        }
        return bitmap
    }

    private fun setUpCarouselVisibilities(bigView: RemoteViews) {
        if (carouselItems!!.size < 3) {
            bigView.setViewVisibility(R.id.iv_arrow_left, View.GONE)
            bigView.setViewVisibility(R.id.iv_arrow_right, View.GONE)
        } else {
            bigView.setViewVisibility(R.id.iv_arrow_left, View.VISIBLE)
            bigView.setViewVisibility(R.id.iv_arrow_right, View.VISIBLE)
        }
        if (carouselItems!!.size < 2) {
            bigView.setViewVisibility(R.id.ll_right_item_layout, View.GONE)
        } else {
            bigView.setViewVisibility(R.id.ll_right_item_layout, View.VISIBLE)
        }
        if (TextUtils.isEmpty(bigContentText)) {
            bigView.setViewVisibility(R.id.tv_carousel_content, View.GONE)
        } else {
            bigView.setViewVisibility(R.id.tv_carousel_content, View.VISIBLE)
        }
        if (TextUtils.isEmpty(bigContentTitle)) {
            bigView.setViewVisibility(R.id.tv_carousel_title, View.GONE)
        } else {
            bigView.setViewVisibility(R.id.tv_carousel_title, View.VISIBLE)
        }
        if (TextUtils.isEmpty(leftItemTitle)) {
            bigView.setViewVisibility(R.id.tv_left_title_text, View.GONE)
        } else {
            bigView.setViewVisibility(R.id.tv_left_title_text, View.VISIBLE)
        }
        if (TextUtils.isEmpty(leftItemDescription)) {
            bigView.setViewVisibility(R.id.tv_left_description_text, View.GONE)
        } else {
            bigView.setViewVisibility(R.id.tv_left_description_text, View.VISIBLE)
        }
        if (TextUtils.isEmpty(rightItemTitle)) {
            bigView.setViewVisibility(R.id.tv_right_title_text, View.GONE)
        } else {
            bigView.setViewVisibility(R.id.tv_right_title_text, View.VISIBLE)
        }
        if (TextUtils.isEmpty(rightItemDescription)) {
            bigView.setViewVisibility(R.id.tv_right_description_text, View.GONE)
        } else {
            bigView.setViewVisibility(R.id.tv_right_description_text, View.VISIBLE)
        }
        if (!isImagesInCarousel) {
            bigView.setViewVisibility(R.id.iv_image_left, View.GONE)
            bigView.setViewVisibility(R.id.iv_image_right, View.GONE)
        } else {
            bigView.setViewVisibility(R.id.iv_image_left, View.VISIBLE)
            bigView.setViewVisibility(R.id.iv_image_right, View.VISIBLE)
        }
    }

    private fun setUpCarouselTitles() {
        if (TextUtils.isEmpty(contentTitle)) {
            setContentTitle("")
        }
        if (bigContentTitle == null) bigContentTitle = ""
        if (bigContentText == null) bigContentText = ""
    }

    private fun setUpCarouselIcons() {
        if (appIcon != null) {
            if (largeIcon == null) {
                largeIcon = appIcon
            }
            if (caraousalPlaceholder == null) {
                caraousalPlaceholder = appIcon
            }
        } else {
            appIcon = BitmapFactory.decodeResource(
                context.resources, ImageUtils.getAppIcon(
                    context
                )
            )
            if (largeIcon == null) {
                largeIcon = appIcon
            }
            if (caraousalPlaceholder == null) {
                caraousalPlaceholder = appIcon
            }
        }
        if (smallIconResourceId < 0) {
            smallIconResourceId = ImageUtils.getAppIcon(context)
        }
        if (smallIconResourceId < 0) {
            smallIconResourceId = R.drawable.ic_carousel_icon
        }
    }

    private fun setUpCarouselItems(bigView: RemoteViews) {
        if (leftItemBitmap != null) {
            bigView.setImageViewBitmap(R.id.iv_image_left, leftItemBitmap)
        }
        if (rightItemBitmap != null) {
            bigView.setImageViewBitmap(R.id.iv_image_right, rightItemBitmap)
        }
        bigView.setImageViewBitmap(R.id.iv_carousel_app_icon, largeIcon)
        bigView.setTextViewText(R.id.tv_carousel_title, bigContentTitle)
        bigView.setTextViewText(R.id.tv_carousel_content, bigContentText)
        bigView.setTextViewText(R.id.tv_right_title_text, rightItemTitle)
        bigView.setTextViewText(R.id.tv_right_description_text, rightItemDescription)
        bigView.setTextViewText(R.id.tv_left_title_text, leftItemTitle)
        bigView.setTextViewText(R.id.tv_left_description_text, leftItemDescription)
    }

    private fun setPendingIntents(bigView: RemoteViews) {
        //right arrow
        val rightArrowPendingIntent = getPendingIntent(Constants.EVENT_RIGHT_ARROW_CLICKED)
        bigView.setOnClickPendingIntent(R.id.iv_arrow_right, rightArrowPendingIntent)
        //left arrow
        val leftArrowPendingIntent = getPendingIntent(Constants.EVENT_LEFT_ARROW_CLICKED)
        bigView.setOnClickPendingIntent(R.id.iv_arrow_left, leftArrowPendingIntent)
        //right item
        val rightItemPendingIntent = getPendingIntent(Constants.EVENT_RIGHT_ITEM_CLICKED)
        bigView.setOnClickPendingIntent(R.id.ll_right_item_layout, rightItemPendingIntent)
        //left item
        val leftItemPendingIntent = getPendingIntent(Constants.EVENT_LEFT_ITEM_CLICKED)
        bigView.setOnClickPendingIntent(R.id.ll_left_item_layout, leftItemPendingIntent)
    }

    private fun getPendingIntent(eventClicked: Int): PendingIntent {
        val carouselIntent = Intent(context, CarouselEventReceiver::class.java)
        val bundle = Bundle()
        bundle.putInt(Constants.NOTIFICATION_ID, carouselNotificationId)
        bundle.putInt(Constants.EVENT_CAROUSAL_ITEM_CLICKED_KEY, eventClicked)
        bundle.putParcelable(Constants.CAROUSAL_SET_UP_KEY, carousel)
        bundle.putSerializable("message", message)
        carouselIntent.putExtras(bundle)
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            PendingIntent.getBroadcast(
                context,
                eventClicked,
                carouselIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                eventClicked,
                carouselIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    private fun saveCarouselSetUp(): Carousel {
        setUpfilePathOfImages()
        return Carousel(
            carouselItems, contentTitle, contentText,
            bigContentTitle, bigContentText, carouselNotificationId,
            currentStartIndex, smallIconPath, smallIconResourceId, largeIconPath,
            placeHolderImagePath, leftItem, rightItem, isImagesInCarousel
        )
    }

    private fun setUpfilePathOfImages() {
        if (smallIcon != null) {
            smallIconPath = ImageUtils.saveBitmapToInternalStorage(
                context, smallIcon!!,
                Constants.CAROUSAL_SMALL_ICON_FILE_NAME
            )
        }
        if (largeIcon != null) {
            largeIconPath = ImageUtils.saveBitmapToInternalStorage(
                context, largeIcon!!,
                Constants.CAROUSAL_LARGE_ICON_FILE_NAME
            )
        }
        if (caraousalPlaceholder != null) {
            placeHolderImagePath = ImageUtils.saveBitmapToInternalStorage(
                context, caraousalPlaceholder!!,
                Constants.CAROUSAL_PLACEHOLDER_ICON_FILE_NAME
            )
        }
    }

    private fun clearCarouselIfExists() {
        if (carouselItems != null) {
            /*for (CarouselItem cr : carouselItems) {
                if (cr.getImageFileName() != null)
                if(context.deleteFile(cr.getImageFileName()))
                    Log.i(TAG, "Image deleted.");
            }*/
            carouselItems!!.clear()
            smallIconResourceId = -1
            isImagesInCarousel = true
            smallIcon = null
            smallIconPath = null
            largeIcon = null
            placeHolderImagePath = null
            caraousalPlaceholder = null
            contentText = null
            contentTitle = null
            bigContentText = null
            bigContentTitle = null
            val mNotifyManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            mNotifyManager?.cancel(carouselNotificationId)
        }
        //ToDo :  delete all cache files
    }

    fun handleClickEvent(clickEvent: Int, setUp: Carousel) {
        verifyAndSetUpVariables(setUp)
        when (clickEvent) {
            Constants.EVENT_LEFT_ARROW_CLICKED -> onLeftArrowClicked()
            Constants.EVENT_RIGHT_ARROW_CLICKED -> onRightArrowClicked()
            Constants.EVENT_LEFT_ITEM_CLICKED -> onLeftItemClicked()
            Constants.EVENT_RIGHT_ITEM_CLICKED -> onRightItemClicked()
            else -> {}
        }
    }

    private fun verifyAndSetUpVariables(setUp: Carousel) {
        if (carousel == null) {
            carouselItems = setUp.carouselItems
            contentTitle = setUp.contentTitle
            contentText = setUp.contentText
            bigContentTitle = setUp.bigContentTitle
            bigContentText = setUp.bigContentText
            carouselNotificationId = setUp.carouselNotificationId
            currentStartIndex = setUp.currentStartIndex
            smallIconPath = setUp.smallIcon
            largeIconPath = setUp.largeIcon
            placeHolderImagePath = setUp.caraousalPlaceholder
            leftItem = setUp.leftItem
            rightItem = setUp.rightItem
            isImagesInCarousel = setUp.isImagesInCarousel
            setUpBitCarouselBitmapsFromSetUp()
        } else if (carousel != null && carouselNotificationId != setUp.carouselNotificationId) {
            carousel = null
            verifyAndSetUpVariables(setUp)
        }
    }

    /**
     * If exists it loads bitmaps from file directory and saves them.
     */
    private fun setUpBitCarouselBitmapsFromSetUp() {
        if (smallIconPath != null) {
            smallIcon = ImageUtils.loadImageFromStorage(
                context,
                smallIconPath,
                Constants.CAROUSAL_SMALL_ICON_FILE_NAME
            )
        }
        if (largeIconPath != null) {
            largeIcon = ImageUtils.loadImageFromStorage(
                context,
                largeIconPath,
                Constants.CAROUSAL_LARGE_ICON_FILE_NAME
            )
        }
        if (placeHolderImagePath != null) {
            caraousalPlaceholder = ImageUtils.loadImageFromStorage(
                context,
                placeHolderImagePath,
                Constants.CAROUSAL_PLACEHOLDER_ICON_FILE_NAME
            )
        }
    }

    private fun onRightItemClicked() {
        sendItemClickedBroadcast(rightItem)
    }

    private fun onLeftItemClicked() {
        sendItemClickedBroadcast(leftItem)
    }

    private fun sendItemClickedBroadcast(cItem: CarouselItem?) {
        var intent: Intent
        val bundle = Bundle()
        bundle.putParcelable(Constants.CAROUSAL_ITEM_CLICKED_KEY, cItem)
        bundle.putString(
            Constants.CAROUSEL_ITEM_CLICKED_URL,
            message!!.getElements()!![cItem!!.id!!.toInt() - 1].url
        )
        val intentStr: String =
            SharedPref.readString(context.applicationContext, Constants.INTENT_NAME)
        if (intentStr.isNotEmpty()) {
            try {
                intent = Intent(context.applicationContext, Class.forName(intentStr))
                intent.putExtras(bundle)
            } catch (e: Exception) {
                val element = Throwable().stackTrace[0]
                LogUtils.formGraylogModel(
                    context,
                    "e",
                    "Navigating to the activity of the customer : " + e.message,
                    element.className + "/" + element.methodName + "/" + element.lineNumber
                )
                Log.e("PushClick : ", "The class could not be found!")
                val packageManager = context.packageManager
                intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
                val componentName = intent.component
                val notificationIntent = Intent.makeRestartActivityTask(componentName)
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                notificationIntent.putExtras(bundle)
            }
        } else {
            val packageManager = context.packageManager
            intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
            val componentName = intent.component
            val notificationIntent = Intent.makeRestartActivityTask(componentName)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            notificationIntent.putExtras(bundle)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.applicationContext.startActivity(intent)
        try {
            clearCarouselIfExists()
        } catch (e: Exception) {
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                context,
                "e",
                "Clearing carousel : " + e.message,
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            e.printStackTrace()
            Log.e(TAG, "Unable To send notification's pendingIntent")
        }
    }

    private fun onLeftArrowClicked() {
        if (carouselItems != null && carouselItems!!.size > currentStartIndex) {
            when (currentStartIndex) {
                1 -> {
                    currentStartIndex = carouselItems!!.size - 1
                    prepareVariablesForCarouselAndShow(
                        carouselItems!![currentStartIndex],
                        carouselItems!![0]
                    )
                }
                0 -> {
                    currentStartIndex = carouselItems!!.size - 2
                    prepareVariablesForCarouselAndShow(
                        carouselItems!![currentStartIndex],
                        carouselItems!![currentStartIndex + 1]
                    )
                }
                else -> {
                    currentStartIndex -= 2
                    prepareVariablesForCarouselAndShow(
                        carouselItems!![currentStartIndex],
                        carouselItems!![currentStartIndex + 1]
                    )
                }
            }
        }
    }

    private fun onRightArrowClicked() {
        if (carouselItems != null && carouselItems!!.size > currentStartIndex) {
            val difference = carouselItems!!.size - currentStartIndex
            when (difference) {
                3 -> {
                    currentStartIndex += 2
                    prepareVariablesForCarouselAndShow(
                        carouselItems!![currentStartIndex],
                        carouselItems!![0]
                    )
                }
                2 -> {
                    currentStartIndex = 0
                    prepareVariablesForCarouselAndShow(carouselItems!![0], carouselItems!![1])
                }
                1 -> {
                    currentStartIndex = 1
                    prepareVariablesForCarouselAndShow(
                        carouselItems!![currentStartIndex],
                        carouselItems!![currentStartIndex + 1]
                    )
                }
                else -> {
                    currentStartIndex += 2
                    prepareVariablesForCarouselAndShow(
                        carouselItems!![currentStartIndex],
                        carouselItems!![currentStartIndex + 1]
                    )
                }
            }
        }
    }

    companion object {
        private var carouselBuilder: CarouselBuilder? = null
        private const val TAG = "Carousel"
        private var currentStartIndex = 0 //Variable that keeps track of where the startIndex is
        private var appIcon: Bitmap? = null
        private var smallIcon: Bitmap? = null
        private var smallIconResourceId = -1 //check before setting it that it does exists
        private var largeIcon: Bitmap? = null
        private var caraousalPlaceholder: Bitmap? = null
        fun with(context: Context, notificationId: Int): CarouselBuilder? {
            if (carouselBuilder == null) {
                synchronized(CarouselBuilder::class.java) {
                    if (carouselBuilder == null) {
                        carouselBuilder =
                            CarouselBuilder(context, notificationId)
                        try {
                            appIcon = ImageUtils.drawableToBitmap(
                                context.packageManager
                                    .getApplicationIcon(context.packageName)
                            )
                        } catch (e: PackageManager.NameNotFoundException) {
                            val element =
                                Throwable().stackTrace[0]
                            LogUtils.formGraylogModel(
                                context,
                                "e",
                                "Getting carousel app icon bitmap : " + e.message,
                                element.className + "/" + element.methodName + "/" + element.lineNumber
                            )
                            appIcon = null
                            Log.e(
                                TAG,
                                "Unable to retrieve app Icon"
                            )
                        }
                    }
                }
            }
            return carouselBuilder
        }
    }

    init {
        carouselNotificationId = notificationId
        mBuilder = NotificationCompat.Builder(
            context, AppUtils.getNotificationChannelId(
                context, false
            )
        )
    }
}