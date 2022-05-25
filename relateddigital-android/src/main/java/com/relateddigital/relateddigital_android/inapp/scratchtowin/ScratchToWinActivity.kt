package com.relateddigital.relateddigital_android.inapp.scratchtowin

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.ActivityScratchToWinBinding
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.model.ScratchToWin
import com.relateddigital.relateddigital_android.model.ScratchToWinExtendedProps
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.util.StringUtils
import com.squareup.picasso.Picasso
import java.net.URI
import java.util.regex.Pattern

class ScratchToWinActivity : Activity(), ScratchToWinInterface {
    private lateinit var binding: ActivityScratchToWinBinding
    private var mScratchToWinMessage: ScratchToWin? = null
    private var isMailSubsForm = false
    private var mExtendedProps: ScratchToWinExtendedProps? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScratchToWinBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        setFinishOnTouchOutside(true)
        scratchToWinMessage
        parseExtendedProps()
        setupInitialView()
    }

    private val scratchToWinMessage: Unit
        get() {
            val intent = intent
            if (intent != null) {
                if (intent.hasExtra("scratch-to-win-data")) {
                    mScratchToWinMessage = intent.getSerializableExtra("scratch-to-win-data") as ScratchToWin?
                }
            }
            if (mScratchToWinMessage == null) {
                Log.e(LOG_TAG, "Could not get the content from the server!")
                finish()
            }
        }

    private fun setupInitialView() {
        setupCloseButton()
        setupScratchToWin()
        isMailSubsForm = mScratchToWinMessage!!.actiondata!!.mailSubscription!!
        if (isMailSubsForm) {
            binding.viewToBeScratched.setInvalidEmailMessage(mScratchToWinMessage!!.actiondata!!
                    .mailSubscriptionForm!!.invalidEmailMessage)
            binding.viewToBeScratched.setMissingConsentMessage(mScratchToWinMessage!!.actiondata!!
                    .mailSubscriptionForm!!.checkConsentMessage)
            setupEmail()
        } else {
            removeEmailViews()
            binding.viewToBeScratched.enableScratching()
        }
    }

    private fun setupCloseButton() {
        binding.closeButton.setBackgroundResource(closeIcon)
        binding.closeButton.setOnClickListener { finish() }
    }

    private fun setupScratchToWin() {
        if (!mExtendedProps!!.backgroundColor.isNullOrEmpty()) {
            binding.scratchToWinContainer.setBackgroundColor(Color.parseColor(mExtendedProps!!.backgroundColor))
        }
        if (mScratchToWinMessage!!.actiondata!!.img!!.isNotEmpty()) {
            Picasso.get().load(mScratchToWinMessage!!.actiondata!!.img).into(binding.mainImage)
        }
        binding.titleText.text = mScratchToWinMessage!!.actiondata!!.contentTitle!!.replace("\\n", "\n")
        binding.titleText.setTextColor(Color.parseColor(mExtendedProps!!.contentTitleTextColor))
        binding.titleText.textSize = mExtendedProps!!.contentBodyTextSize!!.toFloat() + 12
        binding.titleText.setTypeface(mExtendedProps!!.getContentTitleFontFamily(this), Typeface.BOLD)
        binding.bodyText.text = mScratchToWinMessage!!.actiondata!!.contentBody!!.replace("\\n", "\n")
        binding.bodyText.setTextColor(Color.parseColor(mExtendedProps!!.contentBodyTextColor))
        binding.bodyText.textSize = mExtendedProps!!.contentBodyTextSize!!.toFloat() + 8
        binding.bodyText.typeface = mExtendedProps!!.getContentBodyFontFamily(this)
        binding.promotionCodeText.text = mScratchToWinMessage!!.actiondata!!.promotionCode
        binding.promotionCodeText.setTextColor(Color.parseColor(mExtendedProps!!.promoCodeTextColor))
        binding.promotionCodeText.textSize = mExtendedProps!!.promoCodeTextSize!!.toFloat() + 12
        binding.promotionCodeText.typeface = mExtendedProps!!.getPromoCodeFontFamily(this)
        binding.copyToClipboard.text = mScratchToWinMessage!!.actiondata!!.copybuttonLabel
        binding.copyToClipboard.setTextColor(Color.parseColor(mExtendedProps!!.copyButtonTextColor))
        binding.copyToClipboard.textSize = mExtendedProps!!.copyButtonTextSize!!.toFloat() + 10
        binding.copyToClipboard.typeface = mExtendedProps!!.getCopyButtonFontFamily(this)
        binding.copyToClipboard.setBackgroundColor(Color.parseColor(mExtendedProps!!.copyButtonColor))
        binding.viewToBeScratched.setColor(Color.parseColor(mScratchToWinMessage!!.actiondata!!.scratchColor))
        binding.copyToClipboard.visibility = View.GONE
        binding.viewToBeScratched.setContainer(binding.scratchToWinContainer)
        binding.viewToBeScratched.setListener(this)
        binding.copyToClipboard.setOnClickListener {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("", mScratchToWinMessage!!.actiondata!!.promotionCode)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(applicationContext, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupEmail() {
        binding.invalidEmailMessage.text = mScratchToWinMessage!!.actiondata!!.mailSubscriptionForm!!.invalidEmailMessage
        binding.resultText.text = mScratchToWinMessage!!.actiondata!!.mailSubscriptionForm!!.checkConsentMessage
        binding.emailPermitText.text = createHtml(mScratchToWinMessage!!.actiondata!!.mailSubscriptionForm!!.emailpermitText!!,
                mExtendedProps!!.emailPermitTextUrl)
        binding.emailPermitText.textSize = mExtendedProps!!.emailPermitTextSize!!.toFloat() + 10
        binding.emailPermitText.setOnClickListener {
            if (!mExtendedProps!!.emailPermitTextUrl.isNullOrEmpty()) {
                try {
                    val viewIntent = Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString(mExtendedProps!!.emailPermitTextUrl))
                    startActivity(viewIntent)
                } catch (e: ActivityNotFoundException) {
                    Log.i(LOG_TAG, "Could not direct to the url entered!")
                }
            }
        }
        binding.consentText.text = createHtml(mScratchToWinMessage!!.actiondata!!.mailSubscriptionForm!!.consentText!!,
                mExtendedProps!!.consentTextUrl)
        binding.consentText.textSize = mExtendedProps!!.consentTextSize!!.toFloat() + 10
        binding.consentText.setOnClickListener {
            if (!mExtendedProps!!.consentTextUrl.isNullOrEmpty()) {
                try {
                    val viewIntent = Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString(mExtendedProps!!.consentTextUrl))
                    startActivity(viewIntent)
                } catch (e: ActivityNotFoundException) {
                    Log.i(LOG_TAG, "Could not direct to the url entered!")
                }
            }
        }
        binding.emailPermitCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.viewToBeScratched.setConsent1Status(true)
            } else {
                binding.viewToBeScratched.setConsent1Status(false)
            }
        }
        binding.consentCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.viewToBeScratched.setConsent2Status(true)
            } else {
                binding.viewToBeScratched.setConsent2Status(false)
            }
        }
        binding.emailEdit.hint = mScratchToWinMessage!!.actiondata!!.mailSubscriptionForm!!.placeholder
        binding.saveMail.text = mScratchToWinMessage!!.actiondata!!.mailSubscriptionForm!!.buttonLabel
        binding.saveMail.setTextColor(Color.parseColor(mExtendedProps!!.buttonTextColor))
        binding.saveMail.textSize = mExtendedProps!!.buttonTextSize!!.toFloat() + 10
        binding.saveMail.typeface = mExtendedProps!!.getButtonFontFamily(this)
        binding.saveMail.setBackgroundColor(Color.parseColor(mExtendedProps!!.buttonColor))
        binding.emailEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                binding.viewToBeScratched.setEmailStatus(checkEmail(binding.emailEdit.text.toString()))
            }
        })
        binding.emailEdit.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
                if (checkEmail(binding.emailEdit.text.toString())) {
                    binding.invalidEmailMessage.visibility = View.GONE
                } else {
                    binding.invalidEmailMessage.visibility = View.VISIBLE
                }
            }
        }
        binding.saveMail.setOnClickListener {
            val email: String = binding.emailEdit.text.toString()
            binding.invalidEmailMessage.visibility = View.GONE
            binding.resultText.visibility = View.GONE
            if (checkEmail(email) && checkTheBoxes()) {
                binding.mailContainer.visibility = View.GONE
                binding.emailEdit.visibility = View.GONE
                binding.saveMail.visibility = View.GONE
                RequestHandler.createSubsJsonRequest(applicationContext,
                        mScratchToWinMessage!!.actiondata!!.type!!,
                        mScratchToWinMessage!!.actid.toString(),
                        mScratchToWinMessage!!.actiondata!!.auth!!, email)
                binding.viewToBeScratched.enableScratching()
                Toast.makeText(applicationContext, mScratchToWinMessage!!.actiondata!!.mailSubscriptionForm!!.successMessage, Toast.LENGTH_SHORT).show()
            } else {
                if (!checkEmail(email)) {
                    binding.invalidEmailMessage.visibility = View.VISIBLE
                } else {
                    binding.resultText.visibility = View.VISIBLE
                }
            }
        }
    }

    private val closeIcon: Int
        get() {
            when (mExtendedProps!!.closeButtonColor) {
                "white" -> return R.drawable.ic_close_white_24dp
                "black" -> return R.drawable.ic_close_black_24dp
            }
            return R.drawable.ic_close_black_24dp
        }

    private fun parseExtendedProps() {
        try {
            mExtendedProps = Gson().fromJson(URI(mScratchToWinMessage!!.actiondata!!.extendedProps).path, ScratchToWinExtendedProps::class.java)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Extended properties could not be parsed properly!")
            finish()
        }
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

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun checkEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkTheBoxes(): Boolean {
        return binding.emailPermitCheckbox.isChecked && binding.consentCheckbox.isChecked
    }

    private fun removeEmailViews() {
        binding.invalidEmailMessage.visibility = View.GONE
        binding.resultText.visibility = View.GONE
        binding.mailContainer.visibility = View.GONE
        binding.emailEdit.visibility = View.GONE
        binding.saveMail.visibility = View.GONE
    }

    override fun onScratchingComplete() {
        sendReport()
        binding.copyToClipboard.visibility = View.VISIBLE
    }

    private fun sendReport() {
        var report: MailSubReport?
        try {
            report = MailSubReport()
            report.impression = mScratchToWinMessage!!.actiondata!!.report!!.impression
            report.click = mScratchToWinMessage!!.actiondata!!.report!!.click
        } catch (e: Exception) {
            Log.e(LOG_TAG, "There is no report to send!")
            e.printStackTrace()
            report = null
        }
        if (report != null) {
            RequestHandler.createInAppActionClickRequest(applicationContext, report)
        }
    }

    companion object {
        private const val LOG_TAG = "ScratchToWinActivity"
    }
}