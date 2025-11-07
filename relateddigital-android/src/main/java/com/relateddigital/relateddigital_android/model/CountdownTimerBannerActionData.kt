package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CountdownTimerBannerActionData(
    @SerializedName("scratch_color")
    var scratch_color: String? = null,

    @SerializedName("waiting_time")
    var waiting_time: Int? = null,

    @SerializedName("ExtendedProps")
    var extendedProps: String? = null,

    @SerializedName("report")
    var report: CountdownTimerBannerReport? = null,

    @SerializedName("android_lnk")
    var android_lnk: String? = null,

    @SerializedName("img")
    var img: String? = null,

    @SerializedName("content_body")
    var content_body: String? = null,

    @SerializedName("close_button_color")
    var close_button_color: String? = null,

    @SerializedName("counter_Date")
    var counter_Date: String? = null,

    @SerializedName("counter_Time")
    var counter_Time: String? = null
) : Serializable