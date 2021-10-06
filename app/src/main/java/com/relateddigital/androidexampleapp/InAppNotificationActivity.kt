package com.relateddigital.androidexampleapp

import android.R
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.relateddigital.androidexampleapp.databinding.ActivityInAppNotificationExampleBinding
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.inapp.CountdownTimerFragment
import com.relateddigital.relateddigital_android.inapp.SocialProofFragment
import java.util.*

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
            val socialProofFragment: SocialProofFragment = SocialProofFragment.newInstance(0, null)

            socialProofFragment.retainInstance = true

            val transaction = fragmentManager.beginTransaction()
            transaction.add(R.id.content, socialProofFragment)
            transaction.commit()
            //TODO when backend side gets ready, check below
            //sendInAppRequest("socialproof");
        }

        binding.countdownTimerButton.setOnClickListener {
            val countdownTimerFragment: CountdownTimerFragment = CountdownTimerFragment.newInstance(0, null)

            countdownTimerFragment.retainInstance = true

            val transaction = fragmentManager.beginTransaction()
            transaction.add(R.id.content, countdownTimerFragment)
            transaction.commit()
            //TODO when backend side gets ready, check below
            //sendInAppRequest("countdowntimer");
        }

        binding.inAppCarouselButton.setOnClickListener {
            //TODO: change this to "carousel" when BE gets ready
            sendInAppRequest("image_button  ")
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