package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class CustomActions {
    var fontFiles = ArrayList<String>()

    @SerializedName("actid")
    var actid: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("actiontype")
    var actiontype: String? = null

    @SerializedName("actiondata")
    var actiondata: CustomActionsActionData? = null

    @SerializedName("panelv2"    ) var panelv2    : Boolean?    = null
}