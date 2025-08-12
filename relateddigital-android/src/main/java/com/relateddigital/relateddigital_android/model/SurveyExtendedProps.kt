package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SurveyExtendedProps(
    @SerializedName("background_color")
    var backgroundColor: String? = null,

    @SerializedName("font_family")
    var fontFamily: String? = null,

    @SerializedName("title_text_color")
    var titleTextColor: String? = null,

    @SerializedName("title_text_size")
    var titleTextSize: String? = null,

    @SerializedName("navigationbar_color")
    var navigationBarColor: String? = null
) : Serializable