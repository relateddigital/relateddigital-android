package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SurveyQuestion(
    @SerializedName("question_text")
    var questionText: String? = null,

    @SerializedName("options")
    var options: List<SurveyOption>? = null
) : Serializable