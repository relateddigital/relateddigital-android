package com.relateddigital.relateddigital_android.model

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.google.gson.annotations.SerializedName
import com.relateddigital.relateddigital_android.inapp.FontFamily
import java.io.Serializable
import java.util.*
import com.relateddigital.relateddigital_android.util.AppUtils.isFontResourceAvailable


class ActionData : Serializable {
    @SerializedName("alert_type")
    var mAlertType: String? = null

    @SerializedName("android_lnk")
    var mAndroidLnk: String? = null

    @SerializedName("background")
    var mBackground: String? = null

    @SerializedName("btn_text")
    var mBtnText: String? = null

    @SerializedName("button_color")
    var mButtonColor: String? = null

    @SerializedName("button_text_color")
    var mButtonTextColor: String? = null

    @SerializedName("cid")
    var mCid: String? = null

    @SerializedName("close_button_color")
    var mCloseButtonColor: String? = null

    @SerializedName("close_button_text")
    var mCloseButtonText: String? = null

    @SerializedName("courseofaction")
    var mCourseOfAction: String? = null

    @SerializedName("font_family")
    var mFontFamily: String? = null

    @SerializedName("img")
    var mImg: String? = null

    @SerializedName("ios_lnk")
    var mIosLnk: String? = null

    @SerializedName("msg_body")
    var mMsgBody: String? = null

    @SerializedName("msg_body_color")
    var mMsgBodyColor: String? = null

    @SerializedName("msg_body_textsize")
    var mMsgBodyTextSize: String? = null

    @SerializedName("msg_title")
    var mMsgTitle: String? = null

    @SerializedName("msg_title_color")
    var mMsgTitleColor: String? = null

    @SerializedName("msg_type")
    var mMsgType: String? = null

    @SerializedName("promocode_background_color")
    var mPromoCodeBackgroundColor: String? = null

    @SerializedName("promocode_text_color")
    var mPromoCodeTextColor: String? = null

    @SerializedName("promotion_code")
    var mPromotionCode: String? = null

    @SerializedName("promocode_copybutton_text")
    var mPromoCodeCopyButtonText: String? = null

    @SerializedName("promocode_copybutton_text_color")
    var mPromoCodeCopyButtonTextColor: String? = null

    @SerializedName("promocode_copybutton_color")
    var mPromocodeCopyButtonColor: String? = null

    @SerializedName("number_colors")
    var mNumberColors: Array<String?>? = null

    @SerializedName("number_range")
    var mNumberRange: String? = null

    @SerializedName("qs")
    var mQs: String? = null

    @SerializedName("visit_data")
    var mVisitData: String? = null

    @SerializedName("visitor_data")
    var mVisitorData: String? = null

    @SerializedName("waiting_time")
    var mWaitingTime: String? = null

    @SerializedName("secondPopup_type")
    var mSecondPopupType: String? = null

    @SerializedName("secondPopup_msg_title")
    var mSecondPopupMsgTitle: String? = null

    @SerializedName("secondPopup_msg_body")
    var mSecondPopupMsgBody: String? = null

    @SerializedName("secondPopup_btn_text")
    var mSecondPopupBtnText: String? = null

    @SerializedName("secondPopup_msg_body_textsize")
    var mSecondPopupMsgBodyTextSize: String? = null

    @SerializedName("secondPopup_feedbackform_minpoint")
    var mSecondPopupFeecbackFormMinPoint: String? = null

    @SerializedName("secondPopup_image1")
    var mSecondPopupImg1: String? = null

    @SerializedName("secondPopup_image2")
    var mSecondPopupImg2: String? = null

    @SerializedName("pos")
    var mPos: String? = null

    @SerializedName("msg_title_textsize")
    var mMsgTitleTextSize: String? = null

    @SerializedName("close_event_trigger")
    var mCloseEventTrigger: String? = null

    @SerializedName("custom_font_family_ios")
    var mCustomFontFamilyIos: String? = null

    @SerializedName("custom_font_family_android")
    var mCustomFontFamilyAndroid: String? = null

    @SerializedName("carousel_items")
    var carouselItems: List<InAppCarouselItem>? = null

    @SerializedName("msg_title_backgroundcolor")
    var mMsgTitleBackgroundColor: String? = null

    @SerializedName("msg_body_backgroundcolor")
    var mMsgBodyBackgroundColor: String? = null

    @SerializedName("button_function")
    var mButtonFunction: String? = null

    @SerializedName("videourl")
    var mVideoUrl: String? = null

    @SerializedName("secondPopup_videourl1")
    var mSecondPopupVideoUrl1: String? = null

    @SerializedName("secondPopup_videourl2")
    var mSecondPopupVideoUrl2: String? = null

    @SerializedName("second_button_function")
    var mSecondButtonFunction: String? = null

    @SerializedName("second_button_text")
    var mSecondButtonText: String? = null

    @SerializedName("second_button_text_color")
    var mSecondButtonTextColor: String? = null

    @SerializedName("second_button_color")
    var mSecondButtonColor: String? = null

    @SerializedName("second_button_ios_lnk")
    var mSecondButtonIosLink: String? = null

    @SerializedName("second_button_android_lnk")
    var mSecondButtonAndroidLink: String? = null

    @SerializedName("button_border_radius")
    var mButtonBorderRadius: String? = null


    fun getFontFamily(context: Context): Typeface? {
        if (mFontFamily == null || mFontFamily == "") {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == mFontFamily!!.lowercase(Locale.getDefault())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == mFontFamily!!.lowercase(Locale.getDefault())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == mFontFamily!!.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (!mCustomFontFamilyAndroid.isNullOrEmpty()) {
            if (isFontResourceAvailable(context, mCustomFontFamilyAndroid)) {
                val id = context.resources.getIdentifier(
                    mCustomFontFamilyAndroid,
                    "font",
                    context.packageName
                )
                return ResourcesCompat.getFont(context, id)
            }
        }

        return Typeface.DEFAULT
    }
}