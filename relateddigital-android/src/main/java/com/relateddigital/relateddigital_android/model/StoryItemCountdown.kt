package com.relateddigital.relateddigital_android.model

import java.io.Serializable

class StoryItemCountdown : Serializable {
    var pagePosition: String? = null
    var messageText: String? = null
    var messageTextSize: String? = null
    var messageTextColor: String? = null
    var displayType: String? = null
    var endDateTime: String? = null
    var endAction: String? = null  //TODO : endAction gidecek yerine bitince cıkacak gif url si gelecek
}