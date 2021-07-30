package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FavoriteAttributeAction : Serializable {
    @SerializedName("actiontype")
    var actiontype: String? = null

    @SerializedName("actid")
    var actid: String? = null

    @SerializedName("actiondata")
    var actiondata: FavsActionData? = null

    @SerializedName("title")
    var title: String? = null

    override fun toString(): String {
        return "FavoriteAttributeAction [actiontype = $actiontype, actid = $actid, actiondata = $actiondata, title = $title]"
    }
}