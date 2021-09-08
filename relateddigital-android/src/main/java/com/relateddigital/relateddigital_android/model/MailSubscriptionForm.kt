package com.relateddigital.relateddigital_android.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class MailSubscriptionForm() : Serializable, Parcelable {
    @SerializedName("actid")
    var actid: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("actiontype")
    var actiontype: String? = null

    @SerializedName("actiondata")
    var actiondata: MailSubActionData? = null

    constructor(parcel: Parcel) : this() {
        actid = parcel.readString()
        title = parcel.readString()
        actiontype = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(actid)
        parcel.writeString(title)
        parcel.writeString(actiontype)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MailSubscriptionForm> {
        override fun createFromParcel(parcel: Parcel): MailSubscriptionForm {
            return MailSubscriptionForm(parcel)
        }

        override fun newArray(size: Int): Array<MailSubscriptionForm?> {
            return arrayOfNulls(size)
        }
    }
}