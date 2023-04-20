package com.relateddigital.androidexampleapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.relateddigital.androidexampleapp.databinding.ActivityInAppNotificationExampleBinding
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.inapp.VisilabsCallback
import com.relateddigital.relateddigital_android.inapp.VisilabsResponse
import com.relateddigital.relateddigital_android.inapp.countdowntimer.CountdownTimerFragment
import com.relateddigital.relateddigital_android.inapp.findtowin.FindToWinActivity
import com.relateddigital.relateddigital_android.inapp.giftcatch.GiftCatchActivity
import com.relateddigital.relateddigital_android.inapp.shaketowin.ShakeToWinActivity
import com.relateddigital.relateddigital_android.model.FavsResponse
import com.relateddigital.relateddigital_android.util.ActivityUtils
import java.util.*


class InAppNotificationActivity : AppCompatActivity() {
    companion object{
        private const val LOG_TAG = "InAppNotificationActivity"
    }
    private lateinit var binding: ActivityInAppNotificationExampleBinding
    private lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInAppNotificationExampleBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        activity = this

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
            val parameters: HashMap<String, String> = HashMap()
            parameters["OM.inapptype"] = "socialproof"
            parameters["OM.pv"] = "584992"
            RelatedDigital.customEvent(
                    context = applicationContext,
                    pageName = "in-app",
                    properties = parameters,
                    parent = this@InAppNotificationActivity)
        }

        binding.countdownTimerButton.setOnClickListener {
            val countdownTimerFragment: CountdownTimerFragment = CountdownTimerFragment.newInstance(0, null)

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(android.R.id.content, countdownTimerFragment)
            transaction.commit()
            //TODO when backend side gets ready, check below
            //sendInAppRequest("countdowntimer");
        }

        binding.inAppCarouselButton.setOnClickListener {
            sendInAppRequest("inappcarousel")
        }

        binding.shakeToWinButton.setOnClickListener {
            sendInAppRequest("ShakeToWin");
        }

        binding.appTrackerButton.setOnClickListener {
            RelatedDigital.sendTheListOfAppsInstalled(applicationContext)
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

        binding.halfScreenButton.setOnClickListener {
            sendInAppRequest("halfscreen")
        }

        binding.locationPermissionButton.setOnClickListener {
            RelatedDigital.sendLocationPermission(applicationContext)
        }

        binding.recommendationButton.setOnClickListener {
            val callback: VisilabsCallback = object : VisilabsCallback {
                override fun success(response: VisilabsResponse?) {
                    try {
                        Toast.makeText(applicationContext, "Got the recommendations successfully!", Toast.LENGTH_SHORT).show()
                        val jsonObject = response!!.json
                        val groupTitle = jsonObject!!.getString("title")
                        val jsonArray = jsonObject.getJSONArray("recommendations")
                        for (i in 0 until jsonArray.length()) {
                            val currentProductObject = jsonArray.getJSONObject(i)
                            val currentProductTitle = currentProductObject.getString("title")
                            val currentProductPrice = currentProductObject.getDouble("price")
                            val currentProductFreeShipping = currentProductObject.getBoolean("freeshipping")
                            val qs = currentProductObject.getString("qs")
                            //Continues like this...
                        }
                    } catch (e: Exception) {
                        Toast.makeText(applicationContext, "Could not parse the recommendations!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun fail(response: VisilabsResponse?) {
                    Toast.makeText(applicationContext, "Could not get the recommendations!", Toast.LENGTH_SHORT).show()
                }
            }

            /*RelatedDigital.getRecommendations(
                context = applicationContext,
                zoneId = zoneId,
                productCode = productCode,
                visilabsCallback = callback)*/
        }

        binding.favouriteResponseButton.setOnClickListener {
            val callback: VisilabsCallback = object : VisilabsCallback {
                override fun success(response: VisilabsResponse?) {
                    try {
                        Toast.makeText(applicationContext, "Got the favourites successfully!", Toast.LENGTH_SHORT).show()
                        val favsResponse: FavsResponse = Gson().fromJson(response!!.rawResponse, FavsResponse::class.java)

                        val favBrands: String = favsResponse.favoriteAttributeAction!![0].actiondata!!.favorites!!.brand!![0]!!
                        Log.i("Favs 1.Brand", favBrands)

                    } catch (e: Exception) {
                        Toast.makeText(applicationContext, "Could not parse the favourites!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun fail(response: VisilabsResponse?) {
                    Toast.makeText(applicationContext, "Could not get the favourites!", Toast.LENGTH_SHORT).show()
                }
            }

            RelatedDigital.getFavorites(
                context = applicationContext,
                actionId = null,
                actionType = Constants.FavoriteAttributeAction,
                visilabsCallback = callback)
        }

        binding.notification.setOnClickListener {
            sendInAppRequest("drawer")
        }

        binding.mailSubsFormHalfButton.setOnClickListener {
            sendInAppRequest("mail_subs_form_2")
        }

        binding.geofencingButton.setOnClickListener {
            RelatedDigital.requestLocationPermission(activity)
        }

        binding.giftCatchButton.setOnClickListener {
            sendInAppRequest("giftrain")
        }

        binding.giftboxButton.setOnClickListener {
            sendInAppRequest("giftBox")
        }

        binding.findToWinButton.setOnClickListener {
            sendInAppRequest("findtowin")
        }

        binding.inlineNps.setOnClickListener{
            val intent = Intent(this, InlineNpsWithNumbersActivity::class.java)
             startActivity(intent)

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