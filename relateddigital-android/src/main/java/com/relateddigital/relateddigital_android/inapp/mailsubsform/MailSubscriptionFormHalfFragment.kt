package com.relateddigital.relateddigital_android.inapp.mailsubsform

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.FragmentMailSubscriptionFormHalfBinding
import com.relateddigital.relateddigital_android.model.MailSubscriptionFormHalf
import com.squareup.picasso.Picasso
import java.util.regex.Pattern

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
        isImageRight = true
        isTextTop = true

        binding.imageViewLeft.visibility = View.VISIBLE
        binding.container.visibility = View.VISIBLE
        binding.imageViewRight.visibility = View.VISIBLE
        binding.titleView.visibility = View.VISIBLE
        binding.bodyTextViewTop.visibility = View.VISIBLE
        binding.bodyTextViewBot.visibility = View.VISIBLE
        binding.emailConsent1Container.visibility = View.VISIBLE
        binding.emailConsent2Container.visibility = View.VISIBLE

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
        binding.titleView.textSize = 20f
        binding.btn.text = "Kaydol"
        binding.btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_200))
        binding.btn.setOnClickListener {
            val email: String = binding.emailView.text.toString()
            if (checkEmail(email)) {
                binding.invalidMessage.visibility = View.GONE
            } else {
                binding.invalidMessage.visibility = View.VISIBLE
                binding.invalidMessage.setTextColor(Color.RED)
                binding.invalidMessage.text = "Lütfen geçerli bir email adresi giriniz"
                return@setOnClickListener
            }
            if (!checkCheckBoxes()) {
                return@setOnClickListener
            }

            //TODO : open this
            /*RequestHandler.createInAppActionClickRequest(applicationContext, mMailSubscriptionForm!!.actiondata!!.report)
            RequestHandler.createSubsJsonRequest(applicationContext, "subscription_email", mMailSubscriptionForm!!.actid!!,
                mMailSubscriptionForm!!.actiondata!!.auth!!, email)*/

            Toast.makeText(activity, "E-posta adresiniz başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
            endFragment()
        }

        binding.parentContainer.setOnClickListener{}

        binding.emailView.hint = "email address"

        //TODO : email consent texts here.second one may not be available.
        if (/*mMailSubscriptionForm!!.actiondata!!
                .emailpermit_text.isNullOrEmpty()*/ false
        ) {
            binding.emailConsent1Container.visibility = View.GONE
        } else {
            binding.emailConsent1.movementMethod = LinkMovementMethod.getInstance()
            binding.emailConsent1.text = createHtml("email izin 1",
                "https://www.relateddigital.com/"
            )
            binding.emailConsent1.textSize = 12f
            binding.emailConsent1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        if (/*mMailSubscriptionForm!!.actiondata!!
                .emailpermit_text.isNullOrEmpty()*/ false
        ) {
            binding.emailConsent2Container.visibility = View.GONE
        } else {
            binding.emailConsent2.movementMethod = LinkMovementMethod.getInstance()
            binding.emailConsent2.text = createHtml("email izin 2",
                "https://www.relateddigital.com/"
            )
            binding.emailConsent2.textSize = 12f
            binding.emailConsent2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
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

    //Email Permit TextEmail Permit <LINK>TextEmail</LINK> Permit Text
    private fun createHtml(text: String, url: String?): Spanned {
        var textLoc = text
        if (url == null || url.isEmpty() || !Patterns.WEB_URL.matcher(url).matches()) {
            return Html.fromHtml(url!!.replace("<LINK>", "").replace("</LINK>", ""))
        }
        val pattern = Pattern.compile("<LINK>(.+?)</LINK>")
        val matcher = pattern.matcher(textLoc)
        var linkMatched = false
        while (matcher.find()) {
            linkMatched = true
            val outerHtml = matcher.group(0)
            val innerText = matcher.group(1)
            val s = "<a href=\"$url\">$innerText</a>"
            textLoc = textLoc.replace(outerHtml!!, s)
        }
        if (!linkMatched) {
            textLoc = "<a href=\"$url\">$text</a>"
        }
        return Html.fromHtml(textLoc)
    }

    private fun checkEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkCheckBoxes(): Boolean {
        var isCheckboxesOk = true
        if (binding.emailConsent1Container.visibility != View.GONE) {
            if (!binding.emailConsent1Cb.isChecked) {
                isCheckboxesOk = false
                binding.invalidMessage.visibility = View.VISIBLE
                binding.invalidMessage.setTextColor(Color.RED)
                binding.invalidMessage.text = "Lütfen kullanım koşullarını kabul ediyorumu onaylayınız."
                return isCheckboxesOk
            } else {
                isCheckboxesOk = true
                binding.invalidMessage.visibility = View.GONE
            }
        }
        if (binding.emailConsent2Container.visibility != View.GONE) {
            if (!binding.emailConsent2Cb.isChecked) {
                isCheckboxesOk = false
                binding.invalidMessage.visibility = View.VISIBLE
                binding.invalidMessage.setTextColor(Color.RED)
                binding.invalidMessage.text = "Lütfen kullanım koşullarını kabul ediyorumu onaylayınız."
                return isCheckboxesOk
            } else {
                isCheckboxesOk = true
                binding.invalidMessage.visibility = View.GONE
            }
        }
        return isCheckboxesOk
    }
}