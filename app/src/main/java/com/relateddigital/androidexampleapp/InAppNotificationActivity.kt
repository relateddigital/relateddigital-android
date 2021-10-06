package com.relateddigital.androidexampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.relateddigital.androidexampleapp.databinding.ActivityInAppNotificationExampleBinding
import com.relateddigital.relateddigital_android.RelatedDigital
import java.util.HashMap

class InAppNotificationActivity : AppCompatActivity() {
    companion object{
        private const val LOG_TAG = "InAppNotificationActivity"
    }
    private lateinit var binding: ActivityInAppNotificationExampleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInAppNotificationExampleBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        setupUi()
    }

    private fun setupUi() {
        binding.fullButton.setOnClickListener {
            sendInAppRequest("full")
        }

        binding.miniButton.setOnClickListener {
            sendInAppRequest("mini")
        }

        binding.fullImageButton.setOnClickListener {
            sendInAppRequest("full_image")
        }

        binding.imageButtonButton.setOnClickListener {
            sendInAppRequest("image_button")
        }

        binding.imageTextButtonButton.setOnClickListener {
            sendInAppRequest("image_text_button")
        }

        binding.smileRatingButton.setOnClickListener {
            sendInAppRequest("smile_rating")
        }

        binding.npsWithNumbersButton.setOnClickListener {
            sendInAppRequest("nps_with_numbers")
        }

        binding.npsButton.setOnClickListener {
            sendInAppRequest("nps")
        }

        binding.nativeAlertButton.setOnClickListener {
            sendInAppRequest("alert_native")
        }

        binding.actionSheetButton.setOnClickListener {
            sendInAppRequest("alert_actionsheet")
        }

        binding.mailSubsFormButton.setOnClickListener {
            sendInAppRequest("mailsubsform")
        }

        binding.scratchToWinButton.setOnClickListener {
            sendInAppRequest("scratch-to-win")
        }

        binding.spinToWinButton.setOnClickListener {
            sendInAppRequest("spintowin")
        }

        binding.socialProofButton.setOnClickListener {
            sendInAppRequest("social-proof")
        }

        binding.countdownTimerButton.setOnClickListener {
            sendInAppRequest("countdown-timer")
        }

        binding.inAppCarouselButton.setOnClickListener {
            //sendInAppRequest("in-app-carousel")
        }

        binding.shakeToWinButton.setOnClickListener {
            sendInAppRequest("shake-to-win")
        }

        binding.appTrackerButton.setOnClickListener {
            RelatedDigital.sendTheListOfAppsInstalled(applicationContext);
        }

        binding.nps1Button.setOnClickListener {
            sendInAppRequest("nps-image-text-button")
        }

        binding.nps2Button.setOnClickListener {
            sendInAppRequest("nps-image-text-button-image")
        }

        binding.nps3Button.setOnClickListener {
            sendInAppRequest("nps-feedback")
        }
    }

    private fun sendInAppRequest(type: String) {
        val parameters = HashMap<String, String>()
        parameters["OM.inapptype"] = type
        RelatedDigital.customEvent(
                context = applicationContext,
                pageName = "in-app",
                properties = parameters,
                parent = this@InAppNotificationActivity
        )
    }
}