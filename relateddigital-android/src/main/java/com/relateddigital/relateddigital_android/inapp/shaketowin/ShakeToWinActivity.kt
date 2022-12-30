package com.relateddigital.relateddigital_android.inapp.shaketowin

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ExoPlayer
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinMailFormBinding
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinStep1Binding
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinStep2Binding
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinStep3Binding
import com.relateddigital.relateddigital_android.inapp.scratchtowin.ScratchToWinActivity
import com.relateddigital.relateddigital_android.model.*
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.util.ActivityUtils
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.StringUtils
import com.squareup.picasso.Picasso
import java.net.URI
import java.util.*
import java.util.regex.Pattern
import kotlin.math.sqrt

class ShakeToWinActivity : Activity(), SensorEventListener {
    private lateinit var bindingMailForm: ActivityShakeToWinMailFormBinding
    private lateinit var bindingStep1: ActivityShakeToWinStep1Binding
    private lateinit var bindingStep2: ActivityShakeToWinStep2Binding
    private lateinit var bindingStep3: ActivityShakeToWinStep3Binding
    private var mShakeToWinMessage : ShakeToWin? = null
    private var mExtendedProps : ShakeToWinExtendedProps? = null
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer = 0f
    private var mAccelerometerCurrent = 0f
    private var mAccelerometerLast = 0f
    private var mTimerWithoutShaking: Timer? = null
    private var mTimerAfterShaking: Timer? = null
    private var isShaken = false
    private var isStep3 = false
    private var player: ExoPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMailForm = ActivityShakeToWinMailFormBinding.inflate(layoutInflater)
        bindingStep1 = ActivityShakeToWinStep1Binding.inflate(layoutInflater)
        bindingStep2 = ActivityShakeToWinStep2Binding.inflate(layoutInflater)
        bindingStep3 = ActivityShakeToWinStep3Binding.inflate(layoutInflater)
        setContentView(bindingMailForm.root)

