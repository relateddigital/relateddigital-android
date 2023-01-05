package com.relateddigital.relateddigital_android.model

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.google.gson.annotations.SerializedName
import com.relateddigital.relateddigital_android.inapp.FontFamily
import com.relateddigital.relateddigital_android.util.AppUtils
import java.io.Serializable
import java.util.*

class ShakeToWinExtendedProps : Serializable {
    @SerializedName("background_image"                  ) var backgroundImage                : String?               = null
    @SerializedName("background_color"                  ) var backgroundColor                : String?               = null
    @SerializedName("font_family"                       ) var fontFamily                     : String?               = null
    @SerializedName("custom_font_family_ios"            ) var customFontFamilyIos            : String?               = null
    @SerializedName("custom_font_family_android"        ) var customFontFamilyAndroid        : String?               = null
    @SerializedName("close_button_color"                ) var closeButtonColor               : String?               = null
    @SerializedName("mail_subscription_form"            ) var mailSubscriptionForm           : ShakeToWinExtendedPropsMailSubscriptionForm? = ShakeToWinExtendedPropsMailSubscriptionForm()
    @SerializedName("gamification_rules"                ) var gamificationRules              : ShakeToWinExtendedPropsGamificationRules?    = ShakeToWinExtendedPropsGamificationRules()
    @SerializedName("game_elements"                     ) var gameElements                   : ShakeToWinExtendedPropsGameElements?         = ShakeToWinExtendedPropsGameElements()
    @SerializedName("game_result_elements"              ) var gameResultElements             : ShakeToWinExtendedPropsGameResultElements?   = ShakeToWinExtendedPropsGameResultElements()
    @SerializedName("promocode_background_color"        ) var promocodeBackgroundColor       : String?               = null
    @SerializedName("promocode_text_color"              ) var promocodeTextColor             : String?               = null
    @SerializedName("copybutton_color"                  ) var copybuttonColor                : String?               = null
    @SerializedName("copybutton_text_color"             ) var copybuttonTextColor            : String?               = null
    @SerializedName("copybutton_text_size"              ) var copybuttonTextSize             : String?               = null
    @SerializedName("promocode_banner_text"             ) var promocodeBannerText            : String?               = null
    @SerializedName("promocode_banner_text_color"       ) var promocodeBannerTextColor       : String?               = null
    @SerializedName("promocode_banner_background_color" ) var promocodeBannerBackgroundColor : String?               = null
    @SerializedName("promocode_banner_button_label"     ) var promocodeBannerButtonLabel     : String?               = null
    @SerializedName("emailpermit_text_size"             ) var emailPermitTextSize            : String?               = null
    @SerializedName("emailpermit_text_url"              ) var emailPermitTextUrl             : String?               = null
    @SerializedName("consent_text_size"                 ) var consentTextSize                : String?               = null
    @SerializedName("consent_text_url"                  ) var consentTextUrl                 : String?               = null
    @SerializedName("content_title_text_color"          ) var contentTitleTextColor          : String?               = null
    @SerializedName("content_title_font_family"         ) private var content_title_font_family: String?             = null
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

    @SerializedName("text_size")
    var textSize: String? = null

    @SerializedName("text_font_family")
    var textFontFamily: String? = null


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
}