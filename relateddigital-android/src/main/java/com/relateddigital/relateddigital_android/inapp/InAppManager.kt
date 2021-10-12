package com.relateddigital.relateddigital_android.inapp

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.inapp.inappmessages.*
import com.relateddigital.relateddigital_android.inapp.mailsubsform.MailSubscriptionFormActivity
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.model.MailSubscriptionForm
import com.relateddigital.relateddigital_android.util.ActivityImageUtils
import java.util.concurrent.locks.ReentrantLock

class InAppManager(
    private val mCookieID: String,
    private val mDataSource: String
) {
    companion object {
        private const val LOG_TAG = "InAppManager"
    }

    fun showMailSubscriptionForm(mailSubscriptionForm: MailSubscriptionForm?, parent: Activity) {
        parent.runOnUiThread {
            val lock: ReentrantLock =
                InAppUpdateDisplayState.getLockObject()
            lock.lock()
            try {
                if (InAppUpdateDisplayState.hasCurrentProposal()) {
                    Log.e(LOG_TAG, "DisplayState is locked, will not show notifications")
                } else {
                    val intent = Intent(parent, MailSubscriptionFormActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    intent.putExtra(
                        Constants.INTENT_ID_KEY, getStateId(
                            parent,
                            mailSubscriptionForm!!
                        )
                    )
                    parent.startActivity(intent)
                }
            } catch (ex: java.lang.Exception) {
                Log.e(LOG_TAG, ex.message, ex)
            } finally {
                lock.unlock()
            }
        }
    }

    fun showInAppMessage(inAppMessage: InAppMessage, parent: Activity) {
        parent.runOnUiThread(Runnable {
            val lock: ReentrantLock = InAppUpdateDisplayState.getLockObject()
            lock.lock()
            try {
                var willShowInApp = true
                if (InAppUpdateDisplayState.hasCurrentProposal()) {
                    Log.w(LOG_TAG, "DisplayState is locked, will not show notifications")
                    willShowInApp = false
                }
                if (inAppMessage.mActionData!!.mMsgType == null) {
                    Log.w(LOG_TAG, "No in app available, will not show.")
                    willShowInApp = false
                }
                if (!willShowInApp) {
                    return@Runnable
                }
                val intent = Intent(parent, InAppNotificationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                val stateId: Int
                val inAppUpdateDisplayState: InAppUpdateDisplayState?
                when (inAppMessage.mActionData!!.mMsgType) {
                    InAppNotificationType.UNKNOWN.toString() -> {

                    }
                    InAppNotificationType.MINI.toString() -> {
                        stateId = getStateId(parent, inAppMessage)
                        inAppUpdateDisplayState = InAppUpdateDisplayState.claimDisplayState(
                            stateId
                        )
                        if (inAppUpdateDisplayState == null) {
                            Log.w(
                                LOG_TAG,
                                "Notification's display proposal was already consumed, no notification will be shown."
                            )
                        } else {
                            openInAppMiniFragment(stateId, parent, inAppUpdateDisplayState)
                        }
                    }

                    InAppNotificationType.FULL.toString() -> {
                        openInAppActivity(parent, getStateId(parent, inAppMessage))
                    }

                    InAppNotificationType.FULL_IMAGE.toString(),
                    InAppNotificationType.SMILE_RATING.toString(),
                    InAppNotificationType.NPS.toString(), InAppNotificationType.IMAGE_TEXT_BUTTON.toString(),
                    InAppNotificationType.NPS_WITH_NUMBERS.toString(), InAppNotificationType.IMAGE_BUTTON.toString(),
                    InAppNotificationType.CAROUSEL.toString(), InAppNotificationType.NPS_AND_SECOND_POP_UP.toString() -> {
                        intent.putExtra(
                            Constants.INTENT_ID_KEY, getStateId(
                                parent,
                                inAppMessage
                            )
                        )
                        parent.startActivity(intent)
                    }
                    InAppNotificationType.ALERT.toString() -> {
                        stateId = getStateId(parent, inAppMessage)
                        inAppUpdateDisplayState = InAppUpdateDisplayState.claimDisplayState(
                            stateId
                        )
                        if (inAppUpdateDisplayState == null) {
                            Log.w(
                                LOG_TAG,
                                "Notification's display proposal was already consumed, no notification will be shown."
                            )
                        } else {
                            if (inAppMessage.mActionData!!.mAlertType.equals("actionSheet")) {
                                openInAppActionSheet(stateId, parent, inAppUpdateDisplayState)
                            } else {
                                openInAppAlert(stateId, parent, inAppUpdateDisplayState)
                            }
                        }
                    }
                    else -> Log.e(
                        LOG_TAG,
                        "Unrecognized notification type " + inAppMessage.mActionData
                        !!.mMsgType.toString() + " can't be shown"
                    )
                }
            } catch (ex: Exception) {
                Log.e(LOG_TAG, ex.message, ex)
            } finally {
                lock.unlock()
            }
        })
    }

    private fun getStateId(parent: Activity, mailSubscriptionForm: MailSubscriptionForm): Int {
        val highlightColor: Int = ActivityImageUtils.getHighlightColorFromBackground(parent)
        val inAppNotificationState = InAppNotificationState(mailSubscriptionForm, highlightColor)
        val stateID: Int = InAppUpdateDisplayState.proposeDisplay(
            inAppNotificationState,
            mCookieID,
            mDataSource
        )
        if (stateID <= 0) {
            Log.e(LOG_TAG, "DisplayState Lock in inconsistent state!")
        }
        return stateID
    }

    private fun getStateId(parent: Activity, inAppMessage: InAppMessage): Int {
        val highlightColor: Int = ActivityImageUtils.getHighlightColorFromBackground(parent)
        val inAppNotificationState = InAppNotificationState(inAppMessage, highlightColor)
        val stateID: Int = InAppUpdateDisplayState.proposeDisplay(
            inAppNotificationState,
            mCookieID,
            mDataSource
        )
        if (stateID <= 0) {
            Log.e(LOG_TAG, "DisplayState Lock in inconsistent state!")
        }
        return stateID
    }

    private fun openInAppMiniFragment(
        stateID: Int,
        parent: Activity,
        inAppUpdateDisplayState: InAppUpdateDisplayState
    ) {
        val inAppMiniFragment = InAppMiniFragment()
        if (inAppUpdateDisplayState.getDisplayState() != null) {
            inAppMiniFragment.setInAppState(
                stateID,
                inAppUpdateDisplayState.getDisplayState() as InAppNotificationState
            )
            inAppMiniFragment.retainInstance = true
            val transaction = parent.fragmentManager.beginTransaction()
            transaction.add(android.R.id.content, inAppMiniFragment)
            transaction.commit()
        }
    }

    private fun openInAppActivity(parent: Activity, inAppData: Int) {
        val intent = Intent(parent.applicationContext, InAppFullActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.putExtra(InAppFullActivity.INTENT_ID_KEY, inAppData)
        parent.startActivity(intent)
    }

    private fun openInAppAlert(
        stateID: Int,
        parent: Activity,
        inAppUpdateDisplayState: InAppUpdateDisplayState
    ) {
        if (inAppUpdateDisplayState.getDisplayState() == null) {
            InAppUpdateDisplayState.releaseDisplayState(stateID)
            return
        }
        if (parent is FragmentActivity) {
            val inAppAlertFragment: InAppAlertFragment = InAppAlertFragment.newInstance()
            inAppAlertFragment.isCancelable = false
            inAppAlertFragment.setInAppState(
                stateID,
                inAppUpdateDisplayState.getDisplayState() as InAppNotificationState,
                parent
            )
            inAppAlertFragment.show(parent.supportFragmentManager, "InAppAlertFragment")
        } else {
            InAppUpdateDisplayState.releaseDisplayState(stateID)
        }
    }

    private fun openInAppActionSheet(
        stateID: Int,
        parent: Activity,
        inAppUpdateDisplayState: InAppUpdateDisplayState
    ) {
        if (inAppUpdateDisplayState.getDisplayState() == null) {
            InAppUpdateDisplayState.releaseDisplayState(stateID)
            return
        }
        if (parent is FragmentActivity) {
            val inAppBottomSheetFragment: InAppBottomSheetFragment = InAppBottomSheetFragment.newInstance()
            inAppBottomSheetFragment.isCancelable = false
            inAppBottomSheetFragment.setInAppState(
                stateID,
                inAppUpdateDisplayState.getDisplayState() as InAppNotificationState
            )
            inAppBottomSheetFragment.show(parent.supportFragmentManager, "InAppBottomSheetFragment")
        } else {
            InAppUpdateDisplayState.releaseDisplayState(stateID)
        }
    }
}