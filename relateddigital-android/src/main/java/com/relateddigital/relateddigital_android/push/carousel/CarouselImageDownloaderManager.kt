package com.relateddigital.relateddigital_android.push.carousel

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.CarouselItem
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.ImageUtils
import java.util.ArrayList

class CarouselImageDownloaderManager(
    context: Context, carouselItems: ArrayList<CarouselItem>?, numberOfImages: Int,
    onDownloadsCompletedListener: OnDownloadsCompletedListener
) {
    private val TAG = this.javaClass.simpleName
    private val context: Context
    private val carouselItems: ArrayList<CarouselItem>?
    private val onDownloadsCompletedListener: OnDownloadsCompletedListener
    private var numberOfImages: Int
    private var currentItem: CarouselItem? = null
    private val mImageLoaderListener: OnImageLoaderListener = object : OnImageLoaderListener {
        override fun onError(error: ImageError?) {
            updateDownLoad()
        }

        override fun onComplete(resultPath: String?) {
            updateDownLoad()
        }
    }

    private fun updateDownLoad() {
        for (i in currentDownloadTaskIndex + 1 until carouselItems!!.size) {
            if (!TextUtils.isEmpty(carouselItems[i].photoUrl)) {
                currentDownloadTaskIndex = i
                currentItem = carouselItems[i]
                downloadImage(currentItem!!.photoUrl!!)
                break
            }
        }
        --numberOfImages
        if (numberOfImages < 1 || currentDownloadTaskIndex > carouselItems.size - 1) {
            onDownloadsCompletedListener.onComplete()
        }
    }

    fun startAllDownloads() {
        if (carouselItems != null && carouselItems.size > 0) {
            for (i in carouselItems.indices) {
                if (!TextUtils.isEmpty(carouselItems[i].photoUrl)) {
                    currentDownloadTaskIndex = i
                    currentItem = carouselItems[i]
                    downloadImage(currentItem!!.photoUrl!!)
                    break
                }
            }
        }
    }

    interface OnImageLoaderListener {
        fun onError(error: ImageError?)
        fun onComplete(resultPath: String?)
    }

    interface OnDownloadsCompletedListener {
        fun onComplete()
    }

    private fun downloadImage(imageUrl: String) {
        val runnable = Runnable {
            var imagePath: String? = null
            val currentTimeInMillis = System.currentTimeMillis()
            val bitmap: Bitmap? = AppUtils.getBitMapFromUri(context, imageUrl)
            if (bitmap != null) {
                val sampleSize: Int =
                    ImageUtils.calculateInSampleSize(bitmap.width, bitmap.height, 250, 250)
                val bit = Bitmap.createScaledBitmap(
                    bitmap,
                    bitmap.width / sampleSize,
                    bitmap.height / sampleSize,
                    false
                )
                imagePath = ImageUtils.saveBitmapToInternalStorage(
                    context,
                    bit,
                    Constants.CAROUSAL_IMAGE_BEGENNING + currentTimeInMillis
                )
            }
            if (imagePath == null) {
                Log.e(TAG, "factory returned a null result")
                mImageLoaderListener.onError(
                    ImageError("downloaded file could not be decoded as bitmap")
                        .setErrorCode(ImageError.ERROR_DECODE_FAILED)
                )
            } else {
                Log.d(TAG, "download complete")
                if (currentItem != null) {
                    currentItem!!.imageFileLocation = imagePath
                    currentItem!!.imageFileName = Constants.CAROUSAL_IMAGE_BEGENNING + currentTimeInMillis
                }
                mImageLoaderListener.onComplete(imagePath)
            }
            System.gc()
        }
        val thread = Thread(runnable)
        thread.start()
    }

    class ImageError : Throwable {
        var errorCode = 0
            private set

        internal constructor(message: String) : super(message) {}
        internal constructor(error: Throwable) : super(error.message, error.cause) {
            stackTrace = error.stackTrace
        }

        fun setErrorCode(code: Int): ImageError {
            errorCode = code
            return this
        }

        companion object {
            const val ERROR_GENERAL_EXCEPTION = -1
            const val ERROR_INVALID_FILE = 0
            const val ERROR_DECODE_FAILED = 1
        }
    }

    companion object {
        private var currentDownloadTaskIndex = 0
    }

    init {
        this.carouselItems = carouselItems
        this.context = context
        this.onDownloadsCompletedListener = onDownloadsCompletedListener
        this.numberOfImages = numberOfImages
    }
}