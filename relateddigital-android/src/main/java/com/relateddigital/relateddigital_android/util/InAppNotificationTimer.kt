package com.relateddigital.relateddigital_android.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.inapp.InAppManager
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.model.RelatedDigitalModel
import java.util.*

class InAppNotificationTimer(
        private val mType: String? = null,
        private val mActId: Int = 0,
        private val mMessages: List<InAppMessage>? = null,
        private val mParent: Activity? = null,
        private val model: RelatedDigitalModel,
        private val mContext: Context
): TimerTask() {
    companion object {
        private const val LOG_TAG = "InAppNotificationTimer"
    }

    private var mMessage: InAppMessage? = null

    init {
        selectMessage()
    }

    private fun selectMessage() {
        if (mActId > 0) {
            for (i in mMessages!!.indices) {
                if (mMessages[i].mActId == this.mActId) {
                    mMessage = mMessages[i]
                    break
                }
            }
        }
        if (mMessage == null && mType != null) {
            for (i in mMessages!!.indices) {
                if (mMessages[i].mActionData!!.mMsgType.toString() == mType) {
                    mMessage = mMessages[i]
                    break
                }
            }
        }
        if (mMessage == null && mMessages!!.isNotEmpty()) {
            mMessage = mMessages[0]
        }
    }

    fun getMessage(): InAppMessage? {
        return mMessage
    }

    override fun run() {
        if (mParent == null) {
            Log.w(LOG_TAG, "Could not display the in-app notification since the user has changed the original page!")
            cancel()
            return
        }
        if (mMessage != null) {
            if (!mMessage!!.mActionData!!.mVisitData.isNullOrEmpty()) {
                Log.i("mVisitData", mMessage!!.mActionData!!.mVisitData!!)
                model.setVisitData(mContext, mMessage!!.mActionData!!.mVisitData!!)
            }
            if (mMessage!!.mActionData!!.mVisitorData != null && !mMessage!!.mActionData!!.mVisitorData.equals("")) {
                Log.i("mVisitorData", mMessage!!.mActionData!!.mVisitorData!!)
                model.setVisitorData(mContext, mMessage!!.mActionData!!.mVisitorData!!)
            }
            InAppManager(model.getCookieId()!!, model.getDataSource()).showInAppMessage(mMessage!!, mParent)
        }
        cancel()
    }
}