package com.relateddigital.relateddigital_android.inapp.gamification

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
import java.util.*

class GamificationCodeBannerFragment : Fragment() {
    /* companion object {
        private const val LOG_TAG = "JackpotBanner"

        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "banner_data"
        private const val ARG_PARAM2 = "banner_code"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param extendedProps Parameter 1.
         * @param code Parameter 2.
         * @return A new instance of fragment JackpotCodeBannerFragment.
         */
        fun newInstance(
            extendedProps: JackpotExtendedProps,
            code: String
        ): JackpotCodeBannerFragment {
            val fragment = JackpotCodeBannerFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, extendedProps)
            args.putString(ARG_PARAM2, code)
            fragment.arguments = args
            return fragment
        }
    }

    private var mExtendedProps: JackpotExtendedProps? = null
    private var bannerCode: String? = null
    private lateinit var binding: FragmentJackpotCodeBannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mExtendedProps = requireArguments().getSerializable(ARG_PARAM1) as JackpotExtendedProps
        bannerCode = requireArguments().getString(ARG_PARAM2)
        if (bannerCode.isNullOrEmpty()) {
            endFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJackpotCodeBannerBinding.inflate(inflater, container, false)
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
        if (!mExtendedProps!!.promocodeBannerBackgroundColor.isNullOrEmpty()) {
            binding.container.setBackgroundColor(Color.parseColor(mExtendedProps!!.promocodeBannerBackgroundColor))
        } else {
            binding.container.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
        }

        binding.bannerText.text = mExtendedProps!!.promocodeBannerText!!.replace("\\n", "\n")
        binding.bannerLabel.text = mExtendedProps!!.promocodeBannerButtonLabel
        binding.bannerCode.text = bannerCode

        if (!mExtendedProps!!.promocodeBannerTextColor.isNullOrEmpty()) {
            binding.bannerText.setTextColor(Color.parseColor(mExtendedProps!!.promocodeBannerTextColor))
            binding.bannerLabel.setTextColor(Color.parseColor(mExtendedProps!!.promocodeBannerTextColor))
            binding.bannerCode.setTextColor(Color.parseColor(mExtendedProps!!.promocodeBannerTextColor))
        } else {
            binding.bannerText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.bannerLabel.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.bannerCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        /*
        if (!mExtendedProps!!.gameResultElements!!.textSize.isNullOrEmpty()) {
            binding.bannerText.textSize = mExtendedProps!!.gameResultElements!!.textSize!!.toFloat() + 10
            binding.bannerLabel.textSize = mExtendedProps!!.gameResultElements!!.textSize!!.toFloat() + 12
            binding.bannerCode.textSize = mExtendedProps!!.gameResultElements!!.textSize!!.toFloat() + 10
        } else {
            binding.bannerText.textSize = 14f
            binding.bannerLabel.textSize = 16f
            binding.bannerCode.textSize = 14f
        }
         */

        if (mExtendedProps!!.fontFamily.isNullOrEmpty()) {
            binding.bannerText.typeface = Typeface.DEFAULT
            binding.bannerLabel.typeface = Typeface.DEFAULT
        } else if (FontFamily.Monospace.toString() == mExtendedProps!!.fontFamily!!.lowercase(
                Locale.getDefault()
            )
        ) {
            binding.bannerText.typeface = Typeface.MONOSPACE
            binding.bannerLabel.typeface = Typeface.MONOSPACE
        } else if (FontFamily.SansSerif.toString() == mExtendedProps!!.fontFamily!!.lowercase(
                Locale.getDefault()
            )
        ) {
            binding.bannerText.typeface = Typeface.SANS_SERIF
            binding.bannerLabel.typeface = Typeface.SANS_SERIF
        } else if (FontFamily.Serif.toString() == mExtendedProps!!.fontFamily!!.lowercase(Locale.getDefault())) {
            binding.bannerText.typeface = Typeface.SERIF
            binding.bannerLabel.typeface = Typeface.SERIF
        } else if (!mExtendedProps!!.customFontFamilyAndroid.isNullOrEmpty()) {
            if (AppUtils.isFontResourceAvailable(
                    requireContext(),
                    mExtendedProps!!.customFontFamilyAndroid
                )
            ) {
                val id = requireActivity().resources.getIdentifier(
                    mExtendedProps!!.customFontFamilyAndroid,
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

        binding.container.setOnClickListener {
            val clipboard =
                requireActivity().getSystemService(FragmentActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Coupon Code", bannerCode)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
                requireContext(),
                getString(R.string.copied_to_clipboard),
                Toast.LENGTH_LONG
            ).show()
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
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this@JackpotCodeBannerFragment).commit()
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
    } */
}