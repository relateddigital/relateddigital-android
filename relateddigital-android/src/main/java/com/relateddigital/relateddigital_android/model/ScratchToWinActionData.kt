package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ScratchToWinActionData : Serializable {
    @SerializedName("cid")
    var cid: String? = null

    @SerializedName("mail_subscription")
    var mailSubscription: Boolean? = null

    @SerializedName("scratch_color")
    var scratchColor: String? = null

    @SerializedName("auth")
    var auth: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("waiting_time")
    var waitingTime: Int? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("sendemail")
    var sendemail: Boolean? = null

    @SerializedName("mail_subscription_form")
    var mailSubscriptionForm: ScratchToWinMailSubForm? = null

    @SerializedName("ExtendedProps")
    var extendedProps: String? = null

    @SerializedName("report")
    var report: ScratchToWinReport? = null

    @SerializedName("copybutton_label")
    var copybuttonLabel: String? = null

    @SerializedName("promotion_code")
    var promotionCode: String? = null

    @SerializedName("copybutton_function"    ) var copybuttonFunction   : String?               = null
    @SerializedName("ios_lnk"                ) var iosLnk               : String?               = null
    @SerializedName("android_lnk"            ) var androidLnk           : String?               = null

    @SerializedName("img")
    var img: String? = null

    @SerializedName("content_title")
    var contentTitle: String? = null

    @SerializedName("content_body")
    var contentBody: String? = null

    @SerializedName("down_content_body")
    var downContentBody: String? = null

}