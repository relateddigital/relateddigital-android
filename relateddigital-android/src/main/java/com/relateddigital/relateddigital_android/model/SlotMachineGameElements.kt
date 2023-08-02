package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SlotMachineGameElements : Serializable {
    @SerializedName("slot_images"       ) var slotImages       : ArrayList<SlotMachineSlotImages> = arrayListOf()
    @SerializedName("slotmachine_image" ) var slotmachineImage : String?               = null
    @SerializedName("spinbutton_label"  ) var spinbuttonLabel  : String?               = null
    @SerializedName("duration_of_game"  ) var durationOfGame   : Int?                  = null
    @SerializedName("sound_url"         ) var soundUrl         : String?               = null
}