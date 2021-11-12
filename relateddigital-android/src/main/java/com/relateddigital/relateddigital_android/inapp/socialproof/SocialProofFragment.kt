package com.relateddigital.relateddigital_android.inapp.socialproof

import android.app.Fragment
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.FragmentSocialProofBinding
import com.relateddigital.relateddigital_android.inapp.FontFamily
import com.relateddigital.relateddigital_android.model.ProductStatNotifier
import com.relateddigital.relateddigital_android.model.ProductStatNotifierExtendedProps
import com.relateddigital.relateddigital_android.model.UtilResultModel
import com.relateddigital.relateddigital_android.util.AppUtils
import java.net.URI
import java.net.URISyntaxException
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [SocialProofFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SocialProofFragment : Fragment() {
    private var mModel: ProductStatNotifier? = null
    private var mIsTop = false
    private var mTimer: Timer? = null
    private lateinit var binding: FragmentSocialProofBinding
    private var mNumber = 0
    private var mExtendedProps: ProductStatNotifierExtendedProps? = null
    private var utilResultModel: UtilResultModel? = null
    private var mFontFamily = Typeface.DEFAULT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = arguments.getSerializable(ARG_PARAM1) as ProductStatNotifier
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentSocialProofBinding.inflate(inflater, container, false)
        val view: View = binding.root
        checkNumber()
        if (isUnderThreshold) {
            endFragment()
        }
        setupInitialView()
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mTimer != null) {
            mTimer!!.cancel()
        }
    }

    private fun setupInitialView() {
        try {
            mExtendedProps = Gson().fromJson(URI(mModel!!.actiondata!!.extendedProps).path, ProductStatNotifierExtendedProps::class.java)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            endFragment()
        } catch (e: Exception) {
            e.printStackTrace()
            endFragment()
        }
        fontFamily
        mIsTop = mModel!!.actiondata!!.pos.equals("top")
        if (mIsTop) {
            adjustTop()
        } else {
            adjustBottom()
        }
        setTimer()
        setupCloseButton()
    }

    private fun adjustTop() {
        if(utilResultModel!!.startIdx == -1) {
            endFragment()
        } else {
            binding.socialProofContainerTop.setBackgroundColor(Color.parseColor(mModel!!.actiondata!!.bgcolor))
            val text: String = mModel!!.actiondata!!.content!!
            val spannableString = SpannableString(text)
            spannableString.setSpan(
                AbsoluteSizeSpan(mExtendedProps!!.contentcount_text_size!!.toInt() * 2 + 14, true),
                utilResultModel!!.startIdx, utilResultModel!!.endIdx, 0
            )
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor(mExtendedProps!!.contentcount_text_color)),
                utilResultModel!!.startIdx, utilResultModel!!.endIdx, 0
            )
            binding.textViewTop.text = spannableString
            binding.textViewTop.typeface = mFontFamily
            binding.textViewTop.textSize = mExtendedProps!!.content_text_size!!.toFloat() * 2 + 14
            binding.textViewTop.setTextColor(Color.parseColor(mExtendedProps!!.content_text_color))
            binding.socialProofContainerBot.visibility = View.GONE
        }
    }

    private fun adjustBottom() {
        if(utilResultModel!!.startIdx == -1) {
            endFragment()
        } else {
            binding.socialProofContainerBot.setBackgroundColor(Color.parseColor(mModel!!.actiondata!!.bgcolor))
            val text: String = mModel!!.actiondata!!.content!!
            val spannableString = SpannableString(text)
            spannableString.setSpan(
                AbsoluteSizeSpan(mExtendedProps!!.content_text_size!!.toInt() * 2 + 14, true),
                utilResultModel!!.startIdx, utilResultModel!!.endIdx, 0
            )
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor(mExtendedProps!!.contentcount_text_color)),
                utilResultModel!!.startIdx, utilResultModel!!.endIdx, 0
            )
            binding.textViewBot.text = spannableString
            binding.textViewBot.typeface = mFontFamily
            binding.textViewBot.textSize = mExtendedProps!!.content_text_size!!.toFloat() * 2 + 14
            binding.textViewBot.setTextColor(Color.parseColor(mExtendedProps!!.content_text_color))
            binding.socialProofContainerTop.visibility = View.GONE
        }
    }

    private fun setTimer() {
        if (!mModel!!.actiondata!!.timeout.equals("0")) {
            mTimer = Timer("SocialProofTimer", false)
            val task: TimerTask = object : TimerTask() {
                override fun run() {
                    endFragment()
                }
            }
            mTimer!!.schedule(task, mModel!!.actiondata!!.timeout!!.toInt().toLong())
        } else {
            if (mIsTop) {
                binding.socialProofContainerTop.setOnClickListener { endFragment() }
            } else {
                binding.socialProofContainerBot.setOnClickListener { endFragment() }
            }
        }
    }

    private fun setupCloseButton() {
        if (mModel!!.actiondata!!.showclosebtn!!) {
            if (mIsTop) {
                binding.closeButtonTop.setBackgroundResource(closeIcon)
                binding.closeButtonTop.setOnClickListener { endFragment() }
            } else {
                binding.closeButtonBot.setBackgroundResource(closeIcon)
                binding.closeButtonBot.setOnClickListener { endFragment() }
            }
        } else {
            binding.closeButtonTop.visibility = View.GONE
            binding.closeButtonBot.visibility = View.GONE
        }
    }

    private val closeIcon: Int
        get() {
            when (mExtendedProps!!.close_button_color) {
                "white" -> return R.drawable.ic_close_white_24dp
                "black" -> return R.drawable.ic_close_black_24dp
            }
            return R.drawable.ic_close_black_24dp
        }

    private fun checkNumber() {
        utilResultModel = AppUtils.getNumberFromText(mModel!!.actiondata!!.content)
        if (utilResultModel == null) {
            Log.e(LOG_TAG, "Invalid Inputs!")
            endFragment()
        } else {
            mNumber = utilResultModel!!.number
        }
    }

    private val fontFamily: Unit
        get() {
            if (FontFamily.Monospace.toString() == mExtendedProps!!.content_font_family!!.toLowerCase(Locale.ROOT)) {
                mFontFamily = Typeface.MONOSPACE
            }
            if (FontFamily.SansSerif.toString() == mExtendedProps!!.content_font_family!!.toLowerCase(Locale.ROOT)) {
                mFontFamily = Typeface.SANS_SERIF
            }
            if (FontFamily.Serif.toString() == mExtendedProps!!.content_font_family!!.toLowerCase(Locale.ROOT)) {
                mFontFamily = Typeface.SERIF
            }
            if (FontFamily.Default.toString() == mExtendedProps!!.content_font_family!!.toLowerCase(Locale.ROOT)) {
                mFontFamily = Typeface.DEFAULT
            }
        }
    private val isUnderThreshold: Boolean
        get() = mNumber < mModel!!.actiondata!!.threshold!!

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
        private const val ARG_PARAM1 = "dataKey"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param model Parameter 1.
         * @return A new instance of fragment SocialProofFragment.
         */
        fun newInstance(model: ProductStatNotifier): SocialProofFragment {
            val fragment = SocialProofFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, model)
            fragment.arguments = args
            return fragment
        }
    }
}