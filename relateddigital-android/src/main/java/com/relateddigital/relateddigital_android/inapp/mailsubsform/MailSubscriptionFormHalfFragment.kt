package com.relateddigital.relateddigital_android.inapp.mailsubsform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.FragmentMailSubscriptionFormHalfBinding
import com.relateddigital.relateddigital_android.model.MailSubscriptionFormHalf
import com.relateddigital.relateddigital_android.util.AppUtils
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [MailSubscriptionFormHalfFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MailSubscriptionFormHalfFragment : Fragment() {
    companion object{
        private const val LOG_TAG = "MailSubsFragment"
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "mail_subs_half_data"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param response Parameter 1.
         * @return A new instance of fragment HalfScreenFragment.
         */
        fun newInstance(response: MailSubscriptionFormHalf?): MailSubscriptionFormHalfFragment {
            val fragment = MailSubscriptionFormHalfFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, response)
            fragment.arguments = args
            return fragment
        }
    }

    private var mResponse: MailSubscriptionFormHalf? = null
    private lateinit var binding: FragmentMailSubscriptionFormHalfBinding
    private var isImageRight: Boolean = true
    private var isTextTop: Boolean = true

    fun MailSubscriptionFormHalfFragment() {
        // Required empty public constructor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mResponse = requireArguments().getSerializable(ARG_PARAM1) as MailSubscriptionFormHalf
        if (mResponse == null) {
            endFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMailSubscriptionFormHalfBinding.inflate(inflater, container, false)
        val view: View = binding.root

        populateUI()
        return view
    }

    private fun populateUI() {
        setupCloseButton()

        //TODO : set the values from real data here
        isImageRight = false
        isTextTop = false

        binding.imageViewLeft.visibility = View.VISIBLE
        binding.container.visibility = View.VISIBLE
        binding.imageViewRight.visibility = View.VISIBLE
        binding.titleView.visibility = View.VISIBLE
        binding.bodyTextViewTop.visibility = View.VISIBLE
        binding.bodyTextViewBot.visibility = View.VISIBLE
        binding.llEmailPermit.visibility = View.VISIBLE
        binding.llConsent.visibility = View.VISIBLE

        if (/*!mInApp!!.mActionData!!.mImg.isNullOrEmpty()*/true) {
            if(isImageRight) {
                binding.imageViewLeft.visibility = View.GONE
                if(/*AppUtils.isAnImage(mInApp!!.mActionData!!.mImg)*/true) {
                    Picasso.get().load("https://media-exp1.licdn.com/dms/image/C4E0BAQGmNKZy5oLtCg/company-logo_200_200/0/1639655091397?e=2147483647&v=beta&t=Bs-WyGPQ6VLsXN9TFKQo6AuZJ8zZxaRmTG6gLuvhAPU")
                        .into(binding.imageViewRight)
                } else {
                    Glide.with(this)
                        .load("https://media-exp1.licdn.com/dms/image/C4E0BAQGmNKZy5oLtCg/company-logo_200_200/0/1639655091397?e=2147483647&v=beta&t=Bs-WyGPQ6VLsXN9TFKQo6AuZJ8zZxaRmTG6gLuvhAPU")
                        .into(binding.imageViewRight)
                }
            } else {
                binding.imageViewRight.visibility = View.GONE
                if(/*AppUtils.isAnImage(mInApp!!.mActionData!!.mImg)*/true) {
                    Picasso.get().load("https://media-exp1.licdn.com/dms/image/C4E0BAQGmNKZy5oLtCg/company-logo_200_200/0/1639655091397?e=2147483647&v=beta&t=Bs-WyGPQ6VLsXN9TFKQo6AuZJ8zZxaRmTG6gLuvhAPU")
                        .into(binding.imageViewLeft)
                } else {
                    Glide.with(this)
                        .load("https://media-exp1.licdn.com/dms/image/C4E0BAQGmNKZy5oLtCg/company-logo_200_200/0/1639655091397?e=2147483647&v=beta&t=Bs-WyGPQ6VLsXN9TFKQo6AuZJ8zZxaRmTG6gLuvhAPU")
                        .into(binding.imageViewLeft)
                }
            }
        } else {
            binding.imageViewLeft.visibility = View.GONE
            binding.imageViewRight.visibility = View.GONE
        }

        if(/*!mInApp!!.mActionData!!.mBodyText.isNullOrEmpty()*/true) {
            if(isTextTop) {
                binding.bodyTextViewBot.visibility = View.GONE
                binding.bodyTextViewTop.text = "Doğaya karşı sorumluluğumuzu yansıtan ürünlerimizi keşfet"
                binding.bodyTextViewTop.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                binding.bodyTextViewTop.visibility = View.GONE
                binding.bodyTextViewBot.text = "Doğaya karşı sorumluluğumuzu yansıtan ürünlerimizi keşfet"
                binding.bodyTextViewBot.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
        } else {
            binding.bodyTextViewBot.visibility = View.GONE
            binding.bodyTextViewTop.visibility = View.GONE
        }

        binding.container.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
        binding.titleView.text = "İlk Siparişine Özel İndirim"
        binding.titleView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.btn.text = "Kaydol"
        binding.btn.setOnClickListener {
            //TODO: send mail subscription here
            endFragment()
        }

        //TODO : email consent texts here.second one may not be available.
    }

    private fun setupCloseButton() {
        binding.closeButton.setBackgroundResource(getCloseIcon())
        binding.closeButton.setOnClickListener { endFragment() }
    }

    private fun getCloseIcon(): Int {
        // TODO : open this
        /*when (mInAppMessage!!.mActionData!!.mCloseButtonColor) {
            "white" -> return R.drawable.ic_close_white_24dp
            "black" -> return R.drawable.ic_close_black_24dp
        }*/
        return R.drawable.ic_close_black_24dp
    }

    private fun endFragment() {
        requireActivity().supportFragmentManager.beginTransaction().remove(this@MailSubscriptionFormHalfFragment).commit()
    }
}