package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class MailSubscriptionForm : Serializable {
    @SerializedName("actid")
    var actid: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("actiontype")
    var actiontype: String? = null

    @SerializedName("actiondata")
    var actiondata: MailSubActionData? = null
}