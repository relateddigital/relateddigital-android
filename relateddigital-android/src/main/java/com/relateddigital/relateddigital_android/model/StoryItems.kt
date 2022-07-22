package com.relateddigital.relateddigital_android.model

import java.io.Serializable

class StoryItems : Serializable {
    var fileSrc: String? = null
    var buttonText: String? = null
    var displayTime: String? = null
    var buttonColor: String? = null
    var filePreview: String? = null
    var buttonTextColor: String? = null
    var targetUrl: String? = null
    var fileType: String? = null
    var countdown: StoryItemCountdown? = null

    override fun toString(): String {
        return "Items [ fileSrc = " + fileSrc + ", buttonText = " + buttonText +
                ", displayTime = " + displayTime + ", buttonColor = " + buttonColor +
                ", filePreview = " + filePreview + ", buttonTextColor = " + buttonTextColor +
                ", targetUrl = " + targetUrl + ", fileType = " + fileType + "]"
    }
}