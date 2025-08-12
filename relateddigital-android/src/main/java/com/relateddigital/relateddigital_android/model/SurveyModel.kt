package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SurveyModel(
    var fontFiles: ArrayList<String> = arrayListOf(),

    @SerializedName("actid")
    var actid: Int? = null,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("actiontype")
    var actiontype: String? = null,

    @SerializedName("actiondata")
    var actiondata: SurveyActionData? = null
) : Serializable