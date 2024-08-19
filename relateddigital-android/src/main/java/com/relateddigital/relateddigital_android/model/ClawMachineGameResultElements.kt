package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ClawMachineGameResultElements : Serializable {

    @SerializedName("title"   ) var title   : String? = null
    @SerializedName("message" ) var message : String? = null
}