package com.relateddigital.relateddigital_android.inapp.halfscreen

import android.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.databinding.FragmentHalfScreenBinding
import com.relateddigital.relateddigital_android.inapp.InAppButtonInterface
import com.relateddigital.relateddigital_android.inapp.InAppNotificationState
import com.relateddigital.relateddigital_android.inapp.InAppUpdateDisplayState
import com.relateddigital.relateddigital_android.inapp.inappmessages.InAppMiniFragment
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [HalfScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HalfScreenFragment : Fragment() {
    companion object{
        private const val LOG_TAG = "HalfScreenFragment"
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "stateIdKey"
        private const val ARG_PARAM2 = "inAppStateKey"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param stateId Parameter 1.
         * @param inAppState Parameter 2.
         * @return A new instance of fragment SocialProofFragment.
         */
        fun newInstance(stateId: Int, inAppState: InAppNotificationState?): HalfScreenFragment {
            val fragment = HalfScreenFragment()
            val args = Bundle()
            args.putInt(ARG_PARAM1, stateId)
            args.putParcelable(ARG_PARAM2, inAppState)
            fragment.arguments = args
            return fragment
        }
    }

    private var mStateId = 0
    private var mInAppState: InAppNotificationState? = null
    private var mInAppMessage: InAppMessage? = null
    private var mIsTop = false
    private lateinit var binding: FragmentHalfScreenBinding

    fun HalfScreenFragment() {
        // Required empty public constructor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStateId = arguments.getInt(ARG_PARAM1)
        mInAppState = arguments.getParcelable(ARG_PARAM2)
        if (mInAppState != null) {
            mInAppMessage = mInAppState!!.getInAppMessage()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentHalfScreenBinding.inflate(inflater!!, container, false)
        val view: View = binding.root
        if (mInAppState != null) {
            if (mInAppMessage == null) {
                endFragment()
                Log.e(LOG_TAG, "Could not get the data, closing in app")
            } else {
                setupInitialView()
            }
        } else {
            endFragment()
            Log.e(LOG_TAG, "Could not get the data, closing in app")
        }
        return view
    }

    private fun setupInitialView() {
        mIsTop = mInAppMessage!!.mActionData!!.mPos.equals("top")
        if (mIsTop) {
            adjustTop()
        } else {
            adjustBottom()
        }
        setupCloseButton()
    }

    private fun adjustTop() {
        if (!mInAppMessage!!.mActionData!!.mMsgTitle.isNullOrEmpty()) {
            binding.halfScreenContainerTop.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mBackground))
            binding.topTitleView.text = mInAppMessage!!.mActionData!!.mMsgTitle
            binding.topTitleView.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgTitleColor))
            binding.topTitleView.textSize = mInAppMessage!!.mActionData!!.mMsgTitleTextSize!!.toFloat() * 2 + 14
            binding.topTitleView.typeface = mInAppMessage!!.mActionData!!.getFontFamily()
        } else {
            binding.topTitleView.visibility = View.GONE
        }
        Picasso.get().load("https://brtk.net/wp-content/uploads/2021/08/28/30agustossss.jpg?ver=cf14dae8e18a0da9aee40b2c8f3f2b39")
                .into(binding.topImageView)
        binding.topImageView.setOnClickListener {
            val uriString: String? = mInAppMessage!!.mActionData!!.mAndroidLnk
            val buttonInterface: InAppButtonInterface? = RelatedDigital.getInAppButtonInterface()
            RequestHandler.createInAppNotificationClickRequest(activity, mInAppMessage, null)
            if (buttonInterface != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonInterface.onPress(uriString)
            } else {
                if (!uriString.isNullOrEmpty()) {
                    try {
                        val uri: Uri = Uri.parse(uriString)
                        val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                        activity.startActivity(viewIntent)
                    } catch (e: Exception) {
                        Log.i(LOG_TAG, "Can't parse notification URI, will not take any action", e)
                    }
                }
            }
            endFragment()
        }
        binding.halfScreenContainerBot.visibility = View.GONE
    }

    private fun adjustBottom() {
        if (!mInAppMessage!!.mActionData!!.mMsgTitle.isNullOrEmpty()) {
            binding.halfScreenContainerBot.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mBackground))
            binding.botTitleView.text = mInAppMessage!!.mActionData!!.mMsgTitle
            binding.botTitleView.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgTitleColor))
            binding.botTitleView.textSize = mInAppMessage!!.mActionData!!.mMsgTitleTextSize!!.toFloat() * 2 + 14
            binding.botTitleView.typeface = mInAppMessage!!.mActionData!!.getFontFamily()
        } else {
            binding.botTitleView.visibility = View.GONE
        }
        Picasso.get().load("https://brtk.net/wp-content/uploads/2021/08/28/30agustossss.jpg?ver=cf14dae8e18a0da9aee40b2c8f3f2b39")
                .into(binding.botImageView)
        binding.botImageView.setOnClickListener {
            val uriString: String? = mInAppMessage!!.mActionData!!.mAndroidLnk
            val buttonInterface: InAppButtonInterface? = RelatedDigital.getInAppButtonInterface()
            RequestHandler.createInAppNotificationClickRequest(activity, mInAppMessage, null)
            if (buttonInterface != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonInterface.onPress(uriString)
            } else {
                if (!uriString.isNullOrEmpty()) {
                    try {
                        val uri: Uri = Uri.parse(uriString)
                        val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                        activity.startActivity(viewIntent)
                    } catch (e: Exception) {
                        Log.i(LOG_TAG, "Can't parse notification URI, will not take any action", e)
                    }
                }
            }
            endFragment()
        }
        binding.halfScreenContainerTop.visibility = View.GONE
    }

    private fun setupCloseButton() {
        if (mIsTop) {
            binding.topCloseButton.setBackgroundResource(getCloseIcon())
            binding.topCloseButton.setOnClickListener { endFragment() }
        } else {
            binding.botCloseButton.setBackgroundResource(getCloseIcon())
            binding.botCloseButton.setOnClickListener { endFragment() }
        }
    }

    private fun getCloseIcon(): Int {
        when (mInAppMessage!!.mActionData!!.mCloseButtonColor) {
            "white" -> return R.drawable.ic_close_white_24dp
            "black" -> return R.drawable.ic_close_black_24dp
        }
        return R.drawable.ic_close_black_24dp
    }

    private fun endFragment() {
        if (activity != null) {
            InAppUpdateDisplayState.releaseDisplayState(mStateId)
            activity.fragmentManager.beginTransaction().remove(this@HalfScreenFragment).commit()
        }
    }
}