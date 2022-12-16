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
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ExoPlayer
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinMailFormBinding
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinStep1Binding
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinStep2Binding
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinStep3Binding
import com.relateddigital.relateddigital_android.inapp.scratchtowin.ScratchToWinActivity
import com.relateddigital.relateddigital_android.model.*
import com.relateddigital.relateddigital_android.network.RequestHandler
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
    //private var isMailForm = false?
    private var player: ExoPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMailForm = ActivityShakeToWinMailFormBinding.inflate(layoutInflater)
        bindingStep1 = ActivityShakeToWinStep1Binding.inflate(layoutInflater)
        bindingStep2 = ActivityShakeToWinStep2Binding.inflate(layoutInflater)
        bindingStep3 = ActivityShakeToWinStep3Binding.inflate(layoutInflater)
        setContentView(bindingMailForm.root)
        cacheResources()

        //mShakeToWinMessage
        shakeToWinMessage
        parseExtendedProps()
        //mShakeToWinMessage = getShakeToWinMessage()
        setupMailForm()
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
        if(true) {
            // start banner fragment here
            /*val spinToWinCodeBannerFragment =
                SpinToWinCodeBannerFragment.newInstance(extendedProps, spinToWinPromotionCode)

            val transaction: FragmentTransaction =
                (ActivityUtils.parentActivity as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(android.R.id.content, spinToWinCodeBannerFragment)
            transaction.commit()
            ActivityUtils.parentActivity = null */
        }
        super.onDestroy()
    }

    private fun setupMailForm() {
        //TODO : real data usage
        var isMailForm = mShakeToWinMessage!!.actiondata!!.mailSubscription!!

        if(isMailForm) {
            //TODO Get this from scratch-to-win
            bindingMailForm.container.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            bindingMailForm.invalidEmailMessage.text = "invalid e-mail"
            bindingMailForm.resultText.text = "missing consent"
            bindingMailForm.emailPermitText.text = createHtml("email permit text",
                "https://www.relateddigital.com/en/")
            bindingMailForm.emailPermitText.textSize = (5 + 10).toFloat()
            bindingMailForm.emailPermitText.setOnClickListener {
                if (true /*!mExtendedProps!!.emailPermitTextUrl.isNullOrEmpty()*/) {
                    try {
                        val viewIntent = Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString("https://www.relateddigital.com/en/"))
                        startActivity(viewIntent)
                    } catch (e: ActivityNotFoundException) {
                        Log.i(LOG_TAG, "Could not direct to the url entered!")
                    }
                }
            }
            bindingMailForm.consentText.text = createHtml("consent text",
                "https://www.relateddigital.com/en/")
            bindingMailForm.consentText.textSize = (5 + 10).toFloat()
            bindingMailForm.consentText.setOnClickListener {
                if (true /*!mExtendedProps!!.consentTextUrl.isNullOrEmpty()*/) {
                    try {
                        val viewIntent = Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString("https://www.relateddigital.com/en/"))
                        startActivity(viewIntent)
                    } catch (e: ActivityNotFoundException) {
                        Log.i(LOG_TAG, "Could not direct to the url entered!")
                    }
                }
            }

            val closeIcon = when ("white" /*mExtendedProps!!.closeButtonColor*/) {
                "white" -> R.drawable.ic_close_white_24dp
                "black" -> R.drawable.ic_close_black_24dp
                else -> {
                    R.drawable.ic_close_black_24dp
                }
            }
            bindingMailForm.closeButton.setBackgroundResource(closeIcon)
            bindingMailForm.closeButton.setOnClickListener { finish() }

            if (AppUtils.isAnImage("https://media-exp1.licdn.com/dms/image/C4D0BAQF38lnp1R13IQ/company-logo_200_200/0/1657136683972?e=2147483647&v=beta&t=MHcWTkD4BHdAoGyB3_byd679OeBDFkUOC_xOJJkmC6I")) {
                Picasso.get().load("https://media-exp1.licdn.com/dms/image/C4D0BAQF38lnp1R13IQ/company-logo_200_200/0/1657136683972?e=2147483647&v=beta&t=MHcWTkD4BHdAoGyB3_byd679OeBDFkUOC_xOJJkmC6I")
                    .into(bindingMailForm.mainImage)
            } else {
                Glide.with(this)
                    .load("https://media-exp1.licdn.com/dms/image/C4D0BAQF38lnp1R13IQ/company-logo_200_200/0/1657136683972?e=2147483647&v=beta&t=MHcWTkD4BHdAoGyB3_byd679OeBDFkUOC_xOJJkmC6I")
                    .into(bindingMailForm.mainImage)
            }

            bindingMailForm.titleText.text = "Login with your email"
            bindingMailForm.titleText.setTextColor(ContextCompat.getColor(this, R.color.white))
            bindingMailForm.titleText.textSize = (5 + 16).toFloat()
            bindingMailForm.titleText.typeface = Typeface.DEFAULT

            bindingMailForm.bodyText.text = "Login with your email to get promotion codes"
            bindingMailForm.bodyText.setTextColor(ContextCompat.getColor(this, R.color.white))
            bindingMailForm.bodyText.textSize = (5 + 10).toFloat()
            bindingMailForm.bodyText.typeface = Typeface.DEFAULT

            bindingMailForm.emailEdit.hint = "your email"
            bindingMailForm.saveButton.text = "SAVE"
            bindingMailForm.saveButton.setTextColor(ContextCompat.getColor(this, R.color.black))
            bindingMailForm.saveButton.textSize = (5 + 20).toFloat()
            bindingMailForm.saveButton.typeface = Typeface.DEFAULT
            bindingMailForm.saveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
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
                    /*RequestHandler.createSubsJsonRequest(applicationContext,
                        mScratchToWinMessage!!.actiondata!!.type!!,
                        mScratchToWinMessage!!.actid.toString(),
                        mScratchToWinMessage!!.actiondata!!.auth!!, email)*/
                    Toast.makeText(applicationContext, "Successful", Toast.LENGTH_SHORT).show()
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
        //TODO : replace this dummy data with the real one later
        //TODO : check and set the visibilities. Only the button is mandatory
        val isRuleScreen = true

        if(isRuleScreen) {
            setupCloseButtonStep1()
            bindingStep1.container.setBackgroundColor(Color.parseColor("#ff99de"))
            Picasso.get()
                .load("https://imgvisilabsnet.azureedge.net/in-app-message/uploaded_images/163_1100_490_20210319175823217.jpg")
                .into(bindingStep1.imageView)
            bindingStep1.titleView.text = "Title".replace("\\n", "\n")
            bindingStep1.titleView.setTextColor(Color.parseColor("#92008c"))
            bindingStep1.titleView.textSize = 32f
            bindingStep1.bodyTextView.text = "Text".replace("\\n", "\n")
            bindingStep1.bodyTextView.setTextColor(Color.parseColor("#4060ff"))
            bindingStep1.bodyTextView.textSize = 24f
            bindingStep1.buttonView.text = "Button"
            bindingStep1.buttonView.setBackgroundColor(Color.parseColor("#79e7ff"))
            bindingStep1.buttonView.setTextColor(Color.parseColor("#000000"))
            bindingStep1.buttonView.textSize = 24f
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
                mTimerAfterShaking!!.schedule(task, 0) //TODO: real data here
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

        //TODO : replace this dummy data with the real one later
        //TODO : check and set the visibilities.
        setupCloseButtonStep3()
        bindingStep3.container.setBackgroundColor(Color.parseColor("#ff99de"))
        Picasso.get().load("https://imgvisilabsnet.azureedge.net/in-app-message/uploaded_images/163_1100_490_20210319175823217.jpg")
                .into(bindingStep3.imageView)
        bindingStep3.titleView.text = "Title".replace("\\n", "\n")
        bindingStep3.titleView.setTextColor(Color.parseColor("#92008c"))
        bindingStep3.titleView.textSize = 32f
        bindingStep3.bodyTextView.text = "Text".replace("\\n", "\n")
        bindingStep3.bodyTextView.setTextColor(Color.parseColor("#4060ff"))
        bindingStep3.bodyTextView.textSize = 24f
        bindingStep3.couponView.setBackgroundColor(Color.parseColor("#00ffab"))
        bindingStep3.couponCodeView.text = "SDFJSDKFMSASDAKASD"
        bindingStep3.couponCodeView.setTextColor(Color.parseColor("#400080"))
        bindingStep3.couponCopyButton.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(
                getString(R.string.coupon_code),
                "SDFJSDKFMSASDAKASD"
            ) //TODO : real promo code here
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
                applicationContext,
                getString(R.string.copied_to_clipboard),
                Toast.LENGTH_LONG
            ).show()
        }
        bindingStep3.buttonView.text = "Button"
        bindingStep3.buttonView.setBackgroundColor(Color.parseColor("#79e7ff"))
        bindingStep3.buttonView.setTextColor(Color.parseColor("#000000"))
        bindingStep3.buttonView.textSize = 24f
        bindingStep3.buttonView.setOnClickListener {
            try {
                val viewIntent = Intent(
                    Intent.ACTION_VIEW,
                    StringUtils.getURIfromUrlString("https://www.relateddigital.com")
                ) // TODO : real data here
                startActivity(viewIntent)
            } catch (e: Exception) {
                Log.i(LOG_TAG, "Error : Could not direct to the URI given")
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
        //TODO : cache video in step 2 and picture in step 3 here
        Picasso.get().load("https://imgvisilabsnet.azureedge.net/in-app-message/uploaded_images/163_1100_490_20210319175823217.jpg")
                .fetch()
        initializePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        bindingStep2.videoView.player = player
        val mediaItem = MediaItem.fromUri(
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4") //TODO : real url here
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