package com.relateddigital.relateddigital_android.inapp.inappmessages

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
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.inapp.InAppButtonInterface
import com.relateddigital.relateddigital_android.inapp.InAppNotificationState
import com.relateddigital.relateddigital_android.inapp.InAppUpdateDisplayState
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.InAppNotificationClickRequest

class InAppAlertFragment : DialogFragment() {
    private var mInAppStateId = 0
    private var mInAppNotificationState: InAppNotificationState? = null
    private var mInAppMessage: InAppMessage? = null
    private var mContext: Context? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
        val actionData = mInAppMessage?.mActionData
        if (actionData == null) {
            cleanUp()
        } else {
            alertDialogBuilder.setTitle(
                actionData.mMsgTitle.orEmpty().replace("\\n", "\n")
            )
                .setMessage(actionData.mMsgBody.orEmpty().replace("\\n", "\n"))
                .setCancelable(false)
                .setPositiveButton(actionData.mBtnText
                ) { _, _ ->
                    val uriString: String? = actionData.mAndroidLnk
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
                                activity?.startActivity(viewIntent)
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
                    mContext?.let {
                        InAppNotificationClickRequest.createInAppNotificationClickRequest(
                            it,
                            mInAppMessage,
                            null
                        )
                    }
                    InAppUpdateDisplayState.releaseDisplayState(mInAppStateId)
                    dismissAllowingStateLoss()
                }
                .setNegativeButton(actionData.mCloseButtonText
                ) { _, _ ->
                    InAppUpdateDisplayState.releaseDisplayState(mInAppStateId)
                    dismissAllowingStateLoss()
                }
        }
        return alertDialogBuilder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        if (mInAppNotificationState == null) {
            arguments?.let { args ->
                mInAppStateId = args.getInt(ARG_STATE_ID)
                mInAppNotificationState = args.getParcelable(ARG_STATE)
                mInAppMessage = mInAppNotificationState?.getInAppMessage()
            }
        }
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
        arguments = Bundle().apply {
            putInt(ARG_STATE_ID, stateId)
            putParcelable(ARG_STATE, inAppState)
        }
    }

    private fun cleanUp() {
        InAppUpdateDisplayState.releaseDisplayState(mInAppStateId)
        dismissAllowingStateLoss()
    }

    companion object {
        private const val LOG_TAG = "InAppAlertFragment"
        private const val ARG_STATE_ID = "in_app_alert_state_id"
        private const val ARG_STATE = "in_app_alert_state"
        fun newInstance(): InAppAlertFragment {
            return InAppAlertFragment()
        }
    }
}
