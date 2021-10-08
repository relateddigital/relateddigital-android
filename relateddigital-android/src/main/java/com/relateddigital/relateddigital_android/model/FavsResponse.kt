package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FavsResponse : Serializable {
    @SerializedName("FavoriteAttributeAction")
    private var mFavoriteAttributeAction: List<FavoriteAttributeAction>? = null

    @SerializedName("MailSubscriptionForm")
    private var mMailSubscriptionForm: List<MailSubscriptionForm>? = null

    @SerializedName("Story")
    private var mStory: List<BannerStory>? = null

    @SerializedName("VERSION")
    var version: Int? = null

    @SerializedName("capping")
    var capping: String? = null

    var favoriteAttributeAction: List<FavoriteAttributeAction>?
        get() = mFavoriteAttributeAction
        set(favoriteAttributeAction) {
            mFavoriteAttributeAction = favoriteAttributeAction
        }
    var mailSubscriptionForm: List<MailSubscriptionForm>?
        get() = mMailSubscriptionForm
        set(mailSubscriptionForm) {
            mMailSubscriptionForm = mailSubscriptionForm
        }
    var story: List<BannerStory>?
        get() = mStory
        set(story) {
            mStory = story
        }
}