package com.relateddigital.relateddigital_android.model

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.relateddigital.relateddigital_android.inapp.FontFamily
import com.relateddigital.relateddigital_android.util.AppUtils
import java.io.Serializable

class DrawerExtendedProps : Serializable {

    private val content_minimized_text_size: String? = null

    private val content_minimized_text_color: String? = null

    private val content_minimized_font_family: String? = null

    private val content_minimized_custom_font_family_ios: String? = null

    private val content_minimized_custom_font_family_android: String? = null

    private val content_minimized_text_orientation: String? = null

    private val content_minimized_background_image: String? = null

    private val content_minimized_background_color: String? = null

    private val content_minimized_arrow_color: String? = null

    private val content_maximized_background_image: String? = null

    private val content_maximized_background_color: String? = null

    fun getMiniTextSize(): String? {
        return content_minimized_text_size
    }

    fun getMiniTextColor(): String? {
        return content_minimized_text_color
    }

    fun getMiniFontFamily(context: Context): Typeface? {
        if (content_minimized_font_family == null || content_minimized_font_family == "") {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString().equals(content_minimized_font_family.toLowerCase())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString().equals(content_minimized_font_family.toLowerCase())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString().equals(content_minimized_font_family.toLowerCase())) {
            return Typeface.SERIF
        }
        if (content_minimized_custom_font_family_android != null && !content_minimized_custom_font_family_android.isEmpty()) {
            if (AppUtils.isFontResourceAvailable(
                    context,
                    content_minimized_custom_font_family_android
                )
            ) {
                val id = context.resources.getIdentifier(
                    content_minimized_custom_font_family_android,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }

    fun getMiniTextOrientation(): String? {
        return content_minimized_text_orientation
    }

    fun getMiniBackgroundImage(): String? {
        return content_minimized_background_image
    }

    fun getMiniBackgroundColor(): String? {
        return content_minimized_background_color
    }

    fun getArrowColor(): String? {
        return content_minimized_arrow_color
    }

    fun getMaxiBackgroundImage(): String? {
        return content_maximized_background_image
    }

    fun getMaxiBackgroundColor(): String? {
        return content_maximized_background_color
    }
}