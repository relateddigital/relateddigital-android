package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ShakeToWin : Serializable{

    @SerializedName("actid")
    var actid: Int? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("actiontype")
    var actiontype: String? = null

    @SerializedName("actiondata")
    var actiondata: ShakeToWinActionData? = null

    @SerializedName("panelv2"    ) var panelv2    : Boolean?    = null

}