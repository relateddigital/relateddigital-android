package com.relateddigital.relateddigital_android.inapp

import java.io.Serializable

interface StoryItemClickListener : Serializable {
    fun storyItemClicked(storyLink: String?)
}