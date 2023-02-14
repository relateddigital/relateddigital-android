package com.relateddigital.relateddigital_android_core.model

import java.io.Serializable

class SkinBasedStories : Serializable {
    var thumbnail: String? = null
    var title: String? = null
    private var items: List<StoryItems>? = null
    var shown = false

    fun getItems(): List<StoryItems>? {
        return items
    }

    fun setItems(items: List<StoryItems>?) {
        this.items = items
    }

    override fun toString(): String {
        return "Stories [thumbnail = $thumbnail, title = $title, items = $items]"
    }
}