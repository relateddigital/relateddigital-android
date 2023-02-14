package com.relateddigital.relateddigital_android_core.inapp.findtowin

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
import com.relateddigital.relateddigital_android_core.model.FindToWin
import com.relateddigital.relateddigital_android_core.model.FindToWinExtendedProps
import com.relateddigital.relateddigital_android_core.util.ActivityUtils
import com.relateddigital.relateddigital_android_core.util.AppUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import java.util.HashMap

class FindToWinActivity : FragmentActivity(), FindToWinCompleteInterface,
    FindToWinCopyToClipboardInterface, FindToWinShowCodeInterface {
    private var jsonStr: String? = ""
    private var response: FindToWin? = null
    private var findToWinPromotionCode = ""
    private var link = ""
    private lateinit var activity: FragmentActivity
    private lateinit var completeListener: FindToWinCompleteInterface
    private lateinit var copyToClipboardListener: FindToWinCopyToClipboardInterface
    private lateinit var showCodeListener: FindToWinShowCodeInterface
    private var findToWinJsStr = ""

    companion object {
        private const val LOG_TAG = "FindToWin"
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
        val call: Call<ResponseBody> = jsApi?.getFindToWinJsFile(headers)!!
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                responseJs: Response<ResponseBody?>
            ) {
                if (responseJs.isSuccessful) {
                    Log.i(LOG_TAG, "Getting findtowin.js is successful!")
                    findToWinJsStr = responseJs.body()!!.string()

                    if(findToWinJsStr.isEmpty()) {
                        Log.e(LOG_TAG, "Getting findtowin.js failed!")
                        finish()
                    } else {
                        if (savedInstanceState != null) {
                            jsonStr = savedInstanceState.getString("find-to-win-json-str", "")
                        } else {
                            val intent = intent
                            if (intent != null && intent.hasExtra("find-to-win-data")) {
                                response = intent.getSerializableExtra("find-to-win-data") as FindToWin?
                                if (response != null) {
                                    jsonStr = Gson().toJson(response)
                                } else {
                                    Log.e(LOG_TAG, "Could not get the find-to-win data properly!")
                                    finish()
                                }
                            } else {
                                Log.e(LOG_TAG, "Could not get the find-to-win data properly!")
                                finish()
                            }
                        }

                        if (jsonStr != null && jsonStr != "") {
                            val res = AppUtils.createFindToWinCustomFontFiles(
                                activity, jsonStr, findToWinJsStr
                            )
                            if(res == null) {
                                Log.e(LOG_TAG, "Could not get the find-to-win data properly!")
                                finish()
                            } else {
                                val webViewDialogFragment: FindToWinWebDialogFragment =
                                    FindToWinWebDialogFragment.newInstance(res[0], res[1], res[2])
                                webViewDialogFragment.setFindToWinListeners(completeListener, copyToClipboardListener, showCodeListener)
                                webViewDialogFragment.display(supportFragmentManager)
                            }
                        } else {
                            Log.e(LOG_TAG, "Could not get the find-to-win data properly!")
                            finish()
                        }
                    }
                } else {
                    Log.e(LOG_TAG, "Getting findtowin.js failed!")
                    finish()
                }
            }
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e(LOG_TAG, "Getting findtowin.js failed! - ${t.message}")
                finish()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("find-to-win-json-str", jsonStr)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (findToWinPromotionCode.isNotEmpty()) {
            try {
                val extendedProps = Gson().fromJson(
                    URI(response!!.actiondata!!.extendedProps).path,
                    FindToWinExtendedProps::class.java
                )

                if (!extendedProps.promocodeBannerButtonLabel.isNullOrEmpty()) {
                    if(ActivityUtils.parentActivity != null) {
                        val findToWinCodeBannerFragment =
                            FindToWinCodeBannerFragment.newInstance(extendedProps, findToWinPromotionCode)

                        val transaction: FragmentTransaction =
                            (ActivityUtils.parentActivity as FragmentActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(android.R.id.content, findToWinCodeBannerFragment)
                        transaction.commit()
                        ActivityUtils.parentActivity = null
                    }
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "FindToWinCodeBanner : " + e.message)
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
        findToWinPromotionCode = code
    }
}