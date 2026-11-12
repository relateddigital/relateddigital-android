package com.relateddigital.relateddigital_android.inapp.socialproof

import androidx.fragment.app.Fragment
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.FragmentSocialProofBinding
import com.relateddigital.relateddigital_android.inapp.FontFamily
import com.relateddigital.relateddigital_android.inapp.SafeColor
import com.relateddigital.relateddigital_android.inapp.toFloatOr
import com.relateddigital.relateddigital_android.inapp.toIntOr
import com.relateddigital.relateddigital_android.inapp.toLongOr
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
    private val mHandler = Handler(Looper.getMainLooper())
    private val mCloseRunnable = Runnable { endFragment() }
    private lateinit var binding: FragmentSocialProofBinding
    private var mExtendedProps: ProductStatNotifierExtendedProps? = null
    private var utilResultModel: UtilResultModel? = null
    private var mFontFamily = Typeface.DEFAULT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = requireArguments().getSerializable(ARG_PARAM1) as? ProductStatNotifier
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentSocialProofBinding.inflate(inflater, container, false)
        val view: View = binding.root

        hideStatusBar()

        if (checkNumber()) {
            setupInitialView()
        }
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mHandler.removeCallbacks(mCloseRunnable)
    }

    private fun setupInitialView() {
        try {
            mExtendedProps = Gson().fromJson(URI(mModel!!.actiondata!!.extendedProps).path, ProductStatNotifierExtendedProps::class.java)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            endFragment()
            return
        } catch (e: Exception) {
            e.printStackTrace()
            endFragment()
            return
        }
        if (mExtendedProps == null) {
            endFragment()
            return
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
        SafeColor.parseOrNull(mModel!!.actiondata!!.bgcolor)?.let {
            binding.socialProofContainerTop.setBackgroundColor(it)
        }
        val text: String? = utilResultModel!!.message
        binding.textViewTop.typeface = mFontFamily
        binding.textViewTop.textSize = mExtendedProps!!.content_text_size.toFloatOr(2f) * 2 + 14
        SafeColor.parseOrNull(mExtendedProps!!.content_text_color)?.let { binding.textViewTop.setTextColor(it) }
        binding.socialProofContainerBot.visibility = View.GONE
        if (utilResultModel!!.isTag) {
            val spannableString = SpannableString(text)
            for (i in 0 until utilResultModel!!.numbers.size) {
                spannableString.setSpan(
                    AbsoluteSizeSpan(
                        mExtendedProps!!.contentcount_text_size.toIntOr(2) * 2 + 14,
                        true
                    ),
                    utilResultModel!!.startIdxs[i], utilResultModel!!.endIdxs[i], 0
                )
                spannableString.setSpan(
                    ForegroundColorSpan(SafeColor.parse(mExtendedProps!!.contentcount_text_color, Color.BLACK)),
                    utilResultModel!!.startIdxs[i], utilResultModel!!.endIdxs[i], 0
                )
            }
            binding.textViewTop.text = spannableString
        } else {
            binding.textViewTop.text = text.orEmpty().replace("\\n", "\n")
        }
    }

    private fun adjustBottom() {
        SafeColor.parseOrNull(mModel!!.actiondata!!.bgcolor)?.let {
            binding.socialProofContainerBot.setBackgroundColor(it)
        }
        val text: String? = utilResultModel!!.message
        binding.textViewBot.typeface = mFontFamily
        binding.textViewBot.textSize = mExtendedProps!!.content_text_size.toFloatOr(2f) * 2 + 14
        SafeColor.parseOrNull(mExtendedProps!!.content_text_color)?.let { binding.textViewBot.setTextColor(it) }
        binding.socialProofContainerTop.visibility = View.GONE
        if (utilResultModel!!.isTag) {
            val spannableString = SpannableString(text)
            for (i in 0 until utilResultModel!!.numbers.size) {
                spannableString.setSpan(
                    AbsoluteSizeSpan(
                        mExtendedProps!!.contentcount_text_size.toIntOr(2) * 2 + 14,
                        true
                    ),
                    utilResultModel!!.startIdxs[i], utilResultModel!!.endIdxs[i], 0
                )
                spannableString.setSpan(
                    ForegroundColorSpan(SafeColor.parse(mExtendedProps!!.contentcount_text_color, Color.BLACK)),
                    utilResultModel!!.startIdxs[i], utilResultModel!!.endIdxs[i], 0
                )
            }
            binding.textViewBot.text = spannableString
        } else {
            binding.textViewBot.text = text.orEmpty().replace("\\n", "\n")
        }
    }

    private fun setTimer() {
        val timeout = mModel!!.actiondata!!.timeout.toLongOr(0L)
        if (timeout > 0) {
            mHandler.postDelayed(mCloseRunnable, timeout)
        } else {
            if (mIsTop) {
                binding.socialProofContainerTop.setOnClickListener { endFragment() }
            } else {
                binding.socialProofContainerBot.setOnClickListener { endFragment() }
            }
        }
    }

    private fun setupCloseButton() {
        if (mModel!!.actiondata!!.showclosebtn == true) {
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
            when (mExtendedProps?.close_button_color) {
                "white" -> return R.drawable.ic_close_white_24dp
                "black" -> return R.drawable.ic_close_black_24dp
            }
            return R.drawable.ic_close_black_24dp
        }

    private fun checkNumber(): Boolean {
        utilResultModel = AppUtils.getNumberFromText(mModel?.actiondata?.content)
        return if (utilResultModel == null || mModel?.actiondata == null) {
            Log.e(LOG_TAG, "Invalid Inputs!")
            endFragment()
            false
        } else {
            true
        }
    }

    private val fontFamily: Unit
        get() {
            if (FontFamily.Monospace.toString() == mExtendedProps?.content_font_family?.lowercase(Locale.ROOT)) {
                mFontFamily = Typeface.MONOSPACE
            }
            if (FontFamily.SansSerif.toString() == mExtendedProps?.content_font_family?.lowercase(Locale.ROOT)) {
                mFontFamily = Typeface.SANS_SERIF
            }
            if (FontFamily.Serif.toString() == mExtendedProps?.content_font_family?.lowercase(Locale.ROOT)) {
                mFontFamily = Typeface.SERIF
            }
            if (FontFamily.Default.toString() == mExtendedProps?.content_font_family?.lowercase(Locale.ROOT)) {
                mFontFamily = Typeface.DEFAULT
            }
        }

    private fun endFragment() {
        mHandler.removeCallbacks(mCloseRunnable)
        if (isAdded) {
            parentFragmentManager.beginTransaction().remove(this@SocialProofFragment).commitAllowingStateLoss()
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
        mHandler.removeCallbacks(mCloseRunnable)
        super.onDestroyView()
        showStatusBar()
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