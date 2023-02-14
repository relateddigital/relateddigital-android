package com.relateddigital.relateddigital_android_core.inapp

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.util.concurrent.locks.ReentrantLock

class InAppUpdateDisplayState: Parcelable {
    companion object {
        private const val LOG_TAG = "InAppDisplayState"
        private const val MAX_LOCK_TIME_MILLIS = (12 * 60 * 60 * 1000 // Twelve hour timeout on notification activities
                ).toLong()

        private const val DISTINCT_ID_BUNDLE_KEY = "VisilabsUpdateDisplayState.DISTINCT_ID_BUNDLE_KEY"
        private const val TOKEN_BUNDLE_KEY = "VisilabsUpdateDisplayState.TOKEN_BUNDLE_KEY"
        private const val DISPLAYSTATE_BUNDLE_KEY = "VisilabsUpdateDisplayState.DISPLAYSTATE_BUNDLE_KEY"

        private val mUpdateDisplayLock = ReentrantLock()
        private var mUpdateDisplayLockMillis: Long = -1
        private var mInAppDisplayState: InAppUpdateDisplayState? = null
        private var mNextIntentId = 0
        private var mShowingIntentId = -1

        fun getLockObject(): ReentrantLock {
            return mUpdateDisplayLock
        }

        fun hasCurrentProposal(): Boolean {
            if (!mUpdateDisplayLock.isHeldByCurrentThread) throw AssertionError()
            val currentTime = System.currentTimeMillis()
            val deltaTime = currentTime - mUpdateDisplayLockMillis
            if (mNextIntentId > 0 && deltaTime > MAX_LOCK_TIME_MILLIS) {
                Log.i(
                    LOG_TAG,
                    "UpdateDisplayState set long, long ago, without showing. Update state will be cleared."
                )
                mInAppDisplayState = null
            }
            return null != mInAppDisplayState
        }

        fun proposeDisplay(state: InAppDisplayState, distinctId: String, token: String): Int {
            var ret = -1
            if (!mUpdateDisplayLock.isHeldByCurrentThread) throw AssertionError()
            if (!hasCurrentProposal()) {
                mUpdateDisplayLockMillis = System.currentTimeMillis()
                mInAppDisplayState = InAppUpdateDisplayState(
                    state,
                    distinctId,
                    token
                )
                mNextIntentId++
                ret = mNextIntentId
            } else {
                Log.v(LOG_TAG, "Already showing a Visilabs update, declining to show another.")
            }
            return ret
        }

        fun releaseDisplayState(intentId: Int) {
            mUpdateDisplayLock.lock()
            try {
                if (intentId == mShowingIntentId) {
                    mShowingIntentId = -1
                    mInAppDisplayState = null
                }
            } finally {
                mUpdateDisplayLock.unlock()
            }
        }

        fun claimDisplayState(intentId: Int): InAppUpdateDisplayState? {
            mUpdateDisplayLock.lock()
            return try {
                if (mShowingIntentId > 0 && mShowingIntentId != intentId) {
                    null
                } else if (mInAppDisplayState == null) {
                    null
                } else {
                    mUpdateDisplayLockMillis = System.currentTimeMillis()
                    mShowingIntentId = intentId
                    mInAppDisplayState
                }
            } finally {
                mUpdateDisplayLock.unlock()
            }
        }

        @JvmField var CREATOR: Parcelable.Creator<InAppUpdateDisplayState> =
            object : Parcelable.Creator<InAppUpdateDisplayState> {
                override fun createFromParcel(`in`: Parcel): InAppUpdateDisplayState {
                    val read =
                        Bundle(InAppUpdateDisplayState::class.java.getClassLoader())
                    read.readFromParcel(`in`)
                    return InAppUpdateDisplayState(read)
                }

                override fun newArray(size: Int): Array<InAppUpdateDisplayState?> {
                    return arrayOfNulls<InAppUpdateDisplayState>(size)
                }
            }
    }

    private val mDistinctId: String
    private val mToken: String
    private var mDisplayState: InAppDisplayState? = null

    constructor(read: Bundle) {
        mDistinctId = read.getString(DISTINCT_ID_BUNDLE_KEY).toString()
        mToken = read.getString(TOKEN_BUNDLE_KEY).toString()
        mDisplayState = read.getParcelable(DISPLAYSTATE_BUNDLE_KEY)!!
    }

    constructor(displayState: InAppDisplayState, distinctId: String, token: String) {
        mDistinctId = distinctId
        mToken = token
        mDisplayState = displayState
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        val bundle = Bundle()
        bundle.putString(DISTINCT_ID_BUNDLE_KEY, mDistinctId)
        bundle.putString(TOKEN_BUNDLE_KEY, mToken)
        bundle.putParcelable(DISPLAYSTATE_BUNDLE_KEY, mDisplayState)
        dest.writeBundle(bundle)
    }

    fun getDisplayState(): InAppDisplayState? {
        return mDisplayState
    }

    fun getDistinctId(): String {
        return mDistinctId
    }

    fun getToken(): String {
        return mToken
    }
}