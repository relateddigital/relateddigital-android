package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class PuzzleActionData: Serializable {

    @SerializedName("mail_subscription")
    var mailSubscription: Boolean? = null


    @SerializedName("copybutton_label")
    var copyButtonLabel: String? = null

    @SerializedName("copybutton_function")
    var copyButtonFunction: String? = null

    @SerializedName("ios_lnk")
    var iosLink: String? = null

    @SerializedName("android_lnk")
    var androidLink: String? = null


    @SerializedName("ExtendedProps")
    var extendedProps: String? = null

    @SerializedName("report")
    var report: PuzzleReport? = null

    @SerializedName("auth")
    var auth: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("waiting_time")
    var waitingTime: Int? = null
}