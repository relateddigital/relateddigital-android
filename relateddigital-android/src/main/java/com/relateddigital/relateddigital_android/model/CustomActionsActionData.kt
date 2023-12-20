package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CustomActionsActionData  : Serializable {

    @SerializedName("cid"                 ) var cid               : String? = null
    @SerializedName("courseofaction"      ) var courseofaction    : String? = null
    @SerializedName("javascript"          ) var javascript        : String? = null
    @SerializedName("content"             ) var content           : String? = null
    @SerializedName("report"              ) var report            : CustomActionsReport? = null
    @SerializedName("ExtendedProps"       ) var extendedProps       : String? = null

}