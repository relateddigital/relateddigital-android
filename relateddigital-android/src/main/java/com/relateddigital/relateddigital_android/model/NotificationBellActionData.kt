package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NotificationBellActionData : Serializable {
    @SerializedName("title")
    var title: String? = null

    @SerializedName("bell_icon")
    var bell_icon: String? = null

    @SerializedName("bell_animation")
    var bell_animation: String? = null

    @SerializedName("notification_texts")
    var notification_texts: List<NotificationBellTexts>? = null

    @SerializedName("report")
    var report: NotificationBellReport? = null

    @SerializedName("ExtendedProps")
    var extendedProps: String? = null
}