package com.relateddigital.relateddigital_android.inapp.story

import java.io.Serializable

interface StoryItemClickListener : Serializable {
    fun storyItemClicked(storyLink: String?)
}