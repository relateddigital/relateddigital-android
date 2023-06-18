package com.relateddigital.relateddigital_android.inapp.choosefavorited

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.api.ApiMethods
import com.relateddigital.relateddigital_android.api.JSApiClient
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.ChooseFavorited
import com.relateddigital.relateddigital_android.util.AppUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ChooseFavoritedActivity : FragmentActivity(), ChooseFavoritedCompleteInterface,
    ChooseFavoritedCopyToClipboardInterface, ChooseFavoritedShowCodeInterface {
    private var jsonStr: String? = ""
    private var response: ChooseFavorited? = null
    private var chooseFavoritedPromotionCode = ""
    private var link = ""
    private lateinit var activity: FragmentActivity
    private lateinit var completeListener: ChooseFavoritedCompleteInterface
    private lateinit var copyToClipboardListener: ChooseFavoritedCopyToClipboardInterface
    private lateinit var showCodeListener: ChooseFavoritedShowCodeInterface
    private var chooseFavoritedJsStr = ""

    companion object {
        private const val LOG_TAG = "ChooseFavorited"
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
        val call: Call<ResponseBody> = jsApi?.getChooseFavoritedJsFile(headers)!!
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                responseJs: Response<ResponseBody?>
            ) {
                if (responseJs.isSuccessful) {
                    Log.i(ChooseFavoritedActivity.LOG_TAG, "Getting chooseFavorited.js is successful!")
                    chooseFavoritedJsStr = responseJs.body()!!.string()

                    if(chooseFavoritedJsStr.isEmpty()) {
                        Log.e(ChooseFavoritedActivity.LOG_TAG, "Getting chooseFavorited.js failed!")
                        finish()
                    } else {
                        if (savedInstanceState != null) {
                            jsonStr = savedInstanceState.getString("chooseFavorited-json-str", "")
                        } else {
                            val intent = intent
                            if (intent != null && intent.hasExtra("gift-box-data")) {
                                response = intent.getSerializableExtra("gift-box-data") as ChooseFavorited?
                                if (response != null) {
                                    jsonStr = Gson().toJson(response)
                                } else {
                                    Log.e(ChooseFavoritedActivity.LOG_TAG, "Could not get the chooseFavorited data properly!")
                                    finish()
                                }
                            } else {
                                Log.e(ChooseFavoritedActivity.LOG_TAG, "Could not get the chooseFavorited data properly!")
                                finish()
                            }
                        }

                        if (jsonStr != null && jsonStr != "") {
                            val res = AppUtils.createChooseFavoritedCustomFontFiles(
                                activity, jsonStr, chooseFavoritedJsStr
                            )
                            if(res == null) {
                                Log.e(ChooseFavoritedActivity.LOG_TAG, "Could not get the chooseFavorited data properly!")
                                finish()
                            } else {
                                val webViewDialogFragment: ChooseFavoritedWebDialogFragment =
                                    ChooseFavoritedWebDialogFragment.newInstance(res[0], res[1], res[2])
                                webViewDialogFragment.setChooseFavoritedListeners(completeListener, copyToClipboardListener, showCodeListener)
                                if (!isFinishing && !supportFragmentManager.isDestroyed) {
                                    webViewDialogFragment.display(supportFragmentManager)
                                } else {
                                    Log.e(ChooseFavoritedActivity.LOG_TAG, "Activity is finishing or FragmentManager is destroyed!")
                                    finish()
                                }
                            }
                        } else {
                            Log.e(ChooseFavoritedActivity.LOG_TAG, "Could not get the chooseFavorited data properly!")
                            finish()
                        }
                    }
                } else {
                    Log.e(ChooseFavoritedActivity.LOG_TAG, "Getting chooseFavorited.js failed!")
                    finish()
                }
            }
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e(ChooseFavoritedActivity.LOG_TAG, "Getting chooseFavorited.js failed! - ${t.message}")
                finish()
            }
        })
    }

    override fun onCompleted() {
        finish()
    }

    override fun copyToClipboard(couponCode: String?, link: String?) {
        if(!couponCode.isNullOrEmpty()) {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Coupon Code", couponCode)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
                applicationContext,
                getString(R.string.copied_to_clipboard),
                Toast.LENGTH_LONG
            ).show()
        }
        if(!link.isNullOrEmpty()) {
            this.link = link
        }
        finish()
    }

    override fun onCodeShown(code: String) {
        chooseFavoritedPromotionCode = code
    }
}