package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Stories : Serializable {
    @SerializedName("title")
    var title: String? = null

    @SerializedName("smallImg")
    var smallImg: String? = null

    @SerializedName("link")
    var link: String? = null

    @SerializedName("shown")
    var shown: Boolean? = null
}