package com.relateddigital.relateddigital_android.model

import android.graphics.Typeface
import com.google.gson.annotations.SerializedName
import com.relateddigital.relateddigital_android.inapp.FontFamily
import java.io.Serializable

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

    @SerializedName("number_colors")
    var mNumberColors: Array<String?>? = null

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

    fun getFontFamily(): Typeface? {
        if (mFontFamily == null || mFontFamily == "") {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == mFontFamily!!.toLowerCase()) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == mFontFamily!!.toLowerCase()) {
            return Typeface.SANS_SERIF
        }
        return if (FontFamily.Serif.toString() == mFontFamily!!.toLowerCase()) {
            Typeface.SERIF
        } else Typeface.DEFAULT
    }
}