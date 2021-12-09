package com.relateddigital.relateddigital_android.model

class StorySkinBasedExtendedProps {
    var storyz_img_borderRadius: String? = null
        private set
    var storyz_img_borderColor: String? = null
    var storyz_label_color: String? = null
    var font_family: String? = null
    var custom_font_family_ios: String? = null
    var custom_font_family_android: String? = null
    var moveShownToEnd = false

    fun setstoryz_img_borderRadius(storyz_img_borderRadius: String?) {
        this.storyz_img_borderRadius = storyz_img_borderRadius
    }
}