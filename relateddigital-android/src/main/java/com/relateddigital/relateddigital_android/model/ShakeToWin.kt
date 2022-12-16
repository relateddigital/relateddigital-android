package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ShakeToWin : Serializable{

    @SerializedName("actiondata")
    var actiondata: ShakeToWinActionData? = null
}