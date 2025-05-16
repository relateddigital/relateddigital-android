package com.relateddigital.relateddigital_android.util

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import java.io.*
import java.lang.Exception

object ImageUtils {
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            val bitmapDrawable: BitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun saveBitmapToInternalStorage(
        context: Context,
        bitmapImage: Bitmap,
        fileName: String
    ): String? {
        var fileSaved = false
        val cw = ContextWrapper(context.applicationContext)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "$fileName.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fileSaved = true
        } catch (e: Exception) {
            Log.e("SaveBitMap", "Error writing bitmap")
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                Log.e("SaveBitMap", "Error closing output stream")

                e.printStackTrace()
            }
        }
        return if (fileSaved) directory.absolutePath else null
    }

    fun loadImageFromStorage(context: Context?, path: String?, fileName: String): Bitmap? {
        var b: Bitmap? = null
        try {
            val f = File(path, "$fileName.jpg")
            b = BitmapFactory.decodeStream(FileInputStream(f))
        } catch (e: FileNotFoundException) {
            Log.e("Loading image", "File not found")

            e.printStackTrace()
        }
        return b
    }

    fun calculateInSampleSize(
        width: Int, height: Int, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight
                && halfWidth / inSampleSize >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun getAppIcon(context: Context): Int {
        var appIconResId = 0
        val packageManager = context.packageManager
        val applicationInfo: ApplicationInfo
        try {
            applicationInfo =
                packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            appIconResId = applicationInfo.icon
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("AppIcon", "Getting app icon : " + e.message)

            e.printStackTrace()
        }
        return appIconResId
    }

    @ColorInt
    fun getThemeColor(
        context: Context,
        @AttrRes attributeColor: Int
    ): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(attributeColor, value, true)
        return value.data
    }
}