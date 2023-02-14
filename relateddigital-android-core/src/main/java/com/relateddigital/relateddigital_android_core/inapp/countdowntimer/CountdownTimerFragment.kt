package com.relateddigital.relateddigital_android_core.inapp.countdowntimer

import androidx.fragment.app.Fragment
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.relateddigital.relateddigital_android_core.R
import com.relateddigital.relateddigital_android_core.databinding.FragmentCountdownTimerBinding
import com.relateddigital.relateddigital_android_core.inapp.InAppNotificationState
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [CountdownTimerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CountdownTimerFragment : Fragment() {
    private var mWeekNum: Short = 0
    private var mDayNum: Short = 0
    private var mHourNum: Short = 0
    private var mMinuteNum: Short = 0
    private var mSecondNum: Short = 0
    private var mTimer: Timer? = null
    private var mStateId = 0
    private var mInAppState: InAppNotificationState? = null
    private var mIsTop = false
    private lateinit var binding: FragmentCountdownTimerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStateId = requireArguments().getInt(ARG_PARAM1)
        mInAppState = requireArguments().getParcelable(ARG_PARAM2)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mTimer != null) {
            mTimer!!.cancel()
        }
        //TODO: save the remaining time according to the format here
        //TODO: save the json string here
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentCountdownTimerBinding.inflate(inflater, container, false)
        val view: View = binding.root

        hideStatusBar()

        if (savedInstanceState != null) {
            //TODO: get the remaining time here
            //TODO: get the json string here
            //TODO: set the remaining time to json string here
        } else {
            //TODO: get the json string here
        }
        setupInitialView()
        return view
    }

    private fun setupInitialView() {
        //TODO: Check the type of campaign e.g. end date or time
        //TODO: If only end date is sent, calculate the time between the date now and end date.
        //TODO: Check the position and assign it to mIsTop

        //Calculate the time remained to the end day
        //calculateTimeToEndDate();
        mIsTop = true
        if (mIsTop) {
            adjustTop()
        } else {
            adjustBottom()
        }
        setupCloseButton()
    }

    private fun adjustTop() {
        //TODO remove the code below when the actual data gets ready
        binding.countdownTimerContainerTop.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
        binding.titleTop.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.titleTop.text = "Sana Özel Fırsatı Kaçırma!".replace("\\n", "\n")
        binding.bodyTextTop.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.bodyTextTop.text = "Bugün sana özel indirim kodu için geri sayım başladı.".replace("\\n", "\n")
        binding.buttonTop.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.buttonTop.text = "Alışverişe Başla"
        binding.buttonTop.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.countdownTimerContainerBot.visibility = View.GONE
        adjustCouponViewTop()
        adjustTimerViewTop()
        adjustButtonTop()
    }

    private fun adjustBottom() {
        //TODO remove the code below when the actual data gets ready
        binding.countdownTimerContainerBot.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
        binding.titleBot.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.titleBot.text = "Sana Özel Fırsatı Kaçırma!".replace("\\n", "\n")
        binding.bodyTextBot.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.bodyTextBot.text = "Bugün sana özel indirim kodu için geri sayım başladı.".replace("\\n", "\n")
        binding.buttonBot.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.buttonBot.text = "Alışverişe Başla"
        binding.buttonBot.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.countdownTimerContainerTop.visibility = View.GONE
        adjustCouponViewBot()
        adjustTimerViewBot()
        adjustButtonBot()
    }

    private fun adjustCouponViewTop() {
        //TODO if there is coupon code
        binding.couponTop.visibility = View.VISIBLE
        binding.couponTextTop.text = "1D48KNSDF92A"
        binding.couponTextTop.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.couponTop.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.couponButtonTop.setOnClickListener {
            //TODO: send click report here
            val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            //TODO: get coupon text from the response instead of view here
            val clip = ClipData.newPlainText(getString(R.string.coupon_code), binding.couponTextTop.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireActivity(), getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show()
            //TODO track this click later
        }
    }

    private fun adjustCouponViewBot() {
        //TODO if there is coupon code
        binding.couponBot.visibility = View.VISIBLE
        binding.couponTextBot.text = "1D48KNSDF92A"
        binding.couponTextBot.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.couponBot.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.couponButtonBot.setOnClickListener {
            //TODO: send click report here
            val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            //TODO: get coupon text from the response instead of view here
            val clip = ClipData.newPlainText(getString(R.string.coupon_code), binding.couponTextBot.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireActivity(), getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show()
            //TODO track this click later
        }
    }

    private fun adjustButtonTop() {
        //TODO this should direct to somewhere
        binding.buttonTop.setOnClickListener { //TODO: send click report here
            //TODO: Remove Toast
            /*
            InAppButtonInterface buttonInterface = Visilabs.CallAPI().getInAppButtonInterface();
            Visilabs.CallAPI().trackInAppMessageClick(mInApp, null);
            if(buttonInterface != null) {
                Visilabs.CallAPI().setInAppButtonInterface(null);
                buttonInterface.onPress(mInApp.getActionData().getAndroidLnk());
            } else {
                if (mInApp.getActionData().getAndroidLnk() != null && mInApp.getActionData().getAndroidLnk().length() > 0) {
                    try {
                        Intent viewIntent = new Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString(mInApp.getActionData().getAndroidLnk()));
                        VisilabsInAppActivity.this.startActivity(viewIntent);

                    } catch (final ActivityNotFoundException e) {
                        Log.i("Visilabs", "User doesn't have an activity for notification URI");
                    }
                }
            }
            finish();
            VisilabsUpdateDisplayState.releaseDisplayState(mIntentId);
            */
            Toast.makeText(activity, "Go to the Link", Toast.LENGTH_LONG).show()
            endFragment()
        }
    }

    private fun adjustButtonBot() {
        //TODO this should direct to somewhere
        binding.buttonBot.setOnClickListener { //TODO: send click report here
            //TODO: Remove Toast
            /*
            InAppButtonInterface buttonInterface = Visilabs.CallAPI().getInAppButtonInterface();
            Visilabs.CallAPI().trackInAppMessageClick(mInApp, null);
            if(buttonInterface != null) {
                Visilabs.CallAPI().setInAppButtonInterface(null);
                buttonInterface.onPress(mInApp.getActionData().getAndroidLnk());
            } else {
                if (mInApp.getActionData().getAndroidLnk() != null && mInApp.getActionData().getAndroidLnk().length() > 0) {
                    try {
                        Intent viewIntent = new Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString(mInApp.getActionData().getAndroidLnk()));
                        VisilabsInAppActivity.this.startActivity(viewIntent);

                    } catch (final ActivityNotFoundException e) {
                        Log.i("Visilabs", "User doesn't have an activity for notification URI");
                    }
                }
            }
            finish();
            VisilabsUpdateDisplayState.releaseDisplayState(mIntentId);
            */
            Toast.makeText(activity, "Go to the Link", Toast.LENGTH_LONG).show()
            endFragment()
        }
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
        get() = R.drawable.ic_close_white_24dp
    //TODO when real data comes:
    /* switch (mInAppMessage.getActionData().getCloseButtonColor()) {

         case "white":
             return R.drawable.ic_close_white_24dp;

         case "black":
             return R.drawable.ic_close_black_24dp;
     }
     return R.drawable.ic_close_black_24dp;*/

    private fun adjustTimerViewTop() {
        setTimerValues()
        //TODO check the format here and set the visibilities of the views accordingly
        //TODO: convert bigger part like week to smaller parts like day if necessary according to the format
        binding.weekNumTop.text = mWeekNum.toString()
        binding.weekNumTop.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.dayNumTop.text = mDayNum.toString()
        binding.dayNumTop.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.hourNumTop.text = mHourNum.toString()
        binding.hourNumTop.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.minuteNumTop.text = mMinuteNum.toString()
        binding.minuteNumTop.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.secNumTop.text = mSecondNum.toString()
        binding.secNumTop.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        startTimer()
    }

    private fun adjustTimerViewBot() {
        setTimerValues()
        //TODO check the format here and set the visibilities of the views accordingly
        //TODO: convert bigger part like week to smaller parts like day if necessary according to the format
        binding.weekNumBot.text = mWeekNum.toString()
        binding.weekNumBot.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.dayNumBot.text = mDayNum.toString()
        binding.dayNumBot.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.hourNumBot.text = mHourNum.toString()
        binding.hourNumBot.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.minuteNumBot.text = mMinuteNum.toString()
        binding.minuteNumBot.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.secNumBot.text = mSecondNum.toString()
        binding.secNumBot.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        startTimer()
    }

    private fun setTimerValues() {
        //TODO: When real data came, adjust here accordingly
        mWeekNum = 3
        mDayNum = 5
        mHourNum = 17
        mMinuteNum = 0
        mSecondNum = 6
    }

    private fun startTimer() {
        mTimer = Timer("CountDownTimer", false)
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                reAdjustTimerViews()
            }
        }
        mTimer!!.schedule(task, TIMER_SCHEDULE.toLong(), TIMER_PERIOD.toLong())
    }

    private fun reAdjustTimerViews() {
        calculateTimeFields()
        requireActivity().runOnUiThread {
            try {
                if (mIsTop) {
                    if (binding.weekNumTop.visibility != View.GONE) {
                        binding.weekNumTop.text = mWeekNum.toString()
                    }
                    if (binding.dayNumTop.visibility != View.GONE) {
                        binding.dayNumTop.text = mDayNum.toString()
                    }
                    if (binding.hourNumTop.visibility != View.GONE) {
                        binding.hourNumTop.text = mHourNum.toString()
                    }
                    if (binding.minuteNumTop.visibility != View.GONE) {
                        binding.minuteNumTop.text = mMinuteNum.toString()
                    }
                    if (binding.secNumTop.visibility != View.GONE) {
                        binding.secNumTop.text = mSecondNum.toString()
                    }
                } else {
                    if (binding.weekNumBot.visibility != View.GONE) {
                        binding.weekNumBot.text = mWeekNum.toString()
                    }
                    if (binding.dayNumBot.visibility != View.GONE) {
                        binding.dayNumBot.text = mDayNum.toString()
                    }
                    if (binding.hourNumBot.visibility != View.GONE) {
                        binding.hourNumBot.text = mHourNum.toString()
                    }
                    if (binding.minuteNumBot.visibility != View.GONE) {
                        binding.minuteNumBot.text = mMinuteNum.toString()
                    }
                    if (binding.secNumBot.visibility != View.GONE) {
                        binding.secNumBot.text = mSecondNum.toString()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(LOG_TAG, "The fields for countdown timer could not be set!")
            }
        }
    }

    private fun calculateTimeFields() {
        //TODO: Adjust the logic here for each format. For example, if there is no week field
        //in the format do not set day to max 6 below.
        if (mSecondNum > 0) {
            mSecondNum--
        } else {
            mSecondNum = 59
            if (mMinuteNum > 0) {
                mMinuteNum--
            } else {
                mMinuteNum = 59
                if (mHourNum > 0) {
                    mHourNum--
                } else {
                    mHourNum = 23
                    if (mDayNum > 0) {
                        mDayNum--
                    } else {
                        mDayNum = 6
                        if (mWeekNum > 0) {
                            mWeekNum--
                        } else {
                            expireTime()
                        }
                    }
                }
            }
        }
    }

    private fun expireTime() {
        mSecondNum = 0
        mMinuteNum = 0
        mHourNum = 0
        mDayNum = 0
        mWeekNum = 0
        if (mTimer != null) {
            mTimer!!.cancel()
        }
        requireActivity().runOnUiThread { Toast.makeText(activity, getString(R.string.time_is_over), Toast.LENGTH_LONG).show() }
    }

    private fun endFragment() {
        if (mTimer != null) {
            mTimer!!.cancel()
        }
        if (activity != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(this@CountdownTimerFragment).commit()
        }
    }

    private fun hideStatusBar() {
        val decorView = requireActivity().window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
        requireActivity().actionBar?.hide()
    }

    private fun showStatusBar() {
        if (activity != null) {
            ViewCompat.getWindowInsetsController(
                requireActivity().window.decorView
            )?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showStatusBar()
    }


    companion object {
        private const val LOG_TAG = "CountdownTimerFragment"

        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "stateIdKey"
        private const val ARG_PARAM2 = "inAppStateKey"
        private const val TIMER_SCHEDULE: Short = 1000
        private const val TIMER_PERIOD: Short = 1000

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param stateId    Parameter 1.
         * @param inAppState Parameter 2.
         * @return A new instance of fragment SocialProofFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(stateId: Int, inAppState: InAppNotificationState?): CountdownTimerFragment {
            val fragment = CountdownTimerFragment()
            val args = Bundle()
            args.putInt(ARG_PARAM1, stateId)
            args.putParcelable(ARG_PARAM2, inAppState)
            fragment.arguments = args
            return fragment
        }
    }
}