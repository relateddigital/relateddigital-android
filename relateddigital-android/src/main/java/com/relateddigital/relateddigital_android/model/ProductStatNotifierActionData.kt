package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName

class ProductStatNotifierActionData {
    @SerializedName("content")
    var content: String? = null

    @SerializedName("width")
    var width: Int? = null

    @SerializedName("height")
    var height: Int? = null

    @SerializedName("timeout")
    var timeout: String? = null

    @SerializedName("pos")
    var pos: String? = null

    @SerializedName("bgcolor")
    var bgcolor: String? = null

    @SerializedName("threshold")
    var threshold: Int? = null

    @SerializedName("showclosebtn")
    var showclosebtn: Boolean? = null

    @SerializedName("waiting_time")
    var waitingTime: Int? = null

    @SerializedName("eventtype")
    var eventtype: String? = null

    @SerializedName("ExtendedProps")
    var extendedProps: String? = null
}