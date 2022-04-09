package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DrawerReport : Serializable {
    @SerializedName("impression")
    private var impression: String? = null

    @SerializedName("click")
    private var click: String? = null

    fun getImpression(): String? {
        return impression
    }

    fun setImpression(impression: String?) {
        this.impression = impression
    }

    fun getClick(): String? {
        return click
    }

    fun setClick(click: String?) {
        this.click = click
    }
}