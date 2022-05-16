package com.relateddigital.relateddigital_android.inapp.spintowin

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.FragmentSpintowinCodeBannerBinding
import com.relateddigital.relateddigital_android.inapp.FontFamily
import com.relateddigital.relateddigital_android.model.SpinToWinExtendedProps
import com.relateddigital.relateddigital_android.util.AppUtils
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [SpinToWinCodeBannerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SpinToWinCodeBannerFragment : Fragment() {
    companion object{
        private const val LOG_TAG = "SpinToWinBanner"
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "banner_data"
        private const val ARG_PARAM2 = "banner_code"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param extendedProps Parameter 1.
         * @param code Parameter 2.
         * @return A new instance of fragment SpinToWinCodeBannerFragment.
         */
        fun newInstance(extendedProps: SpinToWinExtendedProps, code: String): SpinToWinCodeBannerFragment {
            val fragment = SpinToWinCodeBannerFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, extendedProps)
            args.putString(ARG_PARAM2, code)
            fragment.arguments = args
            return fragment
        }
    }

    private var mExtendedProps: SpinToWinExtendedProps? = null
    private var bannerCode: String? = null
    private lateinit var binding: FragmentSpintowinCodeBannerBinding

    fun SpinToWinCodeBannerFragment() {
        // Required empty public constructor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mExtendedProps = requireArguments().getSerializable(ARG_PARAM1) as SpinToWinExtendedProps
        bannerCode = requireArguments().getString(ARG_PARAM2)
        if(bannerCode.isNullOrEmpty()) {
            endFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentSpintowinCodeBannerBinding.inflate(inflater, container, false)
        val view: View = binding.root

        hideStatusBar()

        if (mExtendedProps != null) {
            setupUi()
        } else {
            endFragment()
            Log.e(LOG_TAG, "Could not get the data, closing in app")
        }
        return view
    }

    private fun setupUi() {
        if (!mExtendedProps!!.promocode_banner_background_color.isNullOrEmpty()) {
            binding.container.setBackgroundColor(Color.parseColor(mExtendedProps!!.promocode_banner_background_color))
        } else {
            binding.container.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        binding.bannerText.text = mExtendedProps!!.promocode_banner_text!!.replace("\\n", "\n")
        binding.bannerLabel.text = mExtendedProps!!.promocode_banner_button_label
        binding.bannerCode.text = bannerCode

        if (!mExtendedProps!!.promocode_banner_text_color.isNullOrEmpty()) {
            binding.bannerText.setTextColor(Color.parseColor(mExtendedProps!!.promocode_banner_text_color))
            binding.bannerLabel.setTextColor(Color.parseColor(mExtendedProps!!.promocode_banner_text_color))
            binding.bannerCode.setTextColor(Color.parseColor(mExtendedProps!!.promocode_banner_text_color))
        } else {
            binding.bannerText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.bannerLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.bannerCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        if (!mExtendedProps!!.textSize.isNullOrEmpty()) {
            binding.bannerText.textSize = mExtendedProps!!.textSize!!.toFloat() + 10
            binding.bannerLabel.textSize = mExtendedProps!!.textSize!!.toFloat() + 12
            binding.bannerCode.textSize = mExtendedProps!!.textSize!!.toFloat() + 10
        } else {
            binding.bannerText.textSize = 14f
            binding.bannerLabel.textSize = 16f
            binding.bannerCode.textSize = 14f
        }

        if (mExtendedProps!!.textFontFamily.isNullOrEmpty()) {
            binding.bannerText.typeface = Typeface.DEFAULT
            binding.bannerLabel.typeface = Typeface.DEFAULT
        } else if (FontFamily.Monospace.toString() == mExtendedProps!!.textFontFamily!!.lowercase(Locale.getDefault())) {
            binding.bannerText.typeface = Typeface.MONOSPACE
            binding.bannerLabel.typeface = Typeface.MONOSPACE
        } else if (FontFamily.SansSerif.toString() == mExtendedProps!!.textFontFamily!!.lowercase(Locale.getDefault())) {
            binding.bannerText.typeface = Typeface.SANS_SERIF
            binding.bannerLabel.typeface = Typeface.SANS_SERIF
        } else if (FontFamily.Serif.toString() == mExtendedProps!!.textFontFamily!!.lowercase(Locale.getDefault())) {
            binding.bannerText.typeface = Typeface.SERIF
            binding.bannerLabel.typeface = Typeface.SERIF
        } else if (!mExtendedProps!!.textCustomFontFamilyAndroid.isNullOrEmpty()) {
            if (AppUtils.isFontResourceAvailable(requireContext(), mExtendedProps!!.textCustomFontFamilyAndroid)) {
                val id = requireActivity().resources.getIdentifier(
                    mExtendedProps!!.textCustomFontFamilyAndroid,
                    "font",
                    requireActivity().packageName
                )
                binding.bannerText.typeface = ResourcesCompat.getFont(requireActivity(), id)
                binding.bannerLabel.typeface = ResourcesCompat.getFont(requireActivity(), id)
            }
        } else {
            binding.bannerText.typeface = Typeface.DEFAULT
            binding.bannerLabel.typeface = Typeface.DEFAULT
        }

        binding.closeButton.setBackgroundResource(getCloseIcon())
        binding.closeButton.setOnClickListener { endFragment() }

        binding.container.setOnClickListener{
            val clipboard = requireActivity().getSystemService(FragmentActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Coupon Code", bannerCode)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show()
        }
    }

    private fun getCloseIcon(): Int {
        when (mExtendedProps!!.closeButtonColor) {
            "white" -> return R.drawable.ic_close_white_24dp
            "black" -> return R.drawable.ic_close_black_24dp
        }
        return R.drawable.ic_close_black_24dp
    }

    private fun endFragment() {
        if (activity != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(this@SpinToWinCodeBannerFragment).commit()
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
}