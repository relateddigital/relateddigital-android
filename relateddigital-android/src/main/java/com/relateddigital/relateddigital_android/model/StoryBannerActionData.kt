package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class StoryBannerActionData : Serializable {
    @SerializedName("stories")
    var stories: List<BannerStories>? = null

    @SerializedName("taTemplate")
    var taTemplate: String? = null

    @SerializedName("ExtendedProps")
    var extendedProps: String? = null

    @SerializedName("report")
    val report: StoryReport? = null
}