package com.relateddigital.relateddigital_android.model

import android.content.Context
import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat
import com.google.gson.annotations.SerializedName
import com.relateddigital.relateddigital_android.inapp.FontFamily
import com.relateddigital.relateddigital_android.util.AppUtils
import java.util.*

class InAppCarouselItem constructor(`in`: Parcel) : Parcelable {
    @SerializedName("image")
    var image: String?

    @SerializedName("title")
    var title: String?

    @SerializedName("title_color")
    var titleColor: String?

    @SerializedName("title_font_family")
    private var titleFontFamily: String?

    @SerializedName("title_custom_font_family_ios")
    var titleCustomFontFamilyIos: String?

    @SerializedName("title_custom_font_family_android")
    var titleCustomFontFamilyAndroid: String?

    @SerializedName("title_textsize")
    var titleTextsize: String?

    @SerializedName("body")
    var body: String?

    @SerializedName("body_color")
    var bodyColor: String?

    @SerializedName("body_font_family")
    private var bodyFontFamily: String?

    @SerializedName("body_custom_font_family_ios")
    var bodyCustomFontFamilyIos: String?

    @SerializedName("body_custom_font_family_android")
    var bodyCustomFontFamilyAndroid: String?

    @SerializedName("body_textsize")
    var bodyTextsize: String?

    @SerializedName("promocode_type")
    var promocodeType: String?

    @SerializedName("cid")
    var cid: String?

    @SerializedName("promotion_code")
    var promotionCode: String?

    @SerializedName("promocode_background_color")
    var promocodeBackgroundColor: String?

    @SerializedName("promocode_text_color")
    var promocodeTextColor: String?

    @SerializedName("button_text")
    var buttonText: String?

    @SerializedName("button_text_color")
    var buttonTextColor: String?

    @SerializedName("button_color")
    var buttonColor: String?

    @SerializedName("button_font_family")
    private var buttonFontFamily: String?

    @SerializedName("button_custom_font_family_ios")
    var buttonCustomFontFamilyIos: String?

    @SerializedName("button_custom_font_family_android")
    var buttonCustomFontFamilyAndroid: String?

    @SerializedName("button_textsize")
    var buttonTextsize: String?

    @SerializedName("background_image")
    var backgroundImage: String?

    @SerializedName("background_color")
    var backgroundColor: String?

    @SerializedName("ios_lnk")
    var iosLnk: String?

    @SerializedName("android_lnk")
    var androidLnk: String?

    fun getTitleFontFamily(context: Context): Typeface? {
        if (titleFontFamily == null || titleFontFamily == "") {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == titleFontFamily!!.lowercase(Locale.getDefault())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == titleFontFamily!!.lowercase(Locale.getDefault())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == titleFontFamily!!.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (!titleCustomFontFamilyAndroid.isNullOrEmpty()) {
            if (AppUtils.isFontResourceAvailable(context, titleCustomFontFamilyAndroid)) {
                val id = context.resources.getIdentifier(
                    titleCustomFontFamilyAndroid,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }

    fun setTitleFontFamily(titleFontFamily: String?) {
        this.titleFontFamily = titleFontFamily
    }

    fun getBodyFontFamily(context: Context): Typeface? {
        if (bodyFontFamily == null || bodyFontFamily == "") {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == bodyFontFamily!!.lowercase(Locale.getDefault())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == bodyFontFamily!!.lowercase(Locale.getDefault())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == bodyFontFamily!!.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (!bodyCustomFontFamilyAndroid.isNullOrEmpty()) {
            if (AppUtils.isFontResourceAvailable(context, bodyCustomFontFamilyAndroid)) {
                val id = context.resources.getIdentifier(
                    bodyCustomFontFamilyAndroid,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }

    fun setBodyFontFamily(bodyFontFamily: String?) {
        this.bodyFontFamily = bodyFontFamily
    }

    fun getButtonFontFamily(context: Context): Typeface? {
        if (buttonFontFamily == null || buttonFontFamily == "") {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == buttonFontFamily!!.lowercase(Locale.getDefault())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == buttonFontFamily!!.lowercase(Locale.getDefault())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == buttonFontFamily!!.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (!buttonCustomFontFamilyAndroid.isNullOrEmpty()) {
            if (AppUtils.isFontResourceAvailable(context, buttonCustomFontFamilyAndroid)) {
                val id = context.resources.getIdentifier(
                    buttonCustomFontFamilyAndroid,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }
        return Typeface.DEFAULT
    }

    fun setButtonFontFamily(buttonFontFamily: String?) {
        this.buttonFontFamily = buttonFontFamily
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(image)
        dest.writeString(title)
        dest.writeString(titleColor)
        dest.writeString(titleFontFamily)
        dest.writeString(titleCustomFontFamilyIos)
        dest.writeString(titleCustomFontFamilyAndroid)
        dest.writeString(titleTextsize)
        dest.writeString(body)
        dest.writeString(bodyColor)
        dest.writeString(bodyFontFamily)
        dest.writeString(bodyCustomFontFamilyIos)
        dest.writeString(bodyCustomFontFamilyAndroid)
        dest.writeString(bodyTextsize)
        dest.writeString(promocodeType)
        dest.writeString(cid)
        dest.writeString(promotionCode)
        dest.writeString(promocodeBackgroundColor)
        dest.writeString(promocodeTextColor)
        dest.writeString(buttonText)
        dest.writeString(buttonTextColor)
        dest.writeString(buttonColor)
        dest.writeString(buttonFontFamily)
        dest.writeString(buttonCustomFontFamilyIos)
        dest.writeString(buttonCustomFontFamilyAndroid)
        dest.writeString(buttonTextsize)
        dest.writeString(backgroundImage)
        dest.writeString(backgroundColor)
        dest.writeString(iosLnk)
        dest.writeString(androidLnk)
    }

    companion object CREATOR : Parcelable.Creator<InAppCarouselItem> {
        override fun createFromParcel(parcel: Parcel): InAppCarouselItem {
            return InAppCarouselItem(parcel)
        }

        override fun newArray(size: Int): Array<InAppCarouselItem?> {
            return arrayOfNulls(size)
        }
    }

    init {
        image = `in`.readString()
        title = `in`.readString()
        titleColor = `in`.readString()
        titleFontFamily = `in`.readString()
        titleCustomFontFamilyIos = `in`.readString()
        titleCustomFontFamilyAndroid = `in`.readString()
        titleTextsize = `in`.readString()
        body = `in`.readString()
        bodyColor = `in`.readString()
        bodyFontFamily = `in`.readString()
        bodyCustomFontFamilyIos = `in`.readString()
        bodyCustomFontFamilyAndroid = `in`.readString()
        bodyTextsize = `in`.readString()
        promocodeType = `in`.readString()
        cid = `in`.readString()
        promotionCode = `in`.readString()
        promocodeBackgroundColor = `in`.readString()
        promocodeTextColor = `in`.readString()
        buttonText = `in`.readString()
        buttonTextColor = `in`.readString()
        buttonColor = `in`.readString()
        buttonFontFamily = `in`.readString()
        buttonCustomFontFamilyIos = `in`.readString()
        buttonCustomFontFamilyAndroid = `in`.readString()
        buttonTextsize = `in`.readString()
        backgroundImage = `in`.readString()
        backgroundColor = `in`.readString()
        iosLnk = `in`.readString()
        androidLnk = `in`.readString()
    }
}