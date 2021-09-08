package com.relateddigital.relateddigital_android.inapp

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

abstract class InAppDisplayState : Parcelable {
    abstract val type: String

    companion object {
        private const val STATE_TYPE_KEY = "STATE_TYPE_KEY"
        private const val STATE_IMPL_KEY = "STATE_IMPL_KEY"
        val CREATOR: Parcelable.Creator<InAppDisplayState> = object :
            Parcelable.Creator<InAppDisplayState> {
            override fun createFromParcel(source: Parcel): InAppDisplayState {
                val read = Bundle(InAppDisplayState::class.java.classLoader)
                read.readFromParcel(source)
                val type = read.getString(STATE_TYPE_KEY)
                val implementation = read.getBundle(STATE_IMPL_KEY)
                return if (InAppNotificationState.TYPE == type) {
                    InAppNotificationState(implementation!!)
                } else {
                    throw RuntimeException("Unrecognized display state type $type")
                }
            }

            override fun newArray(size: Int): Array<InAppDisplayState?> {
                return arrayOfNulls(size)
            }
        }
    }
}