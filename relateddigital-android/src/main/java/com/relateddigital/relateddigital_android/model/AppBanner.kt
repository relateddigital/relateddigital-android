package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AppBanner : Serializable {
    @SerializedName("actid")
    var actId: Int? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("actiontype")
    var actionType: String? = null

    @SerializedName("actiondata")
    var actionData: AppBannerActionData? = null
}