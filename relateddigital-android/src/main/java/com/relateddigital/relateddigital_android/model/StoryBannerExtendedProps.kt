package com.relateddigital.relateddigital_android.model

import java.io.Serializable

class StoryBannerExtendedProps : Serializable {
    var storylb_img_borderWidth: String? = null
    var storylb_img_borderRadius: String? = null
    var storylb_img_boxShadow: String? = null
    var storylb_img_borderColor: String? = null
    var storylb_label_color: String? = null
    var moveShownToEnd = false

    override fun toString(): String {
        return "ExtendedProps [storylb_img_borderWidth = " + storylb_img_borderWidth +
                ", storylb_img_borderRadius = " + storylb_img_borderRadius +
                ", storylb_img_boxShadow = " + storylb_img_boxShadow +
                ", storylb_img_borderColor = " + storylb_img_borderColor +
                ", storylb_label_color = " + storylb_label_color +
                ", moveShownToEnd = " + moveShownToEnd + "]"
    }
}