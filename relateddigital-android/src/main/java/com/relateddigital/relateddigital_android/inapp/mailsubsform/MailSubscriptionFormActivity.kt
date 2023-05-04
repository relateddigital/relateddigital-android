package com.relateddigital.relateddigital_android.inapp.mailsubsform

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.databinding.ActivityMailSubscriptionFormBinding
import com.relateddigital.relateddigital_android.inapp.InAppNotificationState
import com.relateddigital.relateddigital_android.inapp.InAppUpdateDisplayState
import com.relateddigital.relateddigital_android.model.ExtendedProps
import com.relateddigital.relateddigital_android.model.MailSubscriptionForm
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.InAppActionClickRequest
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import java.util.regex.Pattern

class MailSubscriptionFormActivity : Activity() {
    private var mMailSubscriptionForm: MailSubscriptionForm? = null
    private var mExtendedProps: ExtendedProps? = null
    private var mUpdateDisplayState: InAppUpdateDisplayState? = null
    private var mIntentId = -1
    private lateinit var binding: ActivityMailSubscriptionFormBinding
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMailSubscriptionFormBinding.inflate(layoutInflater)
        val view: View = binding.root
        mIntentId = intent.getIntExtra(Constants.INTENT_ID_KEY, Int.MAX_VALUE)
        mMailSubscriptionForm = mailSubscriptionForm
        try {
            mExtendedProps = Gson().fromJson(
                URI(mMailSubscriptionForm!!.actiondata!!.ExtendedProps).path,
                ExtendedProps::class.java
            )
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        setContentView(view)
        setFinishOnTouchOutside(false)
        binding.etEmail.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
                if (checkEmail(binding.etEmail.text.toString())) {
                    binding.tvInvalidEmailMessage.visibility = View.GONE
                } else {
                    binding.tvInvalidEmailMessage.visibility = View.VISIBLE
                }
            }
        }
        binding.tvEmailPermit.movementMethod = LinkMovementMethod.getInstance()
        binding.tvConsent.movementMethod = LinkMovementMethod.getInstance()
        if (isShowingInApp && mMailSubscriptionForm != null) {
            setUpView()
        } else {
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            finish()
        }
    }

    private fun setUpView() {
        binding.llTextContainer.setBackgroundColor(Color.parseColor(mExtendedProps!!.background_color))
        setCloseButton()
        setTitle()
        setBody()
        setEmail()
        setInvalidEmailMessage()
        setCheckBoxes()
        setCheckConsentMessage()
        setButton()
    }

    private fun setCloseButton() {
        binding.ibClose.setBackgroundResource(closeIcon)
        binding.ibClose.setOnClickListener {
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            finish()
        }
    }

    private fun setTitle() {
        binding.tvTitle.setTypeface(
            mExtendedProps!!.getTitleFontFamily(this),
            Typeface.BOLD
        )
        binding.tvTitle.text = mMailSubscriptionForm!!.actiondata!!.title!!.replace("\\n", "\n")
        binding.tvTitle.setTextColor(Color.parseColor(mExtendedProps!!.title_text_color))
        binding.tvTitle.textSize = mExtendedProps!!.title_text_size!!.toFloat() + 12
    }

    private fun setBody() {
        binding.tvBody.text = mMailSubscriptionForm!!.actiondata!!.message!!.replace("\\n", "\n")
        binding.tvBody.typeface = mExtendedProps!!.getTextFontFamily(this)
        binding.tvBody.setTextColor(Color.parseColor(mExtendedProps!!.text_color))
        binding.tvBody.textSize = mExtendedProps!!.text_size!!.toFloat() + 8
    }

    private fun setEmail() {
        binding.etEmail.hint = mMailSubscriptionForm!!.actiondata!!.placeholder
    }

    private fun setInvalidEmailMessage() {
        binding.tvInvalidEmailMessage.text = mMailSubscriptionForm!!.actiondata!!.invalid_email_message
        binding.tvInvalidEmailMessage.textSize = mExtendedProps!!.text_size!!.toFloat() + 8
        binding.tvInvalidEmailMessage.setTextColor(Color.RED)
    }

    private fun setCheckBoxes() {
        if (mMailSubscriptionForm!!.actiondata!!
                .emailpermit_text.isNullOrEmpty()
        ) {
            binding.llEmailPermit.visibility = View.GONE
        } else {
            binding.tvEmailPermit.text = createHtml(
                mMailSubscriptionForm!!.actiondata!!
                    .emailpermit_text!!, mExtendedProps!!.emailpermit_text_url
            )
            binding.tvEmailPermit.textSize = mExtendedProps!!.emailpermit_text_size!!.toFloat() + 8
        }
        if (mMailSubscriptionForm!!.actiondata!!.consent_text.isNullOrEmpty()
        ) {
            binding.llConsent.visibility = View.GONE
        } else {
            binding.tvConsent.text = createHtml(
                mMailSubscriptionForm!!.actiondata!!.consent_text!!,
                mExtendedProps!!.consent_text_url
            )
            binding.tvConsent.textSize = mExtendedProps!!.consent_text_size!!.toFloat() + 8
        }
    }

    private fun setCheckConsentMessage() {
        binding.tvCheckConsentMessage.text = mMailSubscriptionForm!!.actiondata!!.check_consent_message
        binding.tvCheckConsentMessage.textSize = mExtendedProps!!.text_size!!.toFloat() + 8
        binding.tvCheckConsentMessage.setTextColor(Color.RED)
    }

    private fun setButton() {
        binding.btn.text = mMailSubscriptionForm!!.actiondata!!.button_label
        binding.btn.typeface = mExtendedProps!!.getButtonFontFamily(this)
        binding.btn.setTextColor(Color.parseColor(mExtendedProps!!.button_text_color))
        binding.btn.setBackgroundColor(Color.parseColor(mExtendedProps!!.button_color))
        binding.btn.textSize = mExtendedProps!!.button_text_size!!.toFloat() + 8
        binding.btn.setOnClickListener(View.OnClickListener {
            val email: String = binding.etEmail.text.toString()
            if (checkEmail(email)) {
                binding.tvInvalidEmailMessage.visibility = View.GONE
            } else {
                binding.tvInvalidEmailMessage.visibility = View.VISIBLE
                return@OnClickListener
            }
            if (!checkCheckBoxes()) {
                return@OnClickListener
            }
            InAppActionClickRequest.createInAppActionClickRequest(applicationContext, mMailSubscriptionForm!!.actiondata!!.report)
            RequestHandler.createSubsJsonRequest(applicationContext, "subscription_email", mMailSubscriptionForm!!.actid!!,
                mMailSubscriptionForm!!.actiondata!!.auth!!, email)

            binding.tvCheckConsentMessage.visibility = View.VISIBLE
            binding.tvCheckConsentMessage.setTextColor(Color.GREEN)
            binding.tvCheckConsentMessage.text = mMailSubscriptionForm!!.actiondata!!.success_message
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                finish()
            }, 1000)
        })
    }

    private fun checkCheckBoxes(): Boolean {
        var isCheckboxesOk = true
        if (binding.llEmailPermit.visibility != View.GONE) {
            if (!binding.cbEmailPermit.isChecked) {
                isCheckboxesOk = false
                binding.tvCheckConsentMessage.visibility = View.VISIBLE
                return isCheckboxesOk
            } else {
                isCheckboxesOk = true
                binding.tvCheckConsentMessage.visibility = View.GONE
            }
        }
        if (binding.llConsent.visibility != View.GONE) {
            if (!binding.cbConsent.isChecked) {
                isCheckboxesOk = false
                binding.tvCheckConsentMessage.visibility = View.VISIBLE
                return isCheckboxesOk
            } else {
                isCheckboxesOk = true
                binding.tvCheckConsentMessage.visibility = View.GONE
            }
        }
        return isCheckboxesOk
    }

    private fun checkEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
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

    private val closeIcon: Int
        get() {
            when (mExtendedProps!!.close_button_color) {
                "white" -> return R.drawable.ic_close_white_24dp
                "black" -> return R.drawable.ic_close_black_24dp
            }
            return R.drawable.ic_close_black_24dp
        }
    private val isShowingInApp: Boolean
        get() = if (mUpdateDisplayState == null) {
            false
        } else InAppNotificationState.TYPE == mUpdateDisplayState!!.getDisplayState()!!.type

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        InAppUpdateDisplayState.releaseDisplayState(mIntentId)
        finish()
    }

    private val mailSubscriptionForm: MailSubscriptionForm?
        get() {
            val inAppNotificationState: InAppNotificationState
            mUpdateDisplayState = InAppUpdateDisplayState.claimDisplayState(mIntentId)
            return if (mUpdateDisplayState == null || mUpdateDisplayState!!.getDisplayState() == null) {
                Log.e(
                    "Visilabs",
                    "VisilabsNotificationActivity intent received, but nothing was found to show."
                )
                null
            } else {
                inAppNotificationState = mUpdateDisplayState!!.getDisplayState() as InAppNotificationState
                inAppNotificationState.getMailSubscriptionForm()
            }
        }
}