package com.relateddigital.relateddigital_android.inapp.notification

import android.app.Fragment
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.FragmentNotificationSmallBinding
import com.relateddigital.relateddigital_android.inapp.InAppNotificationState
import com.relateddigital.relateddigital_android.inapp.InAppUpdateDisplayState
import com.relateddigital.relateddigital_android.model.InAppMessage

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationSmallFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationSmallFragment : Fragment() {
    private var mStateId = 0
    private var mInAppState: InAppNotificationState? = null
    private var mInAppMessage: InAppMessage? = null
    private var mIsRight = false
    private lateinit var binding: FragmentNotificationSmallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStateId = arguments.getInt(ARG_PARAM1)
        mInAppState = arguments.getParcelable(ARG_PARAM2)
        if (mInAppState != null) {
            mInAppMessage = mInAppState!!.getInAppMessage()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater?, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationSmallBinding.inflate(inflater!!, container, false)
        val view: View = binding.root

        // TODO: Open this
        /*if (mInAppState != null) {
            if(mInAppMessage == null) {
                endFragment();
                Log.e(LOG_TAG, "Could not get the data, closing in app");
            } else {
                setupInitialView();
            }

        } else {
            endFragment();
            Log.e(LOG_TAG, "Could not get the data, closing in app");
        }*/
        setupInitialView()
        return view
    }

    private fun setupInitialView() {

        //TODO : get real value of mIsRight
        mIsRight = true
        if (mIsRight) {
            adjustRight()
        } else {
            adjustLeft()
        }
    }

    private fun adjustRight() {
        //TODO : real data here
        val gd = binding.rightContainer.background as GradientDrawable
        gd.setColor(ResourcesCompat.getColor(resources, R.color.blue, null))
        binding.rightContainer.setBackgroundResource(R.drawable.rounded_corners_left)
        binding.rightArrowView.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
        binding.rightArrowView.textSize = 32f
        binding.rightTitleView.text = "Discount"
        binding.rightTitleView.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
        binding.rightTitleView.textSize = 32f
        binding.rightContainer.setOnClickListener {
            endFragment()
            //TODO : Open notification big fragment here
        }
        binding.leftContainer.visibility = View.GONE
    }

    private fun adjustLeft() {
        //TODO : real data here
        val gd = binding.leftContainer.background as GradientDrawable
        gd.setColor(ResourcesCompat.getColor(resources, R.color.blue, null))
        binding.leftArrowView.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
        binding.leftArrowView.textSize = 32f
        binding.leftTitleView.text = "Discount"
        binding.leftTitleView.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
        binding.leftTitleView.textSize = 32f
        binding.leftContainer.setOnClickListener {
            endFragment()
            //TODO : Open notification big fragment here
        }
        binding.rightContainer.visibility = View.GONE
    }

    private fun endFragment() {
        if (activity != null) {
            InAppUpdateDisplayState.releaseDisplayState(mStateId)
            activity.fragmentManager.beginTransaction().remove(this@NotificationSmallFragment)
                .commit()
        }
    }

    companion object {
        private const val LOG_TAG = "NotificationSFragment"

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
        fun newInstance(
            stateId: Int,
            inAppState: InAppNotificationState?
        ): NotificationSmallFragment {
            val fragment = NotificationSmallFragment()
            val args = Bundle()
            args.putInt(ARG_PARAM1, stateId)
            args.putParcelable(ARG_PARAM2, inAppState)
            fragment.arguments = args
            return fragment
        }
    }
}