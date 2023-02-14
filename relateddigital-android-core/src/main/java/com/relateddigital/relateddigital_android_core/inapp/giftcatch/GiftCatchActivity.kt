package com.relateddigital.relateddigital_android_core.inapp.giftcatch

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import com.relateddigital.relateddigital_android_core.R
import com.relateddigital.relateddigital_android_core.RelatedDigital
import com.relateddigital.relateddigital_android_core.api.ApiMethods
import com.relateddigital.relateddigital_android_core.api.JSApiClient
import com.relateddigital.relateddigital_android_core.constants.Constants
import com.relateddigital.relateddigital_android_core.model.GiftCatchExtendedProps
import com.relateddigital.relateddigital_android_core.model.GiftRain
import com.relateddigital.relateddigital_android_core.util.ActivityUtils
import com.relateddigital.relateddigital_android_core.util.AppUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import java.util.HashMap

class GiftCatchActivity : FragmentActivity(), GiftCatchCompleteInterface,
    GiftCatchCopyToClipboardInterface, GiftCatchShowCodeInterface {
    private var jsonStr: String? = ""
    private var response: GiftRain? = null
    private var giftCatchPromotionCode = ""
    private var link = ""
    private lateinit var activity: FragmentActivity
    private lateinit var completeListener: GiftCatchCompleteInterface
    private lateinit var copyToClipboardListener: GiftCatchCopyToClipboardInterface
    private lateinit var showCodeListener: GiftCatchShowCodeInterface
    private var giftCatchJsStr = ""

    companion object {
        private const val LOG_TAG = "GiftCatch"
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        completeListener = this
        copyToClipboardListener = this
        showCodeListener = this
        val jsApi = JSApiClient.getClient(RelatedDigital.getRelatedDigitalModel(this).getRequestTimeoutInSecond())
            ?.create(ApiMethods::class.java)
        val headers = HashMap<String, String>()
        headers[Constants.USER_AGENT_REQUEST_KEY] = RelatedDigital.getRelatedDigitalModel(this).getUserAgent()
        val call: Call<ResponseBody> = jsApi?.getGiftCatchJsFile(headers)!!
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                responseJs: Response<ResponseBody?>
            ) {
                if (responseJs.isSuccessful) {
                    Log.i(LOG_TAG, "Getting giftcatch.js is successful!")
                    giftCatchJsStr = responseJs.body()!!.string()

                    if (giftCatchJsStr.isEmpty()) {
                        Log.e(LOG_TAG, "Getting giftcatch.js failed!")
                        finish()
                    } else {
                        if (savedInstanceState != null) {
                            jsonStr = savedInstanceState.getString("gift-rain-json-str", "")
                        } else {
                            val intent = intent
                            if (intent != null && intent.hasExtra("gift-rain-data")) {
                                response = intent.getSerializableExtra("gift-rain-data") as GiftRain?
                                if (response != null) {
                                    jsonStr = Gson().toJson(response)
                                } else {
                                    Log.e(LOG_TAG, "Could not get the gift-rain data properly!")
                                    finish()
                                }
                            } else {
                                Log.e(LOG_TAG, "Could not get the gift-rain data properly!")
                                finish()
                            }
                        }

                        if (jsonStr != null && jsonStr != "") {
                            val res = AppUtils.createGiftRainCustomFontFiles(
                                activity, jsonStr, giftCatchJsStr
                            )
                            if(res == null) {
                                Log.e(LOG_TAG, "Could not get the gift-rain data properly!")
                                finish()
                            } else {
                                val webViewDialogFragment: GiftCatchWebDialogFragment =
                                    GiftCatchWebDialogFragment.newInstance(res[0], res[1], res[2])
                                webViewDialogFragment.setGiftCatchListeners(completeListener, copyToClipboardListener, showCodeListener)
                                webViewDialogFragment.display(supportFragmentManager)
                            }
                        } else {
                            Log.e(LOG_TAG, "Could not get the gift-rain data properly!")
                            finish()
                        }
                    }
                } else {
                    Log.e(LOG_TAG, "Getting giftcatch.js failed!")
                    finish()
                }
            }
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e(LOG_TAG, "Getting giftcatch.js failed! - ${t.message}")
                finish()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("gift-rain-json-str", jsonStr)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (giftCatchPromotionCode.isNotEmpty()) {
            try {
                val extendedProps = Gson().fromJson(
                    URI(response!!.actiondata!!.extendedProps).path,
                    GiftCatchExtendedProps::class.java
                )

                if (!extendedProps.promocodeBannerButtonLabel.isNullOrEmpty()) {
                    if(ActivityUtils.parentActivity != null) {
                        val giftCatchCodeBannerFragment =
                            GiftCatchCodeBannerFragment.newInstance(extendedProps, giftCatchPromotionCode)

                        val transaction: FragmentTransaction =
                            (ActivityUtils.parentActivity as FragmentActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(android.R.id.content, giftCatchCodeBannerFragment)
                        transaction.commit()
                        ActivityUtils.parentActivity = null
                    }
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "GiftCatchCodeBanner : " + e.message)
            }
        }

        if (link.isNotEmpty()) {
            val uri: Uri
            try {
                uri = Uri.parse(link)
                val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(viewIntent)
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Can't parse notification URI, will not take any action", e)
            }
        }
    }

    override fun onCompleted() {
        finish()
    }

    override fun copyToClipboard(couponCode: String?, link: String?) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Coupon Code", couponCode)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(applicationContext, getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show()
        if(!link.isNullOrEmpty()) {
            this.link = link
        }
        finish()
    }

    override fun onCodeShown(code: String) {
        giftCatchPromotionCode = code
    }
}