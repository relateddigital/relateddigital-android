package com.relateddigital.relateddigital_android.inapp

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.FragmentSocialProofBinding
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [SocialProofFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SocialProofFragment : Fragment() {
    private var mStateId = 0
    private var mInAppState: InAppNotificationState? = null
    private var mIsTop = false
    private var mTimer: Timer? = null
    private lateinit var binding: FragmentSocialProofBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStateId = arguments.getInt(ARG_PARAM1)
        mInAppState = arguments.getParcelable(ARG_PARAM2)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentSocialProofBinding.inflate(inflater, container, false)
        val view: View = binding.root
        if (savedInstanceState != null) {
            //TODO: get the json string here
        } else {
            //TODO: get the json string here
        }
        if (isUnderThreshold) {
            endFragment()
        }
        setupInitialView()
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //TODO: save the json string here
    }

    private fun setupInitialView() {
        //Check the position and assign it to mIsTop
        mIsTop = true
        if (mIsTop) {
            adjustTop()
        } else {
            adjustBottom()
        }
        //TODO check if there is timer
        setTimer()
        setupCloseButton()
    }

    private fun adjustTop() {
        //TODO remove the code below when the actual data gets ready
        binding.socialProofContainerTop.setBackgroundColor(resources.getColor(R.color.yellow))
        binding.numberTextViewTop.setTextColor(resources.getColor(R.color.design_default_color_error))
        binding.explanationTextViewTop.setTextColor(resources.getColor(R.color.bottom_sheet_button_color))
        binding.socialProofContainerBot.setVisibility(View.GONE)
    }

    private fun adjustBottom() {
        //TODO remove the code below when the actual data gets ready
        binding.socialProofContainerBot.setBackgroundColor(resources.getColor(R.color.yellow))
        binding.numberTextViewBot.setTextColor(resources.getColor(R.color.design_default_color_error))
        binding.explanationTextViewBot.setTextColor(resources.getColor(R.color.bottom_sheet_button_color))
        binding.socialProofContainerTop.visibility = View.GONE
    }

    private fun setTimer() {
        //TODO check the data if it is "will stay until clicked"
        mTimer = Timer("SocialProofTimer", false)
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                endFragment()
            }
        }
        mTimer!!.schedule(task, 5000) // TODO instead of dummy here, put real data.

        //TODO If will stay until clicked, then
        /*if(mIsTop){
            binding.socialProofContainerTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    endFragment();
                }
            });
        } else {
            binding.socialProofContainerBot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    endFragment();
                }
            });
        }*/
    }

    private fun setupCloseButton() {
        //TODO check if close button will be displayed first
        if (mIsTop) {
            binding.closeButtonTop.setBackgroundResource(closeIcon)
            binding.closeButtonTop.setOnClickListener { endFragment() }
        } else {
            binding.closeButtonBot.setBackgroundResource(closeIcon)
            binding.closeButtonBot.setOnClickListener { endFragment() }
        }
    }

    //TODO when real data comes:
    /* switch (mInAppMessage.getActionData().getCloseButtonColor()) {

         case "white":
             return R.drawable.ic_close_white_24dp;

         case "black":
             return R.drawable.ic_close_black_24dp;
     }
     return R.drawable.ic_close_black_24dp;*/
    private val closeIcon: Int
        get() = R.drawable.ic_close_black_24dp
    //TODO when real data comes:
    /* switch (mInAppMessage.getActionData().getCloseButtonColor()) {

         case "white":
             return R.drawable.ic_close_white_24dp;

         case "black":
             return R.drawable.ic_close_black_24dp;
     }
     return R.drawable.ic_close_black_24dp;*/

    //TODO Check if the number is smaller than the threshold
    private val isUnderThreshold: Boolean
        get() =//TODO Check if the number is smaller than the threshold
            false

    private fun endFragment() {
        if (mTimer != null) {
            mTimer!!.cancel()
        }
        if (activity != null) {
            activity.fragmentManager.beginTransaction().remove(this@SocialProofFragment).commit()
        }
    }

    companion object {
        private const val LOG_TAG = "SocialProofFragment"

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
        // TODO: Rename and change types and number of parameters
        fun newInstance(stateId: Int, inAppState: InAppNotificationState?): SocialProofFragment {
            val fragment = SocialProofFragment()
            val args = Bundle()
            args.putInt(ARG_PARAM1, stateId)
            args.putParcelable(ARG_PARAM2, inAppState)
            fragment.arguments = args
            return fragment
        }
    }
}