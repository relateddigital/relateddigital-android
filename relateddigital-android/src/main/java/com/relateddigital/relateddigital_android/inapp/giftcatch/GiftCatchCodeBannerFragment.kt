package com.relateddigital.relateddigital_android.inapp.giftcatch

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.FragmentGiftcatchCodeBannerBinding
import com.relateddigital.relateddigital_android.model.GiftCatchExtendedProps

/**
 * A simple [Fragment] subclass.
 * Use the [GiftCatchCodeBannerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GiftCatchCodeBannerFragment : Fragment() {
    companion object{
        private const val LOG_TAG = "GiftCatchBanner"
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
        fun newInstance(extendedProps: GiftCatchExtendedProps, code: String): GiftCatchCodeBannerFragment {
            val fragment = GiftCatchCodeBannerFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, extendedProps)
            args.putString(ARG_PARAM2, code)
            fragment.arguments = args
            return fragment
        }
    }

    private var mExtendedProps: GiftCatchExtendedProps? = null
    private var bannerCode: String? = null
    private lateinit var binding: FragmentGiftcatchCodeBannerBinding

    fun GiftCatchCodeBannerFragment() {
        // Required empty public constructor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mExtendedProps = requireArguments().getSerializable(ARG_PARAM1) as GiftCatchExtendedProps
        bannerCode = requireArguments().getString(ARG_PARAM2)
        if(bannerCode.isNullOrEmpty()) {
            endFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentGiftcatchCodeBannerBinding.inflate(inflater, container, false)
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
        // TODO : real data here. Get controls from SpinToWinCodeBannerFragment
        binding.container.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))

        binding.bannerText.text = "Kodunuzu unutmayın.".replace("\\n", "\n")
        binding.bannerLabel.text = "Kopyala"
        binding.bannerCode.text = "DGFGNK2332MKL"

        binding.bannerText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.bannerLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.bannerCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        binding.bannerText.textSize = 14f
        binding.bannerLabel.textSize = 16f
        binding.bannerCode.textSize = 14f

        binding.bannerText.typeface = Typeface.DEFAULT
        binding.bannerLabel.typeface = Typeface.DEFAULT

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
        //TODO real data here
        return R.drawable.ic_close_white_24dp
    }

    private fun endFragment() {
        if (activity != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(this@GiftCatchCodeBannerFragment).commit()
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