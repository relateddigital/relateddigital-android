package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class StoryActionData : Serializable {
    @SerializedName("stories")
    var stories: List<Stories>? = null

    @SerializedName("taTemplate")
    var taTemplate: String? = null

    @SerializedName("extendedProps")
    var extendedProps: String? = null

    @SerializedName("report")
    val report: StoryReport? = null
}