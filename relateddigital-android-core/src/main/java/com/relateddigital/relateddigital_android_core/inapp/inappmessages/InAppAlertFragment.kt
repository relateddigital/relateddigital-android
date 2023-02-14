package com.relateddigital.relateddigital_android_core.inapp.inappmessages

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.relateddigital.relateddigital_android_core.R
import com.relateddigital.relateddigital_android_core.RelatedDigital
import com.relateddigital.relateddigital_android_core.inapp.InAppButtonInterface
import com.relateddigital.relateddigital_android_core.inapp.InAppNotificationState
import com.relateddigital.relateddigital_android_core.inapp.InAppUpdateDisplayState
import com.relateddigital.relateddigital_android_core.model.InAppMessage
import com.relateddigital.relateddigital_android_core.network.RequestHandler

class InAppAlertFragment : DialogFragment() {
    private var mInAppStateId = 0
    private var mInAppNotificationState: InAppNotificationState? = null
    private var mParent: Activity? = null
    private var mInAppMessage: InAppMessage? = null
    private var mContext: Context? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(mParent!!)
        if (mInAppMessage == null) {
            cleanUp()
        } else {
            alertDialogBuilder.setTitle(
                mInAppMessage!!.mActionData!!.mMsgTitle!!.replace("\\n", "\n")
            )
                .setMessage(mInAppMessage!!.mActionData!!.mMsgBody!!.replace("\\n", "\n"))
                .setCancelable(false)
                .setPositiveButton(mInAppMessage!!.mActionData!!.mBtnText
                ) { _, _ ->
                    val uriString: String? = mInAppMessage!!.mActionData!!.mAndroidLnk
                    val buttonInterface: InAppButtonInterface? =
                        RelatedDigital.getInAppButtonInterface()
                    if (buttonInterface != null) {
                        RelatedDigital.setInAppButtonInterface(null)
                        buttonInterface.onPress(uriString)
                    } else {
                        var uri: Uri? = null
                        if (!uriString.isNullOrEmpty()) {
                            try {
                                uri = Uri.parse(uriString)
                                val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                                mParent!!.startActivity(viewIntent)
                            } catch (e: IllegalArgumentException) {
                                Log.e(
                                    LOG_TAG,
                                    "Can't parse notification URI, will not take any action",
                                    e
                                )
                            } catch (e: ActivityNotFoundException) {
                                Log.e(
                                    LOG_TAG,
                                    "User doesn't have an activity for notification URI $uri"
                                )
                            }
                        }
                    }
                    RequestHandler.createInAppNotificationClickRequest(
                        mContext!!,
                        mInAppMessage,
                        null
                    )
                    InAppUpdateDisplayState.releaseDisplayState(mInAppStateId)
                    dismiss()
                }
                .setNegativeButton(mInAppMessage!!.mActionData!!.mCloseButtonText
                ) { _, _ ->
                    InAppUpdateDisplayState.releaseDisplayState(mInAppStateId)
                    dismiss()
                }
        }
        return alertDialogBuilder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        if (mInAppMessage == null || mInAppNotificationState == null) {
            Log.e(LOG_TAG, "InAppMessage is null! Could not get display state!")
            cleanUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        InAppUpdateDisplayState.releaseDisplayState(mInAppStateId)
    }

    fun setInAppState(stateId: Int, inAppState: InAppNotificationState?, parent: Activity?) {
        mInAppStateId = stateId
        mInAppNotificationState = inAppState
        if (mInAppNotificationState != null) {
            mInAppMessage = mInAppNotificationState!!.getInAppMessage()
        }
        mParent = parent
    }

    private fun cleanUp() {
        InAppUpdateDisplayState.releaseDisplayState(mInAppStateId)
        dismiss()
    }

    companion object {
        private const val LOG_TAG = "InAppAlertFragment"
        fun newInstance(): InAppAlertFragment {
            return InAppAlertFragment()
        }
    }
}