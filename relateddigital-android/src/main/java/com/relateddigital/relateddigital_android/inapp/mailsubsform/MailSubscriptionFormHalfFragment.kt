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
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.FragmentMailSubscriptionFormHalfBinding
import com.relateddigital.relateddigital_android.inapp.SafeColor
import com.relateddigital.relateddigital_android.inapp.toFloatOr
import com.relateddigital.relateddigital_android.model.MailSubActionData
import com.relateddigital.relateddigital_android.model.MailSubscriptionFormHalfExtendedProps
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.InAppActionClickRequest
import com.relateddigital.relateddigital_android.network.requestHandler.SubsJsonRequest
import com.relateddigital.relateddigital_android.util.AppUtils
import com.squareup.picasso.Picasso
import java.net.URI
import java.net.URISyntaxException
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
        private const val ARG_PARAM2 = "mail_subs_half_actid_data"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param response Parameter 1.
         * @return A new instance of fragment HalfScreenFragment.
         */
        fun newInstance(response: MailSubActionData?, actId: String): MailSubscriptionFormHalfFragment {
            val fragment = MailSubscriptionFormHalfFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, response)
            args.putString(ARG_PARAM2, actId)
            fragment.arguments = args
            return fragment
        }
    }

    private var mResponse: MailSubActionData? = null
    private var mExtendedProps: MailSubscriptionFormHalfExtendedProps? = null
    private lateinit var binding: FragmentMailSubscriptionFormHalfBinding
    private var isImageRight: Boolean = true
    private var isTextTop: Boolean = true
    private var actId = ""
    private var isValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actId = requireArguments().getString(ARG_PARAM2).orEmpty()
        mResponse = requireArguments().getSerializable(ARG_PARAM1) as MailSubActionData?
        if (mResponse == null) {
            endFragment()
        } else {
            try {
                mExtendedProps = Gson().fromJson(
                    URI(mResponse!!.ExtendedProps).path,
                    MailSubscriptionFormHalfExtendedProps::class.java
                )
            } catch (e: URISyntaxException) {
                e.printStackTrace()
                endFragment()
            } catch (e: Exception) {
                e.printStackTrace()
                endFragment()
            }
        }

        if(mExtendedProps == null) {
            endFragment()
        } else {
            isValid = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMailSubscriptionFormHalfBinding.inflate(inflater, container, false)
        val view: View = binding.root
        if (!isValid) {
            return view
        }

        populateUI()
        return view
    }

    private fun populateUI() {
        setupCloseButton()

        isImageRight = mExtendedProps!!.getImagePosition() == "right"
        isTextTop = mExtendedProps!!.getTextPosition() == "top"

        binding.imageViewLeft.visibility = View.VISIBLE
        binding.container.visibility = View.VISIBLE
        binding.imageViewRight.visibility = View.VISIBLE
        binding.titleView.visibility = View.VISIBLE
        binding.bodyTextViewTop.visibility = View.VISIBLE
        binding.bodyTextViewBot.visibility = View.VISIBLE
        binding.emailConsent1Container.visibility = View.VISIBLE
        binding.emailConsent2Container.visibility = View.VISIBLE

        if (!mResponse!!.img.isNullOrEmpty()) {
            if(isImageRight) {
                binding.imageViewLeft.visibility = View.GONE
                if(AppUtils.isAnImage(mResponse!!.img!!)) {
                    Picasso.get().load(mResponse!!.img!!)
                        .into(binding.imageViewRight)
                } else {
                    Glide.with(this)
                        .load(mResponse!!.img!!)
                        .into(binding.imageViewRight)
                }
            } else {
                binding.imageViewRight.visibility = View.GONE
                if(AppUtils.isAnImage(mResponse!!.img!!)) {
                    Picasso.get().load(mResponse!!.img!!)
                        .into(binding.imageViewLeft)
                } else {
                    Glide.with(this)
                        .load(mResponse!!.img!!)
                        .into(binding.imageViewLeft)
                }
            }
        } else {
            binding.imageViewLeft.visibility = View.GONE
            binding.imageViewRight.visibility = View.GONE
        }

        if(!mResponse!!.message.isNullOrEmpty()) {
            if(isTextTop) {
                binding.bodyTextViewBot.visibility = View.GONE
                binding.bodyTextViewTop.text = mResponse!!.message!!.replace("\\n", "\n")
                SafeColor.parseOrNull(mExtendedProps!!.getTextColor())?.let { binding.bodyTextViewTop.setTextColor(it) }
                binding.bodyTextViewTop.textSize = mExtendedProps!!.getTextSize().toFloatOr(2f) + 6
                binding.bodyTextViewTop.typeface = mExtendedProps!!.getTextFontFamily(requireContext())
            } else {
                binding.bodyTextViewTop.visibility = View.GONE
                binding.bodyTextViewBot.text = mResponse!!.message!!.replace("\\n", "\n")
                SafeColor.parseOrNull(mExtendedProps!!.getTextColor())?.let { binding.bodyTextViewBot.setTextColor(it) }
                binding.bodyTextViewBot.textSize = mExtendedProps!!.getTextSize().toFloatOr(2f) + 6
                binding.bodyTextViewBot.typeface = mExtendedProps!!.getTextFontFamily(requireContext())
            }
        } else {
            binding.bodyTextViewBot.visibility = View.GONE
            binding.bodyTextViewTop.visibility = View.GONE
        }

        SafeColor.parseOrNull(mExtendedProps!!.getBackgroundColor())?.let { binding.container.setBackgroundColor(it) }
        binding.titleView.text = mResponse!!.title.orEmpty().replace("\\n", "\n")
        SafeColor.parseOrNull(mExtendedProps!!.getTitleTextColor())?.let { binding.titleView.setTextColor(it) }
        binding.titleView.textSize = mExtendedProps!!.getTitleTextSize().toFloatOr(2f) + 10
        binding.titleView.typeface = mExtendedProps!!.getTitleFontFamily(requireContext())
        binding.btn.text = mResponse!!.button_label
        SafeColor.parseOrNull(mExtendedProps!!.getButtonTextColor())?.let { binding.btn.setTextColor(it) }
        SafeColor.parseOrNull(mExtendedProps!!.getButtonColor())?.let { binding.btn.setBackgroundColor(it) }
        binding.btn.textSize = mExtendedProps!!.getButtonTextSize().toFloatOr(2f) + 10
        binding.btn.typeface = mExtendedProps!!.getButtonFontFamily(requireContext())
        binding.btn.setOnClickListener {
            val email: String = binding.emailView.text.toString()
            if (checkEmail(email)) {
                binding.invalidMessage.visibility = View.GONE
            } else {
                binding.invalidMessage.visibility = View.VISIBLE
                binding.invalidMessage.setTextColor(Color.RED)
                binding.invalidMessage.text = mResponse!!.invalid_email_message
                return@setOnClickListener
            }
            if (!checkCheckBoxes()) {
                return@setOnClickListener
            }

            InAppActionClickRequest.createInAppActionClickRequest(requireContext(), mResponse!!.report)
            SubsJsonRequest.createSubsJsonRequest(requireContext(), "subscription_email", actId,
                mResponse!!.auth.orEmpty(), email)

            Toast.makeText(activity, mResponse!!.success_message, Toast.LENGTH_SHORT).show()
            endFragment()
        }

        binding.parentContainer.setOnClickListener{}

        binding.emailView.hint = mResponse!!.placeholder

        if (mResponse!!.emailpermit_text.isNullOrEmpty()) {
            binding.emailConsent1Container.visibility = View.GONE
        } else {
            binding.emailConsent1.movementMethod = LinkMovementMethod.getInstance()
            binding.emailConsent1.text = createHtml(mResponse!!.emailpermit_text!!,
                mExtendedProps!!.getEmailPermitTextUrl()
            )
            binding.emailConsent1.textSize = mExtendedProps!!.getEmailPermitTextSize().toFloatOr(2f) + 6
        }
        if (mResponse!!.consent_text.isNullOrEmpty()) {
            binding.emailConsent2Container.visibility = View.GONE
        } else {
            binding.emailConsent2.movementMethod = LinkMovementMethod.getInstance()
            binding.emailConsent2.text = createHtml(mResponse!!.consent_text!!,
                mExtendedProps!!.getConsentTextUrl()
            )
            binding.emailConsent2.textSize = mExtendedProps!!.getConsentTextSize().toFloatOr(2f) + 6
        }
    }

    private fun setupCloseButton() {
        binding.closeButton.setBackgroundResource(getCloseIcon())
        binding.closeButton.setOnClickListener { endFragment() }
    }

    private fun getCloseIcon(): Int {
        when (mExtendedProps!!.getCloseButtonColor()) {
            "white" -> return R.drawable.ic_close_white_24dp
            "black" -> return R.drawable.ic_close_black_24dp
        }
        return R.drawable.ic_close_black_24dp
    }

    private fun endFragment() {
        activity?.supportFragmentManager?.beginTransaction()?.remove(this@MailSubscriptionFormHalfFragment)?.commitAllowingStateLoss()
    }

    //Email Permit TextEmail Permit <LINK>TextEmail</LINK> Permit Text
    private fun createHtml(text: String, url: String?): Spanned {
        var textLoc = text
        if (url.isNullOrEmpty() || !Patterns.WEB_URL.matcher(url).matches()) {
            return Html.fromHtml(text.replace("<LINK>", "").replace("</LINK>", ""))
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
                binding.invalidMessage.text = mResponse!!.check_consent_message
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
                binding.invalidMessage.text = mResponse!!.check_consent_message
                return isCheckboxesOk
            } else {
                isCheckboxesOk = true
                binding.invalidMessage.visibility = View.GONE
            }
        }
        return isCheckboxesOk
    }
}