package com.relateddigital.relateddigital_android.inapp.shaketowin

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ExoPlayer
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinStep1Binding
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinStep2Binding
import com.relateddigital.relateddigital_android.databinding.ActivityShakeToWinStep3Binding
import com.relateddigital.relateddigital_android.util.StringUtils
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.math.sqrt

class ShakeToWinActivity : Activity(), SensorEventListener {
    private lateinit var bindingStep1: ActivityShakeToWinStep1Binding
    private lateinit var bindingStep2: ActivityShakeToWinStep2Binding
    private lateinit var bindingStep3: ActivityShakeToWinStep3Binding
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
        bindingStep1 = ActivityShakeToWinStep1Binding.inflate(layoutInflater)
        bindingStep2 = ActivityShakeToWinStep2Binding.inflate(layoutInflater)
        bindingStep3 = ActivityShakeToWinStep3Binding.inflate(layoutInflater)
        val view: View = bindingStep1.root
        setContentView(view)
        cacheResources()

        //mShakeToWinMessage = getShakeToWinMessage();
        setupStep1View()
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
        super.onDestroy()
    }

    private fun setupStep1View() {
        //TODO : replace this dummy data with the real one later
        //TODO : check and set the visibilities. Only the button is mandatory
        setupCloseButtonStep1()
        bindingStep1.container.setBackgroundColor(Color.parseColor("#ff99de"))
        Picasso.get().load("https://imgvisilabsnet.azureedge.net/in-app-message/uploaded_images/163_1100_490_20210319175823217.jpg")
                .into(bindingStep1.imageView)
        bindingStep1.titleView.text = "Title"
        bindingStep1.titleView.setTextColor(Color.parseColor("#92008c"))
        bindingStep1.titleView.textSize = 32f
        bindingStep1.bodyTextView.text = "Text"
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
        bindingStep3.titleView.text = "Title"
        bindingStep3.titleView.setTextColor(Color.parseColor("#92008c"))
        bindingStep3.titleView.textSize = 32f
        bindingStep3.bodyTextView.text = "Text"
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

    companion object {
        private const val LOG_TAG = "ShakeToWinActivity"
    }
}