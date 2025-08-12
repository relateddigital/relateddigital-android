package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SurveyResult(
    @SerializedName("title")
    var title: String? = null,

    @SerializedName("questions")
    var questions: List<SurveyQuestionAnswer>? = null
) : Serializable