package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SpinToWinActionData : Serializable {
    @SerializedName("slices")
    var slices: List<Slice>? = null

    @SerializedName("mail_subscription")
    var mailSubscription: Boolean? = null

    @SerializedName("spin_to_win_content")
    var spinToWinContent: SpinToWinContent? = null

    @SerializedName("font_size")
    var fontSize: Int? = null

    @SerializedName("circle_R")
    var circleR: Int? = null

    @SerializedName("auth")
    var auth: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("waiting_time")
    var waitingTime: Int? = null

    @SerializedName("promoAuth")
    var promoAuth: String? = null

    @SerializedName("slice_count")
    var sliceCount: String? = null

    @SerializedName("sendemail")
    var sendemail: Boolean? = null

    @SerializedName("courseofaction")
    var courseofaction: String? = null

    @SerializedName("ExtendedProps")
    var extendedProps: String? = null

    @SerializedName("report")
    var report: SpinToWinReport? = null

    @SerializedName("img")
    var img: String? = null

    @SerializedName("taTemplate")
    var taTemplate: String? = null

    @SerializedName("promocode_title")
    var promocodeTitle: String? = null

    @SerializedName("copybutton_label")
    var copybuttonLabel: String? = null

    @SerializedName("wheel_spin_action")
    var wheelSpinAction: String? = null

    @SerializedName("promocodes_soldout_message")
    var promoCodesSoldOutMessage: String? = null

    @SerializedName("copybutton_function")
    var copyButtonFunction: String? = null

}