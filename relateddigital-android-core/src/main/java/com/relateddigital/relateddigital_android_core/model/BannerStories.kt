package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class BannerStories : Serializable {
    @SerializedName("title")
    var title: String? = null

    @SerializedName("smallImg")
    var smallImg: String? = null

    @SerializedName("link")
    var link: String? = null

    @SerializedName("shown")
    var shown = false
}