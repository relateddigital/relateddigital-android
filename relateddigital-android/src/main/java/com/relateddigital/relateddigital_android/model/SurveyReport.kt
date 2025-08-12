package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SurveyReport(
    @SerializedName("impression")
    var impression: String? = null,

    @SerializedName("click")
    var click: String? = null
) : Serializable