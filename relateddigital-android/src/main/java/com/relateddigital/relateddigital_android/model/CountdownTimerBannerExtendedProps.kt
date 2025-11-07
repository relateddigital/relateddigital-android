package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CountdownTimerBannerExtendedProps(
    @SerializedName("content_title_text_color")
    val content_title_text_color: String? = null,

    @SerializedName("content_title_custom_font_family_android")
    val content_title_custom_font_family_android: String? = null,

    @SerializedName("content_body_text_color")
    val content_body_text_color: String? = null,

    @SerializedName("content_body_font_family")
    val content_body_font_family: String? = null,

    @SerializedName("content_body_custom_font_family_android")
    val content_body_custom_font_family_android: String? = null,

    @SerializedName("button_color")
    val button_color: String? = null,

    @SerializedName("counter_color")
    val counter_color: String? = null,

    @SerializedName("position_on_page")
    var position_on_page: String? = null,

    @SerializedName("button_text_color")
    val button_text_color: String? = null,

    @SerializedName("button_custom_font_family_android")
    val button_custom_font_family_android: String? = null,

    @SerializedName("background_color")
    val background_color: String? = null
) : Serializable