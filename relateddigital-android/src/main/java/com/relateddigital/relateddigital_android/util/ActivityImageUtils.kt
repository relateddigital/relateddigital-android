package com.relateddigital.relateddigital_android.util

import android.R
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.View

object ActivityImageUtils {
    private const val LOGTAG = "ActivityImageUtils"
    // May return null.
    fun getScaledScreenshot(
        activity: Activity,
        scaleWidth: Int,
        scaleHeight: Int,
        relativeScaleIfTrue: Boolean
    ): Bitmap? {
        var scaleWidth = scaleWidth
        var scaleHeight = scaleHeight
        val someView = activity.findViewById<View>(R.id.content)
        val rootView = someView.rootView
        val originalCacheState = rootView.isDrawingCacheEnabled
        rootView.isDrawingCacheEnabled = true
        rootView.buildDrawingCache(true)

        // We could get a null or zero px bitmap if the rootView hasn't been measured
        // appropriately, or we grab it before layout.
        // This is ok, and we should handle it gracefully.
        val original = rootView.drawingCache
        var scaled: Bitmap? = null
        if (null != original && original.width > 0 && original.height > 0) {
            if (relativeScaleIfTrue) {
                scaleWidth = original.width / scaleWidth
                scaleHeight = original.height / scaleHeight
            }
            if (scaleWidth > 0 && scaleHeight > 0) {
                try {
                    scaled = Bitmap.createScaledBitmap(original, scaleWidth, scaleHeight, false)
                } catch (error: OutOfMemoryError) {
                    Log.i(
                        LOGTAG,
                        "Not enough memory to produce scaled image, returning a null screenshot"
                    )
                }
            }
        }
        if (!originalCacheState) {
            rootView.isDrawingCacheEnabled = false
        }
        return scaled
    }

    fun getHighlightColorFromBackground(activity: Activity): Int {
        var incolor = Color.BLACK
        val screenshot1px = getScaledScreenshot(activity, 1, 1, false)
        if (null != screenshot1px) {
            incolor = screenshot1px.getPixel(0, 0)
        }
        return getHighlightColor(incolor)
    }

    fun getHighlightColorFromBitmap(bitmap: Bitmap?): Int {
        var incolor = Color.BLACK
        if (null != bitmap) {
            val bitmap1px = Bitmap.createScaledBitmap(bitmap, 1, 1, false)
            incolor = bitmap1px.getPixel(0, 0)
        }
        return getHighlightColor(incolor)
    }

    fun getHighlightColor(sampleColor: Int): Int {
        // Set a constant value level in HSV, in case the averaged color is too light or too dark.
        val hsvBackground = FloatArray(3)
        Color.colorToHSV(sampleColor, hsvBackground)
        hsvBackground[2] = 0.3f // value parameter
        return Color.HSVToColor(0xf2, hsvBackground)
    }
}