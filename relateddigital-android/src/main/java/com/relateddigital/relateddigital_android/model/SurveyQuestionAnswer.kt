package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SurveyQuestionAnswer(
    @SerializedName("question")
    var question: String? = null,

    @SerializedName("answer")
    var answer: String? = null
) : Serializable