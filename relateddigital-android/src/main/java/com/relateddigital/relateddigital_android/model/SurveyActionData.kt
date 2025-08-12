package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SurveyActionData(
    @SerializedName("title")
    var title: String? = null,

    @SerializedName("survey_questions")
    var surveyQuestions: List<SurveyQuestion>? = null,

    @SerializedName("ExtendedProps")
    var extendedProps: String? = null,

    @SerializedName("report")
    var report: SurveyReport? = null
) : Serializable