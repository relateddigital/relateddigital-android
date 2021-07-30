package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ActionResponse : Serializable {
    @SerializedName("FavoriteAttributeAction")
    var mFavoriteAttributeAction: List<FavoriteAttributeAction>? = null

    @SerializedName("MailSubscriptionForm")
    var mMailSubscriptionForm: List<MailSubscriptionForm>? = null

    @SerializedName("SpinToWin")
    var mSpinToWinList: List<SpinToWin>? = null

    @SerializedName("ScratchToWin")
    var mScratchToWinList: List<ScratchToWin>? = null

    @SerializedName("Story")
    var mStory: List<Story>? = null

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
    var spinToWinList: List<SpinToWin>?
        get() = mSpinToWinList
        set(spinToWinList) {
            mSpinToWinList = spinToWinList
        }
    var scratchToWinList: List<ScratchToWin>?
        get() = mScratchToWinList
        set(scratchToWinList) {
            mScratchToWinList = scratchToWinList
        }
    var story: List<Story>?
        get() = mStory
        set(story) {
            mStory = story
        }
}