package com.relateddigital.relateddigital_android.model

import android.graphics.Typeface
import com.google.gson.annotations.SerializedName
import com.relateddigital.relateddigital_android.inapp.FontFamily
import java.io.Serializable
import java.util.*

class ScratchToWinExtendedProps : Serializable {
    @SerializedName("content_title_text_color")
    var contentTitleTextColor: String? = null

    @SerializedName("content_title_font_family")
    private var content_title_font_family: String? = null

    @SerializedName("content_title_text_size")
    var contentTitleTextSize: String? = null

    @SerializedName("content_body_text_color")
    var contentBodyTextColor: String? = null

    @SerializedName("content_body_text_font_family")
    private var content_body_text_font_family: String? = null

    @SerializedName("content_body_text_size")
    var contentBodyTextSize: String? = null

    @SerializedName("button_color")
    var buttonColor: String? = null

    @SerializedName("button_text_color")
    var buttonTextColor: String? = null

    @SerializedName("button_font_family")
    private var button_font_family: String? = null

    @SerializedName("button_text_size")
    var buttonTextSize: String? = null

    @SerializedName("promocode_text_color")
    var promoCodeTextColor: String? = null

    @SerializedName("promocode_font_family")
    private var promocode_font_family: String? = null

    @SerializedName("promocode_text_size")
    var promoCodeTextSize: String? = null

    @SerializedName("copybutton_color")
    var copyButtonColor: String? = null

    @SerializedName("copybutton_text_color")
    var copyButtonTextColor: String? = null

    @SerializedName("copybutton_font_family")
    private var copybutton_font_family: String? = null

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

    val contentTitleFontFamily: Typeface
        get() {
            if (content_title_font_family == null || content_title_font_family == "") {
                return Typeface.DEFAULT
            }
            if (FontFamily.Monospace.toString().equals(content_title_font_family!!.toLowerCase(Locale.ROOT))) {
                return Typeface.MONOSPACE
            }
            if (FontFamily.SansSerif.toString().equals(content_title_font_family!!.toLowerCase(Locale.ROOT))) {
                return Typeface.SANS_SERIF
            }
            return if (FontFamily.Serif.toString().equals(content_title_font_family!!.toLowerCase(Locale.ROOT))) {
                Typeface.SERIF
            } else Typeface.DEFAULT
        }

    fun setContentBodyTextFontFamily(contentBodyTextFontFamily: String?) {
        content_body_text_font_family = contentBodyTextFontFamily
    }

    val contentBodyTextFontFamily: Typeface
        get() {
            if (content_body_text_font_family == null || content_body_text_font_family == "") {
                return Typeface.DEFAULT
            }
            if (FontFamily.Monospace.toString().equals(content_body_text_font_family!!.toLowerCase(Locale.ROOT))) {
                return Typeface.MONOSPACE
            }
            if (FontFamily.SansSerif.toString().equals(content_body_text_font_family!!.toLowerCase(Locale.ROOT))) {
                return Typeface.SANS_SERIF
            }
            return if (FontFamily.Serif.toString().equals(content_body_text_font_family!!.toLowerCase(Locale.ROOT))) {
                Typeface.SERIF
            } else Typeface.DEFAULT
        }

    fun setButtonFontFamily(buttonFontFamily: String?) {
        button_font_family = buttonFontFamily
    }

    val buttonFontFamily: Typeface
        get() {
            if (button_font_family == null || button_font_family == "") {
                return Typeface.DEFAULT
            }
            if (FontFamily.Monospace.toString().equals(button_font_family!!.toLowerCase(Locale.ROOT))) {
                return Typeface.MONOSPACE
            }
            if (FontFamily.SansSerif.toString().equals(button_font_family!!.toLowerCase(Locale.ROOT))) {
                return Typeface.SANS_SERIF
            }
            return if (FontFamily.Serif.toString().equals(button_font_family!!.toLowerCase(Locale.ROOT))) {
                Typeface.SERIF
            } else Typeface.DEFAULT
        }

    fun setPromoCodeFontFamily(promoCodeFontFamily: String?) {
        promocode_font_family = promoCodeFontFamily
    }

    val promoCodeFontFamily: Typeface
        get() {
            if (promocode_font_family == null || promocode_font_family == "") {
                return Typeface.DEFAULT
            }
            if (FontFamily.Monospace.toString().equals(promocode_font_family!!.toLowerCase(Locale.ROOT))) {
                return Typeface.MONOSPACE
            }
            if (FontFamily.SansSerif.toString().equals(promocode_font_family!!.toLowerCase(Locale.ROOT))) {
                return Typeface.SANS_SERIF
            }
            return if (FontFamily.Serif.toString().equals(promocode_font_family!!.toLowerCase(Locale.ROOT))) {
                Typeface.SERIF
            } else Typeface.DEFAULT
        }

    fun setCopyButtonFontFamily(copyButtonFontFamily: String?) {
        copybutton_font_family = copyButtonFontFamily
    }

    val copyButtonFontFamily: Typeface
        get() {
            if (copybutton_font_family == null || copybutton_font_family == "") {
                return Typeface.DEFAULT
            }
            if (FontFamily.Monospace.toString().equals(copybutton_font_family!!.toLowerCase(Locale.ROOT))) {
                return Typeface.MONOSPACE
            }
            if (FontFamily.SansSerif.toString().equals(copybutton_font_family!!.toLowerCase(Locale.ROOT))) {
                return Typeface.SANS_SERIF
            }
            return if (FontFamily.Serif.toString().equals(copybutton_font_family!!.toLowerCase(Locale.ROOT))) {
                Typeface.SERIF
            } else Typeface.DEFAULT
        }
}