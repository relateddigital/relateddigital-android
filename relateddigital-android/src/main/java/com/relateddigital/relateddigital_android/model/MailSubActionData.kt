package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MailSubActionData : Serializable {
    @SerializedName("placeholder")
    var placeholder: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("button_label")
    var button_label: String? = null

    @SerializedName("waiting_time")
    var waiting_time: Int? = null

    @SerializedName("sendemail")
    var sendemail: Boolean? = null

    @SerializedName("auth")
    var auth: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("SendEventsToMyFriend")
    var SendEventsToMyFriend: Boolean? = null

    @SerializedName("consent_text")
    var consent_text: String? = null

    @SerializedName("success_message")
    var success_message: String? = null

    @SerializedName("invalid_email_message")
    var invalid_email_message: String? = null

    @SerializedName("emailpermit_text")
    var emailpermit_text: String? = null

    @SerializedName("ExtendedProps")
    var ExtendedProps: String? = null

    @SerializedName("language")
    var language: String? = null

    @SerializedName("check_consent_message")
    var check_consent_message: String? = null

    @SerializedName("report")
    var report: MailSubReport? = null

    @SerializedName("img")
    var img: String? = null

    @SerializedName("taTemplate")
    var taTemplate: String? = null
}