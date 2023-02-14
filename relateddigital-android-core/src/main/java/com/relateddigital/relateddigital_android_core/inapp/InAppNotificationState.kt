package com.relateddigital.relateddigital_android_core.inapp

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.relateddigital.relateddigital_android_core.model.InAppMessage
import com.relateddigital.relateddigital_android_core.model.MailSubscriptionForm
import kotlin.properties.Delegates

class InAppNotificationState: InAppDisplayState {
    private var mInAppNotification: InAppMessage? = null
    private var mMailSubscriptionForm: MailSubscriptionForm? = null
    private var mHighlightColor by Delegates.notNull<Int>()

    constructor(`in`: Bundle) {
        mInAppNotification = `in`.getParcelable(INAPP_KEY)
        mHighlightColor = `in`.getInt(HIGHLIGHT_KEY)
    }

    constructor(inAppMessage: InAppMessage?, highlightColor: Int) {
        this.mInAppNotification = inAppMessage
        this.mHighlightColor = highlightColor
    }

    constructor(mailSubscriptionForm: MailSubscriptionForm?, highlightColor: Int) {
        this.mMailSubscriptionForm = mailSubscriptionForm
        this.mHighlightColor = highlightColor
    }

    fun getHighlightColor(): Int {
        return mHighlightColor
    }

    fun getInAppMessage(): InAppMessage? {
        return mInAppNotification
    }

    fun getMailSubscriptionForm(): MailSubscriptionForm? {
        return mMailSubscriptionForm
    }

    override val type: String
        get() = TYPE

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        val write = Bundle()
        if (mMailSubscriptionForm != null) {
            write.putParcelable(INAPP_KEY, mMailSubscriptionForm)
        } else {
            write.putParcelable(INAPP_KEY, mInAppNotification)
        }
        write.putInt(HIGHLIGHT_KEY, mHighlightColor)
        dest.writeBundle(write)
    }

    companion object {
        const val TYPE = "InAppNotificationState"
        private const val INAPP_KEY = "INAPP_KEY"
        private const val HIGHLIGHT_KEY = "HIGHLIGHT_KEY"

        @JvmField var CREATOR: Parcelable.Creator<InAppNotificationState> = object : Parcelable.Creator<InAppNotificationState> {
            override fun createFromParcel(source: Parcel?): InAppNotificationState {
                val read = Bundle(InAppNotificationState::class.java.classLoader)
                read.readFromParcel(source)
                return InAppNotificationState(read)
            }

            override fun newArray(size: Int): Array<InAppNotificationState?> {
                return arrayOfNulls(size)
            }
        }
    }
}