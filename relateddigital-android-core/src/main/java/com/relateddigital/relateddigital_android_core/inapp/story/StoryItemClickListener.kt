package com.relateddigital.relateddigital_android_core.inapp.story

import java.io.Serializable

interface StoryItemClickListener : Serializable {
    fun storyItemClicked(storyLink: String?)
}