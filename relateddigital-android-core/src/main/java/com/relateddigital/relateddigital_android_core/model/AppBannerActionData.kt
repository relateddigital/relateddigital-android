package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AppBannerActionData: Serializable {
    @SerializedName("taTemplate")
    var taTemplate: String? = null

    @SerializedName("app_banners")
    var appBanners: List<AppBannerItem>? = null

    @SerializedName("transition_action")
    var transitionAction: String? = null

    @SerializedName("report")
    var report: AppBannerReport? = null
}