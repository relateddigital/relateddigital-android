package com.relateddigital.relateddigital_android.inapp.spintowin

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
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.api.ApiMethods
import com.relateddigital.relateddigital_android.api.JSApiClient
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.SpinToWin
import com.relateddigital.relateddigital_android.model.SpinToWinExtendedProps
import com.relateddigital.relateddigital_android.util.ActivityUtils
import com.relateddigital.relateddigital_android.util.AppUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import java.util.HashMap


class SpinToWinActivity : FragmentActivity(), SpinToWinCompleteInterface,
    SpinToWinCopyToClipboardInterface, SpinToWinShowCodeInterface {
    private var jsonStr: String? = ""
    private var response: SpinToWin? = null
    private var spinToWinPromotionCode = ""
    private var sliceLink = ""
    private lateinit var activity: FragmentActivity
    private lateinit var completeListener: SpinToWinCompleteInterface
    private lateinit var copyToClipboardListener: SpinToWinCopyToClipboardInterface
    private lateinit var showCodeListener: SpinToWinShowCodeInterface
    private var spinToWinJsStr = ""


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
        val call: Call<ResponseBody> = jsApi?.getSpinToWinJsFile(headers)!!
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, responseJs: Response<ResponseBody?>) {
                if (responseJs.isSuccessful) {
                    Log.i(LOG_TAG, "Getting spintowin.js is successful!")
                    spinToWinJsStr = responseJs.body()!!.string()

                    if(spinToWinJsStr.isEmpty()) {
                        Log.e(LOG_TAG, "Getting spintowin.js failed!")
                        finish()
                    } else {
                        if (savedInstanceState != null) {
                            jsonStr = savedInstanceState.getString("spin-to-win-json-str", "")
                        } else {
                            val intent = intent
                            if (intent != null && intent.hasExtra("spin-to-win-data")) {
                                response =
                                    intent.getSerializableExtra("spin-to-win-data") as SpinToWin?
                                if (response != null) {
                                    jsonStr = Gson().toJson(response)
                                } else {
                                    Log.e(LOG_TAG, "Could not get the spin-to-win data properly!")
                                    finish()
                                }
                            } else {
                                Log.e(LOG_TAG, "Could not get the spin-to-win data properly!")
                                finish()
                            }
                        }
                        if (jsonStr != null && jsonStr != "") {
                            val res = AppUtils.createSpinToWinCustomFontFiles(
                                activity, jsonStr, spinToWinJsStr
                            )
                            if (res == null) {
                                Log.e(LOG_TAG, "Could not get the spin-to-win data properly!")
                                finish()
                            } else {
                                val webViewDialogFragment: SpinToWinWebDialogFragment =
                                    SpinToWinWebDialogFragment.newInstance(res[0], res[1], res[2])
                                webViewDialogFragment.setSpinToWinListeners(
                                    completeListener,
                                    copyToClipboardListener,
                                    showCodeListener
                                )
                                if (!isFinishing && !supportFragmentManager.isDestroyed) {
                                    webViewDialogFragment.display(supportFragmentManager)
                                } else {
                                    Log.e(LOG_TAG, "Activity is finishing or FragmentManager is destroyed!")
                                    finish()
                                }
                            }
                        } else {
                            Log.e(LOG_TAG, "Could not get the spin-to-win data properly!")
                            finish()
                        }
                    }
                } else {
                    Log.e(LOG_TAG, "Getting spintowin.js failed!")
                    finish()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e(LOG_TAG, "Getting spintowin.js failed! - ${t.message}")
                finish()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("spin-to-win-json-str", jsonStr)
        super.onSaveInstanceState(outState)
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
            sliceLink = link
        }
        finish()
    }

    companion object {
        private const val LOG_TAG = "SpinToWin"
    }

    override fun onDestroy() {
        super.onDestroy()
        if (spinToWinPromotionCode.isNotEmpty()) {
            try {
                val extendedProps = Gson().fromJson(URI(response!!.actiondata!!.extendedProps).path,
                    SpinToWinExtendedProps::class.java
                )

                if (!extendedProps.promocode_banner_button_label.isNullOrEmpty()) {
                    if(ActivityUtils.parentActivity != null) {
                        val spinToWinCodeBannerFragment =
                            SpinToWinCodeBannerFragment.newInstance(extendedProps, spinToWinPromotionCode)

                        val transaction: FragmentTransaction =
                            (ActivityUtils.parentActivity as FragmentActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(android.R.id.content, spinToWinCodeBannerFragment)
                        transaction.commit()
                        ActivityUtils.parentActivity = null
                    }
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "SpinToWinCodeBanner : " + e.message)
            }
        }
        if (sliceLink.isNotEmpty()) {
            val uri: Uri
            try {
                uri = Uri.parse(sliceLink)
                val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(viewIntent)
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Can't parse notification URI, will not take any action", e)
            }
        }
    }

    override fun onCodeShown(code: String) {
        spinToWinPromotionCode = code
    }
}