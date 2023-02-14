package com.relateddigital.relateddigital_android_core.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class InAppMessage() : Serializable, Parcelable {
    @SerializedName("actid")
    var mActId: Int? = null

    @SerializedName("actiondata")
    var mActionData: ActionData? = null

    @SerializedName("actiontype")
    var mActionType: String? = null

    @SerializedName("title")
    var mTitle: String? = null

    constructor(parcel: Parcel) : this() {
        mActId = parcel.readValue(Int::class.java.classLoader) as? Int
        mActionType = parcel.readString()
        mTitle = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(mActId)
        parcel.writeString(mActionType)
        parcel.writeString(mTitle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InAppMessage> {
        override fun createFromParcel(parcel: Parcel): InAppMessage {
            return InAppMessage(parcel)
        }

        override fun newArray(size: Int): Array<InAppMessage?> {
            return arrayOfNulls(size)
        }
    }
}