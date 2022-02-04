package com.relateddigital.relateddigital_android.model

import android.content.Context
import java.io.Serializable
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.relateddigital.relateddigital_android.inapp.FontFamily
import com.relateddigital.relateddigital_android.util.AppUtils.isFontResourceAvailable
import java.util.*


class ExtendedProps : Serializable {
    val title_text_color: String? = null
    val title_font_family: String? = null
    val title_custom_font_family_ios: String? = null
    val title_custom_font_family_android: String? = null
    val title_text_size: String? = null
    val text_color: String? = null
    val text_font_family: String? = null
    val text_custom_font_family_ios: String? = null
    val text_custom_font_family_android: String? = null
    val text_size: String? = null
    val button_color: String? = null
    val button_text_color: String? = null
    val button_font_family: String? = null
    val button_custom_font_family_ios: String? = null
    val button_custom_font_family_android: String? = null
    val button_text_size: String? = null
    val emailpermit_text_size: String? = null
    val emailpermit_text_url: String? = null
    val consent_text_size: String? = null
    val consent_text_url: String? = null
    val close_button_color: String? = null
    val background_color: String? = null

    fun getTitleFontFamily(context: Context): Typeface? {
        if (title_font_family.isNullOrEmpty()) {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == title_font_family.lowercase(Locale.getDefault())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == title_font_family.lowercase(Locale.getDefault())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == title_font_family.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (!title_custom_font_family_android.isNullOrEmpty()) {
            if (isFontResourceAvailable(context, title_custom_font_family_android)) {
                val id: Int = context.resources.getIdentifier(
                    title_custom_font_family_android,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }

    fun getTextFontFamily(context: Context): Typeface? {
        if (text_font_family.isNullOrEmpty()) {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == text_font_family.lowercase(Locale.getDefault())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == text_font_family.lowercase(Locale.getDefault())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == text_font_family.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (!text_custom_font_family_android.isNullOrEmpty()) {
            if (isFontResourceAvailable(context, text_custom_font_family_android)) {
                val id: Int = context.resources.getIdentifier(
                    text_custom_font_family_android,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }

    fun getButtonFontFamily(context: Context): Typeface? {
        if (button_font_family.isNullOrEmpty()) {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == button_font_family.lowercase(Locale.getDefault())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == button_font_family.lowercase(Locale.getDefault())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == button_font_family.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (!button_custom_font_family_android.isNullOrEmpty()) {
            if (isFontResourceAvailable(context, button_custom_font_family_android)) {
                val id: Int = context.resources.getIdentifier(
                    button_custom_font_family_android,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }

}