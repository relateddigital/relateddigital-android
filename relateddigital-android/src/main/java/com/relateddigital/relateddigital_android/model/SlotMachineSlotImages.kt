package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SlotMachineSlotImages : Serializable{
    @SerializedName("image"      ) var image      : String? = null
    @SerializedName("staticcode" ) var staticcode : String? = null

}