package com.relateddigital.relateddigital_android_core.model

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.google.gson.annotations.SerializedName
import com.relateddigital.relateddigital_android_core.inapp.FontFamily
import com.relateddigital.relateddigital_android_core.util.AppUtils
import java.io.Serializable
import java.util.*

class ScratchToWinExtendedProps : Serializable {
    @SerializedName("content_title_text_color")
    var contentTitleTextColor: String? = null

    @SerializedName("content_title_font_family")
    private var content_title_font_family: String? = null

    @SerializedName("content_title_custom_font_family_ios")
    private val content_title_custom_font_family_ios: String? = null

    @SerializedName("content_title_custom_font_family_android")
    private val content_title_custom_font_family_android: String? = null

    @SerializedName("content_title_text_size")
    var contentTitleTextSize: String? = null

    @SerializedName("content_body_text_color")
    var contentBodyTextColor: String? = null

    @SerializedName("content_body_font_family")
    private var content_body_font_family: String? = null

    @SerializedName("content_body_custom_font_family_ios")
    private val content_body_custom_font_family_ios: String? = null

    @SerializedName("content_body_custom_font_family_android")
    private val content_body_custom_font_family_android: String? = null

    @SerializedName("content_body_text_size")
    var contentBodyTextSize: String? = null

    @SerializedName("button_color")
    var buttonColor: String? = null

    @SerializedName("button_text_color")
    var buttonTextColor: String? = null

    @SerializedName("button_font_family")
    private var button_font_family: String? = null

    @SerializedName("button_custom_font_family_ios")
    private val button_custom_font_family_ios: String? = null

    @SerializedName("button_custom_font_family_android")
    private val button_custom_font_family_android: String? = null

    @SerializedName("button_text_size")
    var buttonTextSize: String? = null

    @SerializedName("promocode_text_color")
    var promoCodeTextColor: String? = null

    @SerializedName("promocode_font_family")
    private var promocode_font_family: String? = null

    @SerializedName("promocode_custom_font_family_ios")
    private val promocode_custom_font_family_ios: String? = null

    @SerializedName("promocode_custom_font_family_android")
    private val promocode_custom_font_family_android: String? = null

    @SerializedName("promocode_text_size")
    var promoCodeTextSize: String? = null

    @SerializedName("copybutton_color")
    var copyButtonColor: String? = null

    @SerializedName("copybutton_text_color")
    var copyButtonTextColor: String? = null

    @SerializedName("copybutton_font_family")
    private var copybutton_font_family: String? = null

    @SerializedName("copybutton_custom_font_family_ios")
    private val copybutton_custom_font_family_ios: String? = null

    @SerializedName("copybutton_custom_font_family_android")
    private val copybutton_custom_font_family_android: String? = null

    @SerializedName("copybutton_text_size")
    var copyButtonTextSize: String? = null

    @SerializedName("emailpermit_text_size")
    var emailPermitTextSize: String? = null

    @SerializedName("emailpermit_text_url")
    var emailPermitTextUrl: String? = null

    @SerializedName("consent_text_size")
    var consentTextSize: String? = null

    @SerializedName("consent_text_url")
    var consentTextUrl: String? = null

    @SerializedName("close_button_color")
    var closeButtonColor: String? = null

    @SerializedName("background_color")
    var backgroundColor: String? = null

    fun setContentTitleFontFamily(contentTitleFontFamily: String?) {
        content_title_font_family = contentTitleFontFamily
    }

    fun getContentTitleFontFamily(context: Context): Typeface? {
        if (content_title_font_family.isNullOrEmpty()) {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == content_title_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == content_title_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == content_title_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (!content_title_custom_font_family_android.isNullOrEmpty()) {
            if (AppUtils.isFontResourceAvailable(context, content_title_custom_font_family_android)) {
                val id = context.resources.getIdentifier(
                    content_title_custom_font_family_android,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }

    fun setContentBodyFontFamily(contentBodyFontFamily: String?) {
        content_body_font_family = contentBodyFontFamily
    }

    fun getContentBodyFontFamily(context: Context): Typeface? {
        if (content_body_font_family.isNullOrEmpty()) {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == content_body_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == content_body_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == content_body_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (!content_body_custom_font_family_android.isNullOrEmpty()) {
            if (AppUtils.isFontResourceAvailable(context, content_body_custom_font_family_android)) {
                val id = context.resources.getIdentifier(
                    content_body_custom_font_family_android,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }

    fun setButtonFontFamily(buttonFontFamily: String?) {
        button_font_family = buttonFontFamily
    }

    fun getButtonFontFamily(context: Context): Typeface? {
        if (button_font_family.isNullOrEmpty()) {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == button_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == button_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == button_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (!button_custom_font_family_android.isNullOrEmpty()) {
            if (AppUtils.isFontResourceAvailable(context, button_custom_font_family_android)) {
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

    fun setPromoCodeFontFamily(promoCodeFontFamily: String?) {
        promocode_font_family = promoCodeFontFamily
    }

    fun getPromoCodeFontFamily(context: Context): Typeface? {
        if (promocode_font_family.isNullOrEmpty()) {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == promocode_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == promocode_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == promocode_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (!promocode_custom_font_family_android.isNullOrEmpty()) {
            if (AppUtils.isFontResourceAvailable(context, promocode_custom_font_family_android)) {
                val id = context.resources.getIdentifier(
                    promocode_custom_font_family_android,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }

    fun setCopyButtonFontFamily(copyButtonFontFamily: String?) {
        copybutton_font_family = copyButtonFontFamily
    }

    fun getCopyButtonFontFamily(context: Context): Typeface? {
        if (copybutton_font_family.isNullOrEmpty()) {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == copybutton_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == copybutton_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == copybutton_font_family!!.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (!copybutton_custom_font_family_android.isNullOrEmpty()) {
            if (AppUtils.isFontResourceAvailable(context, copybutton_custom_font_family_android)) {
                val id = context.resources.getIdentifier(
                    copybutton_custom_font_family_android,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }
}