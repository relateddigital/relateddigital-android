package com.relateddigital.relateddigital_android.inapp.shaketowin

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinMailFormBinding
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinStep1Binding
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinStep2Binding
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinStep3Binding
import com.relateddigital.relateddigital_android.model.*
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.SubsJsonRequest
import com.relateddigital.relateddigital_android.util.ActivityUtils
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
    private var mShakeToWinMessage: ShakeToWin? = null
    private var mExtendedProps: ShakeToWinExtendedProps? = null
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer = 0f
    private var mAccelerometerCurrent = 0f
    private var mAccelerometerLast = 0f
    private var mTimerWithoutShaking: Timer? = null
    private var mTimerAfterShaking: Timer? = null
    private var isShaken = false
    private var isStep3 = false
    private var player: ExoPlayer? = null
    private var soundPlayer: ExoPlayer? = null
    private var promoemail =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isAndroidTV(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        bindingMailForm = ActivityShakeToWinMailFormBinding.inflate(layoutInflater)
        bindingStep1 = ActivityShakeToWinStep1Binding.inflate(layoutInflater)
        bindingStep2 = ActivityShakeToWinStep2Binding.inflate(layoutInflater)
        bindingStep3 = ActivityShakeToWinStep3Binding.inflate(layoutInflater)
        setContentView(bindingMailForm.root)

        shakeToWinMessage
        parseExtendedProps()
        cacheResources()
        setupMailForm()

    }

    private val shakeToWinMessage: Unit
        get() {
            val intent = intent
            if (intent != null) {
                if (intent.hasExtra("shake-to-win-data")) {
                    mShakeToWinMessage =
                        intent.getSerializableExtra("shake-to-win-data") as ShakeToWin?
                }
            }
            if (mShakeToWinMessage == null) {
                Log.e(LOG_TAG, "Could not get the content from the server!")
                finish()
            }
        }

    override fun onDestroy() {
        super.onDestroy()

        if (mTimerWithoutShaking != null) {
            mTimerWithoutShaking!!.cancel()
        }
        if (mTimerAfterShaking != null) {
            mTimerAfterShaking!!.cancel()
        }
        if (mSensorManager != null) {
            mSensorManager!!.unregisterListener(
                this,
                mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            )
        }
        releasePlayer()
        if (mExtendedProps!!.promocodeBannerText!!.isNotEmpty() && isStep3) {
            try {
                val extendedProps = Gson().fromJson(
                    URI(mShakeToWinMessage!!.actiondata!!.ExtendedProps).path,
                    ShakeToWinExtendedProps::class.java
                )
                val shakeToWinCodeBannerFragment =
                    ShakeToWinCodeBannerFragment.newInstance(
                        extendedProps,
                        mShakeToWinMessage!!.actiondata!!.promotionCode.toString()
                    )

                val transaction: FragmentTransaction =
                    (ActivityUtils.parentActivity as FragmentActivity).supportFragmentManager.beginTransaction()
                transaction.replace(android.R.id.content, shakeToWinCodeBannerFragment)
                transaction.commit()
                ActivityUtils.parentActivity = null
            } catch (e: Exception) {
                Log.e(LOG_TAG, "ShakeToWinCodeBanner : " + e.message)
            }
        }
    }

    private fun isAndroidTV(context: Context): Boolean {
        return context.packageManager.hasSystemFeature("android.software.leanback")
    }
    private fun setupMailForm() {

        val isMailForm = mShakeToWinMessage!!.actiondata!!.mailSubscription!!

        if (isMailForm) {
            if (mExtendedProps!!.backgroundColor!!.isNotEmpty()) {
                bindingMailForm.container.setBackgroundColor(Color.parseColor(mExtendedProps!!.backgroundColor))
            } else if (mExtendedProps!!.backgroundImage!!.isNotEmpty()) {

                Picasso.get().load(mExtendedProps!!.backgroundImage)
                    .into(object : com.squareup.picasso.Target {
                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            Log.i(LOG_TAG, "Could not background Image entered!")
                        }

                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            bindingMailForm.container.background = BitmapDrawable(resources, bitmap)
                        }

                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                    })
            }

            bindingMailForm.invalidEmailMessage.text =
                mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.invalidEmailMessage
            bindingMailForm.resultText.text =
                mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.checkConsentMessage
            bindingMailForm.emailPermitText.text = createHtml(
                mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.emailPermitText!!,
                mExtendedProps!!.mailSubscriptionForm!!.emailPermitTextUrl!!
            )
            bindingMailForm.emailPermitText.textSize =
                mExtendedProps!!.mailSubscriptionForm!!.emailPermitTextSize!!.toFloat() + 10
            bindingMailForm.emailPermitText.setOnClickListener {
                if (!mExtendedProps!!.mailSubscriptionForm!!.emailPermitTextUrl.isNullOrEmpty()) {
                    try {
                        val viewIntent = Intent(
                            Intent.ACTION_VIEW,
                            StringUtils.getURIfromUrlString(mExtendedProps!!.mailSubscriptionForm!!.emailPermitTextUrl)
                        )
                        startActivity(viewIntent)
                    } catch (e: ActivityNotFoundException) {
                        Log.i(LOG_TAG, "Could not direct to the url entered!")
                    }
                }
            }
            if (mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.consentText!!.isNotEmpty()) {
                bindingMailForm.consentText.text = createHtml(
                    mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.consentText!!,
                    mExtendedProps!!.mailSubscriptionForm!!.consentTextUrl
                )
                bindingMailForm.consentText.textSize =
                    mExtendedProps!!.mailSubscriptionForm!!.consentTextSize!!.toFloat() + 10
                bindingMailForm.consentText.setOnClickListener {
                    if (!mExtendedProps!!.mailSubscriptionForm!!.consentTextUrl.isNullOrEmpty()) {
                        try {
                            val viewIntent = Intent(
                                Intent.ACTION_VIEW,
                                StringUtils.getURIfromUrlString(mExtendedProps!!.mailSubscriptionForm!!.consentTextUrl)
                            )
                            startActivity(viewIntent)
                        } catch (e: ActivityNotFoundException) {
                            Log.i(LOG_TAG, "Could not direct to the url entered!")
                        }
                    }
                }
            }
            bindingMailForm.closeButton.setBackgroundResource(closeIconColor())
            bindingMailForm.closeButton.setOnClickListener { finish() }

            if (mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.title!!.isNotEmpty()) {
                bindingMailForm.titleText.text =
                    mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.title!!.replace(
                        "\\n",
                        "\n"
                    )
                bindingMailForm.titleText.setTextColor(Color.parseColor(mExtendedProps!!.mailSubscriptionForm!!.titleTextColor))
                bindingMailForm.titleText.textSize =
                    mExtendedProps!!.mailSubscriptionForm!!.titleTextSize!!.toFloat() + 12
                bindingMailForm.titleText.setTypeface(
                    mExtendedProps!!.getContentTitleFontFamily(
                        this
                    ), Typeface.BOLD
                )
            }
            if (mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.message!!.isNotEmpty()) {
                bindingMailForm.bodyText.text =
                    mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.message!!.replace(
                        "\\n",
                        "\n"
                    )
                bindingMailForm.bodyText.setTextColor(Color.parseColor(mExtendedProps!!.mailSubscriptionForm!!.textColor))
                bindingMailForm.bodyText.textSize =
                    mExtendedProps!!.mailSubscriptionForm!!.textSize!!.toFloat() + 8
                bindingMailForm.bodyText.typeface = mExtendedProps!!.getContentBodyFontFamily(this)

            }
            bindingMailForm.emailEdit.hint =
                mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.placeholder
            bindingMailForm.saveButton.text =
                mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.buttonLabel
            bindingMailForm.saveButton.setTextColor(Color.parseColor(mExtendedProps!!.mailSubscriptionForm!!.buttonTextColor))
            bindingMailForm.saveButton.textSize =
                mExtendedProps!!.mailSubscriptionForm!!.buttonTextSize!!.toFloat() + 10
            bindingMailForm.saveButton.typeface = mExtendedProps!!.getButtonFontFamily(this)
            bindingMailForm.saveButton.setBackgroundColor(Color.parseColor(mExtendedProps!!.mailSubscriptionForm!!.buttonColor))
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
                    SubsJsonRequest.createSubsJsonRequest(
                        applicationContext,
                        mShakeToWinMessage!!.actiondata!!.type!!,
                        mShakeToWinMessage!!.actid.toString(),
                        mShakeToWinMessage!!.actiondata!!.auth!!, email
                    )

                    Toast.makeText(
                        applicationContext,
                        mShakeToWinMessage!!.actiondata!!.mailSubscriptionForm!!.successMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    setContentView(bindingStep1.root)
                    promoemail = email
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

    private val backgroundImageTarget = object : com.squareup.picasso.Target {
        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            Log.i(LOG_TAG, "Could not background Image entered!")
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            bindingStep1.linearLayout.background = BitmapDrawable(resources, bitmap)
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }

    private fun setupStep1View() {
        val isRuleScreen = true
        if (isRuleScreen) {
            setupCloseButtonStep1()
            if (mShakeToWinMessage!!.actiondata!!.gamificationRules!!.backgroundImage!!.isNotEmpty()) {
                Picasso.get()
                    .load(mShakeToWinMessage!!.actiondata!!.gamificationRules!!.backgroundImage)
                    .into(bindingStep1.imageView, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            if (mExtendedProps!!.backgroundColor!!.isNotEmpty()) {
                                bindingStep1.container.setBackgroundColor(Color.parseColor(mExtendedProps!!.backgroundColor))
                            } else if (mExtendedProps!!.backgroundImage!!.isNotEmpty()) {
                                Picasso.get()
                                    .load(mExtendedProps!!.backgroundImage)
                                    .into(backgroundImageTarget)
                            }
                            bindingStep1.buttonView.text =
                                mShakeToWinMessage!!.actiondata!!.gamificationRules!!.buttonLabel
                            bindingStep1.buttonView.setBackgroundColor(Color.parseColor(mExtendedProps!!.gamificationRules!!.buttonColor))
                            bindingStep1.buttonView.setTextColor(Color.parseColor(mExtendedProps!!.gamificationRules!!.buttonTextColor))
                            bindingStep1.buttonView.textSize =
                                mExtendedProps!!.gamificationRules!!.buttonTextSize!!.toFloat() + 10
                            bindingStep1.buttonView.setOnClickListener {
                                setContentView(bindingStep2.root)
                                setupStep2View()
                            }


                        }

                        override fun onError(e: Exception?) {

                        }

                    })
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
        bindingStep1.closeButton.setBackgroundResource(closeIconColor())
        bindingStep1.closeButton.setOnClickListener { finish() }
    }

    private fun closeIconColor(): Int {
        if (mExtendedProps!!.closeButtonColor!! == "white") {
            return R.drawable.ic_close_white_24dp
        } else if (mExtendedProps!!.closeButtonColor!! == "black") {
            return R.drawable.ic_close_black_24dp
        }
        return R.drawable.ic_close_black_24dp
    }

    private fun initAccelerometer() {
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mSensorManager!!.registerListener(
            this, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
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
                mTimerAfterShaking!!.schedule(
                    task,
                    mShakeToWinMessage!!.actiondata!!.gameElements!!.shakingTime!!.toLong()
                )
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
        mSensorManager!!.unregisterListener(
            this,
            mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        )
        setContentView(bindingStep3.root)

        setupCloseButtonStep3()
        if (mExtendedProps!!.backgroundColor!!.isNotEmpty()) {
            bindingStep3.container.setBackgroundColor(Color.parseColor(mExtendedProps!!.backgroundColor))
        } else if (mExtendedProps!!.backgroundImage!!.isNotEmpty()) {

            Picasso.get().load(mExtendedProps!!.backgroundImage)
                .into(object : com.squareup.picasso.Target {
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        Log.i(LOG_TAG, "Could not background Image entered!")

                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        bindingStep3.linearLayout.background = BitmapDrawable(resources, bitmap)
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                })
        }


        if (mShakeToWinMessage!!.actiondata!!.gameResultElements!!.title!!.isNotEmpty()) {
            bindingStep3.titleView.text =
                mShakeToWinMessage!!.actiondata!!.gameResultElements!!.title!!.replace("\\n", "\n")
            bindingStep3.titleView.setTextColor(Color.parseColor(mExtendedProps!!.gameResultElements!!.titleTextColor))
            bindingStep3.titleView.textSize =
                mExtendedProps!!.gameResultElements!!.titleTextSize!!.toFloat() + 12
        }
        if (mShakeToWinMessage!!.actiondata!!.gameResultElements!!.message!!.isNotEmpty()) {
            bindingStep3.bodyTextView.text =
                mShakeToWinMessage!!.actiondata!!.gameResultElements!!.message!!.replace(
                    "\\n",
                    "\n"
                )
            bindingStep3.bodyTextView.setTextColor(Color.parseColor(mExtendedProps!!.gameResultElements!!.textColor))
            bindingStep3.bodyTextView.textSize =
                mExtendedProps!!.gameResultElements!!.textSize!!.toFloat() + 8
        }
        bindingStep3.couponView.setBackgroundColor(Color.parseColor(mExtendedProps!!.promocodeBackgroundColor))
        bindingStep3.couponCodeView.text = mShakeToWinMessage!!.actiondata!!.promotionCode
        bindingStep3.couponCodeView.setTextColor(Color.parseColor(mExtendedProps!!.promocodeTextColor))
        bindingStep3.buttonView.text = mShakeToWinMessage!!.actiondata!!.copybuttonLabel
        bindingStep3.buttonView.setBackgroundColor(Color.parseColor(mExtendedProps!!.copybuttonColor))
        bindingStep3.buttonView.setTextColor(Color.parseColor(mExtendedProps!!.copybuttonTextColor))
        bindingStep3.buttonView.textSize = mExtendedProps!!.copybuttonTextSize!!.toFloat() + 10
        sendPromotionCodeInfo(email = promoemail, promotionCode = mShakeToWinMessage!!.actiondata!!.promotionCode.toString())
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
            if (mShakeToWinMessage!!.actiondata!!.copybuttonFunction.equals(Constants.BUTTON_COPY_REDIRECT)) {

                try {
                    val viewIntent = Intent(
                        Intent.ACTION_VIEW,
                        StringUtils.getURIfromUrlString(mShakeToWinMessage!!.actiondata!!.androidLnk)
                    )
                    startActivity(viewIntent)
                    finish()
                } catch (e: Exception) {
                    Log.i(LOG_TAG, "Error : Could not direct to the URI given")
                }
            }
            else
                finish()
        }

    }

    private fun setupCloseButtonStep3() {
        bindingStep3.closeButton.setBackgroundResource(closeIconColor())
        bindingStep3.closeButton.setOnClickListener { finish() }
    }

    private fun startPlayer() {
        player!!.playWhenReady = true
    }

    private fun releasePlayer() {
        if (player != null) {
            player!!.release()
            player = null
        }
        if (soundPlayer != null) {
            soundPlayer!!.release()
            soundPlayer = null
        }
    }

    private fun cacheResources() {
        initializeSoundPlayer()
        initializePlayer()

    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()

        // Video URL'si varsa kontrol ediyoruz
        if (!mShakeToWinMessage!!.actiondata!!.gameElements!!.videoUrl.toString().isNullOrEmpty()) {
            val url = mShakeToWinMessage!!.actiondata!!.gameElements!!.videoUrl.toString()

            if (url.endsWith(".mp4")) {
                bindingStep2.videoView.visibility = View.VISIBLE
                bindingStep2.videoView.player = player
                bindingStep2.videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                val mediaItem = MediaItem.fromUri(url)
                player!!.setMediaItem(mediaItem)
                player!!.prepare()
            } else if (url.endsWith(".gif")) {
                bindingStep2.videoView.visibility = View.GONE
                bindingStep2.imageViewGif.visibility = View.VISIBLE
                val gifUrl = mShakeToWinMessage!!.actiondata!!.gameElements!!.videoUrl.toString()

                Glide.with(this).load(gifUrl)
                    .into(DrawableImageViewTarget(bindingStep2.imageViewGif))
            } else {
                Log.e(LOG_TAG, "Video URL düzgün parse edilemedi!")
                bindingStep2.videoView.visibility = View.VISIBLE
                bindingStep2.videoView.player = player
                bindingStep2.videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                val mediaItem = MediaItem.fromUri(url)
                player!!.setMediaItem(mediaItem)
                player!!.prepare()
            }
        }
    }

    private fun initializeSoundPlayer() {
        soundPlayer = ExoPlayer.Builder(this).build()
        val mediaItem = MediaItem.fromUri(
            mShakeToWinMessage!!.actiondata!!.gameElements!!.soundUrl.toString()
        )
        soundPlayer!!.setMediaItem(mediaItem)
        soundPlayer!!.prepare()
        soundPlayer!!.playWhenReady = true
        soundPlayer!!.repeatMode = Player.REPEAT_MODE_ONE
    }

    private fun parseExtendedProps() {
        try {
            mExtendedProps = Gson().fromJson(
                URI(mShakeToWinMessage!!.actiondata!!.ExtendedProps).path,
                ShakeToWinExtendedProps::class.java
            )
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

    private fun sendPromotionCodeInfo(email: String, promotionCode: String) {
        val actionId = "act-" + mShakeToWinMessage!!.actid
        val parameters = HashMap<String, String>()
        parameters[Constants.PROMOTION_CODE_REQUEST_KEY] = promotionCode
        parameters[Constants.ACTION_ID_REQUEST_KEY] = actionId
        if (email.isNotEmpty()) {
            parameters[Constants.PROMOTION_CODE_EMAIL_REQUEST_KEY] = email
        }
        RelatedDigital.customEvent(
            context = this,
            pageName = Constants.PAGE_NAME_REQUEST_VAL,
            properties = parameters)
    }

    companion object {
        private const val LOG_TAG = "ShakeToWinActivity"
    }
}