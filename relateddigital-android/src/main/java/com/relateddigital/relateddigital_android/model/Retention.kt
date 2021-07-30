package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Retention : Serializable {
    @SerializedName("token")
    var token: String? = null

    @SerializedName("key")
    var key: String? = null

    @SerializedName("pushId")
    var pushId: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("deliver")
    var deliver = 0

    @SerializedName("isMobile")
    var isMobile = 1

    @SerializedName("actionBtn")
    var actionBtn = 0

    @SerializedName("emPushSp")
    var emPushSp: String? = null
}