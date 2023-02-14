package com.relateddigital.relateddigital_android_core.model

import java.io.Serializable

class StorySkinBasedResponse : Serializable {
    var capping: String? = null
    var VERSION = 0
    var FavoriteAttributeAction: List<String>? = null
    var Story: List<SkinBasedStory>? = null
}