package com.relateddigital.androidexampleapp

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.relateddigital.androidexampleapp.databinding.ActivityPushNotificationBinding
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.model.EmailPermit
import com.relateddigital.relateddigital_android.model.GsmPermit
import com.relateddigital.relateddigital_android.model.Message
import com.relateddigital.relateddigital_android.push.EuromessageCallback
import com.relateddigital.relateddigital_android.push.PushMessageInterface
import com.relateddigital.relateddigital_android.util.GoogleUtils

class PushNotificationActivity : AppCompatActivity() {
    companion object{
        private const val LOG_TAG = "PushNotificationActivity"
    }
    private lateinit var binding: ActivityPushNotificationBinding
    private lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPushNotificationBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        activity = this
        setupUI()
    }

    private fun setupUI() {
        showToken()
        createSpinners()
        setupPayloadButton()
        setupSyncButton()
        setupRegisterEmailButton()
    }

    private fun showToken() {
        if(GoogleUtils.checkPlayService(this)) {
            binding.etToken.setText(RelatedDigital.getRelatedDigitalModel(this).getToken())
        } else {
            binding.etHuaweiToken.setText(RelatedDigital.getRelatedDigitalModel(this).getToken())
        }
    }

    private fun createSpinners() {
        val registerEmailCommercialSpinnerItems = arrayOf<String>(
            Constants.EURO_RECIPIENT_TYPE_BIREYSEL,
            Constants.EURO_RECIPIENT_TYPE_TACIR
        )
        val aa1: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            registerEmailCommercialSpinnerItems
        )
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.registeremailcommercialSpinner.adapter = aa1
        val registerEmailPermitSpinnerItems =
            arrayOf<String?>(Constants.EMAIL_PERMIT_ACTIVE, Constants.EMAIL_PERMIT_PASSIVE)
        val aa2: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, registerEmailPermitSpinnerItems)
        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.registeremailpermitSpinner.adapter = aa2
    }

    private fun setupPayloadButton() {
        binding.btnPayload.setOnClickListener {
            val pushMessageInterface: PushMessageInterface = object : PushMessageInterface {
                override fun success(pushMessages: List<Message>) {
                    Toast.makeText(
                        applicationContext,
                        "Successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Make your implementation by using pushMessages here:
                }

                override fun fail(errorMessage: String) {
                    Toast.makeText(
                        applicationContext,
                        "Failure",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Something went wrong. You may consider warning the user:
                }
            }
            RelatedDigital.getPushMessages(activity, pushMessageInterface)
        }
    }

    private fun setupSyncButton() {
        binding.btnSync.setOnClickListener {
            if(binding.autotext.text.toString().isNotEmpty()) {
                RelatedDigital.setEmailPermit(this, EmailPermit.ACTIVE)
                RelatedDigital.setGsmPermit(this, GsmPermit.ACTIVE)
                RelatedDigital.setTwitterId(this, "testTwitterId")
                RelatedDigital.setEmail(this, binding.autotext.text.toString())
                RelatedDigital.setFacebookId(this, "testFacebookId")
                RelatedDigital.setRelatedDigitalUserId(this, "testRelatedDigitalUserId")
                RelatedDigital.setPhoneNumber(this, "testPhoneNumber")
                RelatedDigital.setUserProperty(this, "instagram", "testInstagramId")

                val callback: EuromessageCallback = object : EuromessageCallback {
                    override fun success() {
                        Toast.makeText(applicationContext, "Successful", Toast.LENGTH_LONG).show()
                    }

                    override fun fail(errorMessage: String?) {
                        Toast.makeText(applicationContext, "Failure", Toast.LENGTH_LONG).show()
                    }

                }
                RelatedDigital.sync(this, callback)
            } else {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupRegisterEmailButton() {
        binding.btnRegisteremail.setOnClickListener {
            if(binding.registeremailAutotext.text.toString().isNotEmpty()) {
                var isCommercial = false
                val isCommercialText =
                    java.lang.String.valueOf(binding.registeremailcommercialSpinner.selectedItem)
                if (isCommercialText == Constants.EURO_RECIPIENT_TYPE_TACIR) {
                    isCommercial = true
                }

                var emailPermit = EmailPermit.ACTIVE
                val isEmailPermitActiveText =
                    java.lang.String.valueOf(binding.registeremailpermitSpinner.selectedItem)
                if (isEmailPermitActiveText == Constants.EMAIL_PERMIT_PASSIVE) {
                    emailPermit = EmailPermit.PASSIVE
                }

                RelatedDigital.setGsmPermit(this, GsmPermit.ACTIVE)
                RelatedDigital.setTwitterId(this, "testTwitterId")
                RelatedDigital.setFacebookId(this, "testFacebookId")
                RelatedDigital.setRelatedDigitalUserId(this, "testRelatedDigitalUserId")
                RelatedDigital.setPhoneNumber(this, "testPhoneNumber")
                RelatedDigital.setUserProperty(this, "instagram", "testInstagramId")

                val callback: EuromessageCallback = object : EuromessageCallback {
                    override fun success() {
                        Toast.makeText(applicationContext, "Successful", Toast.LENGTH_LONG).show()
                    }

                    override fun fail(errorMessage: String?) {
                        Toast.makeText(applicationContext, "Failure", Toast.LENGTH_LONG).show()
                    }

                }
                RelatedDigital.registerEmail(
                    this, binding.registeremailAutotext.text.toString(),
                    emailPermit, isCommercial, callback
                )
            } else {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_LONG).show()
            }
        }
    }
}