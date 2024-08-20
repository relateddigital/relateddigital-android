package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.ArrayList

class ClawMachine : Serializable {
    @SerializedName("actid"      ) var actid      : Int?        = null
    @SerializedName("title"      ) var title      : String?     = null
    @SerializedName("actiontype" ) var actiontype : String?     = null
    @SerializedName("actiondata" ) var actiondata : ClawMachineActionData? = null
    @SerializedName("panelv2"    ) var panelv2    : Boolean?    = null
}