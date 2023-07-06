package com.relateddigital.relateddigital_android.inapp.choosefavorite

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
import com.relateddigital.relateddigital_android.model.ChooseFavorite
import com.relateddigital.relateddigital_android.util.AppUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ChooseFavoriteActivity : FragmentActivity(), ChooseFavoriteCompleteInterface,
    ChooseFavoriteCopyToClipboardInterface, ChooseFavoriteShowCodeInterface {
    private var jsonStr: String? = ""
    private var response: ChooseFavorite? = null
    private var chooseFavoritePromotionCode = ""
    private var link = ""
    private lateinit var activity: FragmentActivity
    private lateinit var completeListener: ChooseFavoriteCompleteInterface
    private lateinit var copyToClipboardListener: ChooseFavoriteCopyToClipboardInterface
    private lateinit var showCodeListener: ChooseFavoriteShowCodeInterface
    private var chooseFavoriteJsStr = ""

    companion object {
        private const val LOG_TAG = "ChooseFavorite"
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
        val call: Call<ResponseBody> = jsApi?.getSwipingJsFile(headers)!!
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                responseJs: Response<ResponseBody?>
            ) {
                if (responseJs.isSuccessful) {
                    Log.i(ChooseFavoriteActivity.LOG_TAG, "Getting chooseFavorite.js is successful!")
                    chooseFavoriteJsStr = responseJs.body()!!.string()

                    if(chooseFavoriteJsStr.isEmpty()) {
                        Log.e(ChooseFavoriteActivity.LOG_TAG, "Getting chooseFavorite.js failed!")
                        finish()
                    } else {
                        if (savedInstanceState != null) {
                            jsonStr = savedInstanceState.getString("chooseFavorite-json-str", "")
                        } else {
                            val intent = intent
                            if (intent != null && intent.hasExtra("choose-favorite-data")) {
                                response = intent.getSerializableExtra("choose-favorite-data") as ChooseFavorite?
                                if (response != null) {
                                    jsonStr = Gson().toJson(response)
                                } else {
                                    Log.e(ChooseFavoriteActivity.LOG_TAG, "Could not get the chooseFavorite data properly!")
                                    finish()
                                }
                            } else {
                                Log.e(ChooseFavoriteActivity.LOG_TAG, "Could not get the chooseFavorite data properly!")
                                finish()
                            }
                        }

                        if (jsonStr != null && jsonStr != "") {
                            val res = AppUtils.createChooseFavoriteCustomFontFiles(
                                activity, jsonStr, chooseFavoriteJsStr
                            )
                            if(res == null) {
                                Log.e(ChooseFavoriteActivity.LOG_TAG, "Could not get the chooseFavorite data properly!")
                                finish()
                            } else {
                                val webViewDialogFragment: ChooseFavoriteWebDialogFragment =
                                    ChooseFavoriteWebDialogFragment.newInstance(res[0], res[1], res[2])
                                webViewDialogFragment.setChooseFavoriteListeners(completeListener, copyToClipboardListener, showCodeListener)
                                if (!isFinishing && !supportFragmentManager.isDestroyed) {
                                    webViewDialogFragment.display(supportFragmentManager)
                                } else {
                                    Log.e(ChooseFavoriteActivity.LOG_TAG, "Activity is finishing or FragmentManager is destroyed!")
                                    finish()
                                }
                            }
                        } else {
                            Log.e(ChooseFavoriteActivity.LOG_TAG, "Could not get the chooseFavorite data properly!")
                            finish()
                        }
                    }
                } else {
                    Log.e(ChooseFavoriteActivity.LOG_TAG, "Getting chooseFavorite.js failed!")
                    finish()
                }
            }
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e(ChooseFavoriteActivity.LOG_TAG, "Getting chooseFavorite.js failed! - ${t.message}")
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
        chooseFavoritePromotionCode = code
    }
}