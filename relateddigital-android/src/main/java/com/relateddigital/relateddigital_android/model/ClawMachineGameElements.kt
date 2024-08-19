package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ClawMachineGameElements : Serializable {

    @SerializedName("toy_images"           ) var toyImages           : ArrayList<ClawMachineToyImages> = arrayListOf()
    @SerializedName("clawmachine_image"    ) var clawmachineImage    : String?              = null
    @SerializedName("toysbackground_image" ) var toysbackgroundImage : String?              = null
    @SerializedName("catchbutton_label"    ) var catchbuttonLabel    : String?              = null
}