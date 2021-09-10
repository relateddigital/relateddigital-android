package com.relateddigital.relateddigital_android.inapp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.databinding.FragmentInAppBottomSheetBinding
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.network.RequestHandler
import java.util.*

class InAppBottomSheetFragment : BottomSheetDialogFragment() {
    private var mParent: Context? = null
    private var mInAppStateId = 0
    private var mInAppNotificationState: InAppNotificationState? = null
    private lateinit var binding: FragmentInAppBottomSheetBinding
    private var mInAppMessage: InAppMessage? = null

    companion object {
        private const val LOG_TAG = "InAppBottomSheetFrag"
        fun newInstance(): InAppBottomSheetFragment {
            return InAppBottomSheetFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentInAppBottomSheetBinding.inflate(inflater, container, false)
        val view: View = binding.root
        if (mInAppMessage == null) {
            cleanUp()
            return view
        }
        binding.tvTitle.text = mInAppMessage!!.mActionData!!.mMsgTitle!!.replace("\\n", "\n")
        binding.tvBody.text = mInAppMessage!!.mActionData!!.mMsgBody!!.replace("\\n", "\n")
        binding.tvButton.text = mInAppMessage!!.mActionData!!.mBtnText!!.toUpperCase(Locale.ROOT)
        binding.tvClose.text = mInAppMessage!!.mActionData!!.mCloseButtonText!!.toUpperCase(Locale.ROOT)
        setListeners()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val dialog = dialog as BottomSheetDialog?
                val bottomSheet: FrameLayout = dialog!!.findViewById(R.id.design_bottom_sheet)!!
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight =
                    0 // Remove this line to hide a dark background if you manually hide the dialog.
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mParent = context
        if (mInAppMessage == null || mInAppNotificationState == null) {
            Log.e(LOG_TAG, "InAppMessage is null! Could not get display state!")
            cleanUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        InAppUpdateDisplayState.releaseDisplayState(mInAppStateId)
    }

    fun setInAppState(stateId: Int, inAppState: InAppNotificationState?) {
        mInAppStateId = stateId
        mInAppNotificationState = inAppState
        if (mInAppNotificationState != null) {
            mInAppMessage = mInAppNotificationState!!.getInAppMessage()
        }
    }

    private fun setListeners() {
        binding.tvButton.setOnClickListener {
            val uriString: String? = mInAppMessage!!.mActionData!!.mAndroidLnk
            val buttonInterface: InAppButtonInterface? = RelatedDigital.getInAppButtonInterface()
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
                        Log.e(LOG_TAG, "Can't parse notification URI, will not take any action", e)
                    } catch (e: ActivityNotFoundException) {
                        Log.e(
                            LOG_TAG,
                            "User doesn't have an activity for notification URI $uri"
                        )
                    }
                }
            }
            RequestHandler.createInAppNotificationClickRequest(
                mParent!!,
                mInAppMessage,
                null
            )
            cleanUp()
        }
        binding.tvClose.setOnClickListener { cleanUp() }
    }

    private fun cleanUp() {
        InAppUpdateDisplayState.releaseDisplayState(mInAppStateId)
        dismiss()
    }
}