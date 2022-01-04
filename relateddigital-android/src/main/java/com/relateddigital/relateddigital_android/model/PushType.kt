package com.relateddigital.relateddigital_android.model

enum class PushType(private val nameStr: String) {
    Text("Text"), Image("Image"), Carousel("Carousel"), Video("Video");

    fun equalsName(otherName: String): Boolean {
        return nameStr == otherName
    }

    override fun toString(): String {
        return nameStr
    }
}