        shakeToWinMessage
        parseExtendedProps()
        //mShakeToWinMessage = getShakeToWinMessage()
        setupMailForm()
        cacheResources()

    }

    private val shakeToWinMessage: Unit
        get() {
            val intent = intent
            if (intent != null) {
                if (intent.hasExtra("shake-to-win-data")) {
                    mShakeToWinMessage = intent.getSerializableExtra("shake-to-win-data") as ShakeToWin?
                }
            }
            if (mShakeToWinMessage == null) {
                Log.e(LOG_TAG, "Could not get the content from the server!")
                finish()
            }
        }

    override fun onDestroy() {
        if (mTimerWithoutShaking != null) {
            mTimerWithoutShaking!!.cancel()
        }
        if (mTimerAfterShaking != null) {
            mTimerAfterShaking!!.cancel()
        }
        if (mSensorManager != null) {
            mSensorManager!!.unregisterListener(this, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
        }
        releasePlayer()
        //TODO real control whether the code is null or empty
        // set a variable on step3 when the code is shown
        if(mExtendedProps!!.promocodeBannerText!!.isNotEmpty()) {
            // start banner fragment here
            val shakeToWinCodeBannerFragment =
                ShakeToWinCodeBannerFragment.newInstance(mExtendedProps!!, mShakeToWinMessage!!.actiondata!!.promotionCode.toString())

            val transaction: FragmentTransaction =
                (ActivityUtils.parentActivity as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(android.R.id.content, shakeToWinCodeBannerFragment)
            transaction.commit()
            ActivityUtils.parentActivity = null
        }
        super.onDestroy()
    }

    private fun setupMailForm() {

        var isMailForm = mShakeToWinMessage!!.actiondata!!.mailSubscription!!

        if(isMailForm) {
            //TODO backgroundColor it can be wrong
            bindingMailForm.container.setBackgroundColor(Color.parseColor(mExtendedProps!!.backgroundColor))
            bindingMailForm.invalidEmailMessage.text = mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.invalidEmailMessage
            bindingMailForm.resultText.text = mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.checkConsentMessage
            bindingMailForm.emailPermitText.text = createHtml(mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.emailPermitText!!,
                mExtendedProps!!.emailPermitTextUrl)
            bindingMailForm.emailPermitText.textSize = mExtendedProps!!.emailPermitTextSize!!.toFloat()
            bindingMailForm.emailPermitText.setOnClickListener {
                if (!mExtendedProps!!.emailPermitTextUrl.isNullOrEmpty()) {
                    try {
                        val viewIntent = Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString(mExtendedProps!!.emailPermitTextUrl))
                        startActivity(viewIntent)
                    } catch (e: ActivityNotFoundException) {
                        Log.i(LOG_TAG, "Could not direct to the url entered!")
                    }
                }
            }
            bindingMailForm.consentText.text = createHtml(mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.consentText!!,
                mExtendedProps!!.consentTextUrl)
            bindingMailForm.consentText.textSize = mExtendedProps!!.consentTextSize!!.toFloat()
            bindingMailForm.consentText.setOnClickListener {
                if (!mExtendedProps!!.consentTextUrl.isNullOrEmpty()) {
                    try {
                        val viewIntent = Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString(mExtendedProps!!.consentTextUrl))
                        startActivity(viewIntent)
                    } catch (e: ActivityNotFoundException) {
                        Log.i(LOG_TAG, "Could not direct to the url entered!")
                    }
                }
            }

            val closeIcon = when (mExtendedProps!!.closeButtonColor) {
                "white" -> R.drawable.ic_close_white_24dp
                "black" -> R.drawable.ic_close_black_24dp
                else -> {
                    R.drawable.ic_close_black_24dp
                }
            }
            bindingMailForm.closeButton.setBackgroundResource(closeIcon)
            bindingMailForm.closeButton.setOnClickListener { finish() }
            //TODO Take into account the probability of being null after data comes for img
            if (mShakeToWinMessage!!.actiondata!!.img!!.isNotEmpty()) {
                Picasso.get().load(mShakeToWinMessage!!.actiondata!!.img)
                    .into(bindingMailForm.mainImage)
            } else {
                Glide.with(this)
                    .load(mShakeToWinMessage!!.actiondata!!.img)
                    .into(bindingMailForm.mainImage)
            }

            //TODO title and body can be wrong(I made similar to scratchToWin)
            bindingMailForm.titleText.text = mShakeToWinMessage!!.actiondata!!.contentTitle!!.replace("\\n", "\n")
            bindingMailForm.titleText.setTextColor(Color.parseColor(mExtendedProps!!.contentTitleTextColor))
            bindingMailForm.titleText.textSize = mExtendedProps!!.contentBodyTextSize!!.toFloat() + 12
            bindingMailForm.titleText.setTypeface(mExtendedProps!!.getContentTitleFontFamily(this), Typeface.BOLD)

            bindingMailForm.bodyText.text = mShakeToWinMessage!!.actiondata!!.contentBody!!.replace("\\n", "\n")
            bindingMailForm.bodyText.setTextColor(Color.parseColor(mExtendedProps!!.contentBodyTextColor))
            bindingMailForm.bodyText.textSize = mExtendedProps!!.contentBodyTextSize!!.toFloat() + 8
            bindingMailForm.bodyText.typeface = mExtendedProps!!.getContentBodyFontFamily(this)

            bindingMailForm.emailEdit.hint = mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.placeholder
            bindingMailForm.saveButton.text = mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.buttonLabel
            bindingMailForm.saveButton.setTextColor(Color.parseColor(mExtendedProps!!.buttonTextColor))
            bindingMailForm.saveButton.textSize = mExtendedProps!!.buttonTextSize!!.toFloat() + 10
            bindingMailForm.saveButton.typeface = mExtendedProps!!.getButtonFontFamily(this)
            bindingMailForm.saveButton.setBackgroundColor(Color.parseColor(mExtendedProps!!.buttonColor))
            bindingMailForm.emailEdit.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    hideKeyboard(v)
                }
            }
            bindingMailForm.saveButton.setOnClickListener {
                val email: String = bindingMailForm.emailEdit.text.toString()
                bindingMailForm.invalidEmailMessage.visibility = View.GONE
                bindingMailForm.resultText.visibility = View.GONE
                if (checkEmail(email) && checkTheBoxes()) {
                    bindingMailForm.mailContainer.visibility = View.GONE
                    bindingMailForm.emailEdit.visibility = View.GONE
                    bindingMailForm.saveButton.visibility = View.GONE
                    //TODO Up to the next comment line may be unnecessary
                    RequestHandler.createSubsJsonRequest(applicationContext,
                        mShakeToWinMessage!!.actiondata!!.type!!,
                        mShakeToWinMessage!!.actid.toString(),
                        mShakeToWinMessage!!.actiondata!!.auth!!, email)
                    //To here
                    Toast.makeText(applicationContext, mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.successMessage, Toast.LENGTH_SHORT).show()
                    setContentView(bindingStep1.root)
                    setupStep1View()
                } else {
                    if (!checkEmail(email)) {
                        bindingMailForm.invalidEmailMessage.visibility = View.VISIBLE
                    } else {
                        bindingMailForm.resultText.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            setContentView(bindingStep1.root)
            setupStep1View()
        }
    }

    private fun setupStep1View() {
        //TODO Ask that title, text and background color is useless or not
        val isRuleScreen = true

        if(isRuleScreen) {
            setupCloseButtonStep1()
            //bindingStep1.container.setBackgroundColor(Color.parseColor(mShakeToWinMessage!!.actiondata!!.gamificationRules!!.backgroundImage))
            Picasso.get()
                .load(mShakeToWinMessage!!.actiondata!!.gamificationRules!!.backgroundImage)
                .into(bindingStep1.imageView)
            //bindingStep1.titleView.text = "Title".replace("\\n", "\n")
            //bindingStep1.titleView.setTextColor(Color.parseColor("#92008c"))
            //bindingStep1.titleView.textSize = 32f
            //bindingStep1.bodyTextView.text = "Text".replace("\\n", "\n")
            //bindingStep1.bodyTextView.setTextColor(Color.parseColor("#4060ff"))
            //bindingStep1.bodyTextView.textSize = 24f
            bindingStep1.buttonView.text = mShakeToWinMessage!!.actiondata!!.gamificationRules!!.buttonLabel
            bindingStep1.buttonView.setBackgroundColor(Color.parseColor(mExtendedProps!!.gamificationRules!!.buttonColor))
            bindingStep1.buttonView.setTextColor(Color.parseColor(mExtendedProps!!.gamificationRules!!.buttonTextColor))
            bindingStep1.buttonView.textSize = mExtendedProps!!.gamificationRules!!.buttonTextSize!!.toFloat() +10
            bindingStep1.buttonView.setOnClickListener {
                setContentView(bindingStep2.root)
                setupStep2View()
            }
        } else {
            setContentView(bindingStep2.root)
            setupStep2View()
        }
    }

    private fun setupStep2View() {
        startPlayer()
        initAccelerometer()
    }

    private fun setupCloseButtonStep1() {
        bindingStep1.closeButton.setBackgroundResource(closeIconStep1)
        bindingStep1.closeButton.setOnClickListener { finish() }
    }

    //TODO when real data comes:
    /* switch (mInAppMessage.getActionData().getCloseButtonColor()) {

         case "white":
             return R.drawable.ic_close_white_24dp;

         case "black":
             return R.drawable.ic_close_black_24dp;
     }
     return R.drawable.ic_close_black_24dp;*/
    private val closeIconStep1: Int
        get() = R.drawable.ic_close_black_24dp
    //TODO when real data comes:
    /* switch (mInAppMessage.getActionData().getCloseButtonColor()) {

         case "white":
             return R.drawable.ic_close_white_24dp;

         case "black":
             return R.drawable.ic_close_black_24dp;
     }
     return R.drawable.ic_close_black_24dp;*/

    private fun initAccelerometer() {
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mSensorManager!!.registerListener(this, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
        mAccelerometer = 10f
        mAccelerometerCurrent = SensorManager.GRAVITY_EARTH
        mAccelerometerLast = SensorManager.GRAVITY_EARTH
        mTimerWithoutShaking = Timer("ShakeToWinTimerWithoutShaking", false)
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                if (!isShaken) {
                    runOnUiThread { setupStep3View() }
                }
            }
        }
        mTimerWithoutShaking!!.schedule(task, 5000)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!isStep3) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            mAccelerometerLast = mAccelerometerCurrent
            mAccelerometerCurrent = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta = mAccelerometerCurrent - mAccelerometerLast
            mAccelerometer = mAccelerometer * 0.9f + delta
            if (mAccelerometer > 12) {
                isShaken = true
                mTimerAfterShaking = Timer("ShakeToWinTimerAfterShaking", false)
                val task: TimerTask = object : TimerTask() {
                    override fun run() {
                        runOnUiThread { setupStep3View() }
                    }
                }
                mTimerAfterShaking!!.schedule(task, mShakeToWinMessage!!.actiondata!!.gameElements!!.shakingTime!!.toLong()) //TODO: real data here
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    private fun setupStep3View() {
        releasePlayer()
        if (mTimerWithoutShaking != null) {
            mTimerWithoutShaking!!.cancel()
        }
        if (mTimerAfterShaking != null) {
            mTimerAfterShaking!!.cancel()
        }
        isStep3 = true
        mSensorManager!!.unregisterListener(this, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
        setContentView(bindingStep3.root)

        //TODO : is image useless? float?
        setupCloseButtonStep3()
        bindingStep3.container.setBackgroundColor(Color.parseColor(mExtendedProps!!.promocodeBackgroundColor))
        //Picasso.get().load("hploaded_images/163_1100_490_20210319175823217.jpg")
          //      .into(bindingStep3.imageView)
        bindingStep3.titleView.text = mShakeToWinMessage!!.actiondata!!.gameResultElements!!.title!!.replace("\\n", "\n")
        bindingStep3.titleView.setTextColor(Color.parseColor(mExtendedProps!!.gameResultElements!!.titleTextColor))
        bindingStep3.titleView.textSize = mExtendedProps!!.gameResultElements!!.titleTextSize!!.toFloat() +12
        bindingStep3.bodyTextView.text =mShakeToWinMessage!!.actiondata!!.gameResultElements!!.message!!.replace("\\n", "\n")
        bindingStep3.bodyTextView.setTextColor(Color.parseColor(mExtendedProps!!.gameResultElements!!.textColor))
        bindingStep3.bodyTextView.textSize = mExtendedProps!!.gameResultElements!!.textSize!!.toFloat() +8
        bindingStep3.couponView.setBackgroundColor(Color.parseColor(mExtendedProps!!.promocodeBackgroundColor))
        bindingStep3.couponCodeView.text = mShakeToWinMessage!!.actiondata!!.promotionCode
        bindingStep3.couponCodeView.setTextColor(Color.parseColor(mExtendedProps!!.promocodeTextColor))
        bindingStep3.buttonView.text = mShakeToWinMessage!!.actiondata!!.copybuttonLabel
        bindingStep3.buttonView.setBackgroundColor(Color.parseColor(mExtendedProps!!.copybuttonColor))
        bindingStep3.buttonView.setTextColor(Color.parseColor(mExtendedProps!!.copybuttonTextColor))
        bindingStep3.buttonView.textSize = mExtendedProps!!.copybuttonTextSize!!.toFloat() +10

        bindingStep3.buttonView.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(
                getString(R.string.coupon_code),
                mShakeToWinMessage!!.actiondata!!.promotionCode
            )
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
                applicationContext,
                getString(R.string.copied_to_clipboard),
                Toast.LENGTH_LONG
            ).show()
             if (mShakeToWinMessage!!.actiondata!!.copybuttonFunction.equals(Constants.BUTTON_COPY_REDIRECT)){

                 try {
                     val viewIntent = Intent(
                         Intent.ACTION_VIEW,
                         StringUtils.getURIfromUrlString(mShakeToWinMessage!!.actiondata!!.androidLnk)
                     )
                     startActivity(viewIntent)
                 } catch (e: Exception) {
                     Log.i(LOG_TAG, "Error : Could not direct to the URI given")
                 }
             }
        }
    }

    private fun setupCloseButtonStep3() {
        bindingStep3.closeButton.setBackgroundResource(closeIconStep3)
        bindingStep3.closeButton.setOnClickListener { finish() }
    }

    //TODO when real data comes:
    /* switch (mInAppMessage.getActionData().getCloseButtonColor()) {

         case "white":
             return R.drawable.ic_close_white_24dp;

         case "black":
             return R.drawable.ic_close_black_24dp;
     }
     return R.drawable.ic_close_black_24dp;*/
    private val closeIconStep3: Int
        get() = R.drawable.ic_close_black_24dp
    //TODO when real data comes:
    /* switch (mInAppMessage.getActionData().getCloseButtonColor()) {

         case "white":
             return R.drawable.ic_close_white_24dp;

         case "black":
             return R.drawable.ic_close_black_24dp;
     }
     return R.drawable.ic_close_black_24dp;*/

    private fun startPlayer() {
        player!!.playWhenReady = true
    }

    private fun releasePlayer() {
        if (player != null) {
            player!!.release()
            player = null
        }
    }

    private fun cacheResources() {

        initializePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        bindingStep2.videoView.player = player
        val mediaItem = MediaItem.fromUri(
            mShakeToWinMessage!!.actiondata!!.gameElements!!.videoUrl.toString())
        player!!.setMediaItem(mediaItem)
        player!!.prepare()
    }

    private fun parseExtendedProps() {
        try {
            mExtendedProps = Gson().fromJson(URI(mShakeToWinMessage!!.actiondata!!.ExtendedProps).path, ShakeToWinExtendedProps::class.java)
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
        return bindingMailForm.emailPermitCheckbox.isChecked && bindingMailForm.consentCheckbox.isChecked
    }

    companion object {
        private const val LOG_TAG = "ShakeToWinActivity"
    }
}