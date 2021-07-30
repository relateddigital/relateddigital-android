package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class InAppMessage : Serializable {
    @SerializedName("actid")
    var mActId: Int? = null

    @SerializedName("actiondata")
    var mActionData: ActionData? = null

    @SerializedName("actiontype")
    var mActionType: String? = null

    @SerializedName("title")
    var mTitle: String? = null
}