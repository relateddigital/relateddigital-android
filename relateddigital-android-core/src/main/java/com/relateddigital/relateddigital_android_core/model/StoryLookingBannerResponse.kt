package com.relateddigital.relateddigital_android_core.model

import java.io.Serializable

class StoryLookingBannerResponse : Serializable {
    var capping: String? = null
    var VERSION = 0
    var FavoriteAttributeAction: List<String>? = null
    var Story: List<BannerStory>? = null
}