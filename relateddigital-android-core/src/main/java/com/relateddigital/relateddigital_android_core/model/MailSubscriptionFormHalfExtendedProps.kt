package com.relateddigital.relateddigital_android_core.model

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.relateddigital.relateddigital_android_core.inapp.FontFamily
import com.relateddigital.relateddigital_android_core.util.AppUtils
import java.io.Serializable
import java.util.*

class MailSubscriptionFormHalfExtendedProps : Serializable {

    private val image_position: String? = null

    private val title_text_color: String? = null

    private val title_font_family: String? = null

    private val title_custom_font_family_ios: String? = null

    private val title_custom_font_family_android: String? = null

    private val title_text_size: String? = null

    private val text_position: String? = null

    private val text_color: String? = null

    private val text_font_family: String? = null

    private val text_custom_font_family_ios: String? = null

    private val text_custom_font_family_android: String? = null

    private val text_size: String? = null

    private val button_color: String? = null

    private val button_text_color: String? = null

    private val button_font_family: String? = null

    private val button_custom_font_family_ios: String? = null

    private val button_custom_font_family_android: String? = null

    private val button_text_size: String? = null

    private val emailpermit_text_size: String? = null

    private val emailpermit_text_url: String? = null

    private val consent_text_size: String? = null

    private val consent_text_url: String? = null

    private val close_button_color: String? = null

    private val background_color: String? = null

    fun getImagePosition(): String? {
        return image_position
    }

    fun getTitleTextColor(): String? {
        return title_text_color
    }

    fun getTitleFontFamily(context: Context): Typeface? {
        if (title_font_family == null || title_font_family == "") {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == title_font_family.lowercase(Locale.ROOT)) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == title_font_family.lowercase(Locale.ROOT)) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == title_font_family.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (title_custom_font_family_android != null && title_custom_font_family_android.isNotEmpty()) {
            if (AppUtils.isFontResourceAvailable(
                    context,
                    title_custom_font_family_android
                )
            ) {
                val id = context.resources.getIdentifier(
                    title_custom_font_family_android,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }

    fun getTitleTextSize(): String? {
        return title_text_size
    }

    fun getTextPosition(): String? {
        return text_position
    }

    fun getTextColor(): String? {
        return text_color
    }

    fun getTextFontFamily(context: Context): Typeface? {
        if (text_font_family == null || text_font_family == "") {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == text_font_family.lowercase(Locale.ROOT)) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == text_font_family.lowercase(Locale.ROOT)) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == text_font_family.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (text_custom_font_family_android != null && text_custom_font_family_android.isNotEmpty()) {
            if (AppUtils.isFontResourceAvailable(
                    context,
                    text_custom_font_family_android
                )
            ) {
                val id = context.resources.getIdentifier(
                    text_custom_font_family_android,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }

    fun getTextSize(): String? {
        return text_size
    }

    fun getButtonColor(): String? {
        return button_color
    }

    fun getButtonTextColor(): String? {
        return button_text_color
    }

    fun getButtonFontFamily(context: Context): Typeface? {
        if (button_font_family == null || button_font_family == "") {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == button_font_family.lowercase(Locale.ROOT)) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == button_font_family.lowercase(Locale.ROOT)) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == button_font_family.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (button_custom_font_family_android != null && button_custom_font_family_android.isNotEmpty()) {
            if (AppUtils.isFontResourceAvailable(
                    context,
                    button_custom_font_family_android
                )
            ) {
                val id = context.resources.getIdentifier(
                    button_custom_font_family_android,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }

    fun getButtonTextSize(): String? {
        return button_text_size
    }

    fun getEmailPermitTextSize(): String? {
        return emailpermit_text_size
    }

    fun getEmailPermitTextUrl(): String? {
        return emailpermit_text_url
    }

    fun getConsentTextSize(): String? {
        return consent_text_size
    }

    fun getConsentTextUrl(): String? {
        return consent_text_url
    }

    fun getCloseButtonColor(): String? {
        return close_button_color
    }

    fun getBackgroundColor(): String? {
        return background_color
    }
}