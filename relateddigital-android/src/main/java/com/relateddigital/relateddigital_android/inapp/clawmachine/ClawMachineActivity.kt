package com.relateddigital.relateddigital_android.inapp.clawmachine

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
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
import com.relateddigital.relateddigital_android.model.ClawMachine
import com.relateddigital.relateddigital_android.model.ClawMachineExtendedProps
import com.relateddigital.relateddigital_android.util.ActivityUtils
import com.relateddigital.relateddigital_android.util.AppUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import java.util.HashMap

class ClawMachineActivity : FragmentActivity(), ClawMachineCompleteInterface,
    ClawMachineCopyToClipboardInterface, ClawMachineShowCodeInterface {
    private var jsonStr: String? = ""
    private var response: ClawMachine? = null
    private var clawmachinePromotionCode = ""
    private var link = ""
    private lateinit var activity: FragmentActivity
    private lateinit var completeListener: ClawMachineCompleteInterface
    private lateinit var copyToClipboardListener: ClawMachineCopyToClipboardInterface
    private lateinit var showCodeListener: ClawMachineShowCodeInterface
    private var clawmachineJsStr = ""

    companion object {
        private const val LOG_TAG = "ClawMachine"
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        completeListener = this
        copyToClipboardListener = this
        showCodeListener = this

        if (!isAndroidTV(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        val jsApi = JSApiClient.getClient(RelatedDigital.getRelatedDigitalModel(this).getRequestTimeoutInSecond())
            ?.create(ApiMethods::class.java)
        val headers = HashMap<String, String>()
        headers[Constants.USER_AGENT_REQUEST_KEY] = RelatedDigital.getRelatedDigitalModel(this).getUserAgent()
        val call: Call<ResponseBody> = jsApi?.getClawMachineJsFile(headers)!!
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                responseJs: Response<ResponseBody?>
            ) {
                if (responseJs.isSuccessful) {
                    Log.i(LOG_TAG, "Getting clawmachine.js is successful!")
                    clawmachineJsStr = responseJs.body()!!.string()

                    if(clawmachineJsStr.isEmpty()) {
                        Log.e(LOG_TAG, "Getting clawmachine.js failed!")
                        finish()
                    } else {
                        if (savedInstanceState != null) {
                            jsonStr = savedInstanceState.getString("clawmachine-json-str", "")
                        } else {
                            val intent = intent
                            if (intent != null && intent.hasExtra("claw-machine-data")) {
                                response = intent.getSerializableExtra("claw-machine-data") as ClawMachine?
                                if (response != null) {
                                    jsonStr = Gson().toJson(response)
                                } else {
                                    Log.e(LOG_TAG, "Could not get the clawmachine data properly!")
                                    finish()
                                }
                            } else {
                                Log.e(LOG_TAG, "Could not get the clawmachine data properly!")
                                finish()
                            }
                        }

                        if (jsonStr != null && jsonStr != "") {
                            val res = AppUtils.createClawMachineCustomFontFiles(
                                activity, jsonStr, clawmachineJsStr
                            )
                            if(res == null) {
                                Log.e(LOG_TAG, "Could not get the clawmachine data properly!")
                                finish()
                            } else {
                                val webViewDialogFragment: ClawMachineWebDialogFragment =
                                    ClawMachineWebDialogFragment.newInstance(res[0], res[1], res[2])
                                webViewDialogFragment.setClawMachineListeners(completeListener, copyToClipboardListener, showCodeListener)
                                if (!isFinishing && !supportFragmentManager.isDestroyed) {
                                    webViewDialogFragment.display(supportFragmentManager)
                                } else {
                                    Log.e(LOG_TAG, "Activity is finishing or FragmentManager is destroyed!")
                                    finish()
                                }
                            }
                        } else {
                            Log.e(LOG_TAG, "Could not get the clawmachine data properly!")
                            finish()
                        }
                    }
                } else {
                    Log.e(LOG_TAG, "Getting clawmachine.js failed!")
                    finish()
                }
            }
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e(LOG_TAG, "Getting clawmachine.js failed! - ${t.message}")
                finish()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("clawmachine-json-str", jsonStr)
        super.onSaveInstanceState(outState)
    }

    private fun isAndroidTV(context: Context): Boolean {
        return context.packageManager.hasSystemFeature("android.software.leanback")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (clawmachinePromotionCode.isNotEmpty()) {
            try {
                val extendedProps = Gson().fromJson(
                    URI(response!!.actiondata!!.ExtendedProps).path,
                    ClawMachineExtendedProps::class.java
                )

                if (!extendedProps.promocodeBannerButtonLabel.isNullOrEmpty()) {
                    if(ActivityUtils.parentActivity != null) {
                        val clawmachineCodeBannerFragment =
                            ClawMachineCodeBannerFragment.newInstance(extendedProps, clawmachinePromotionCode)

                        val transaction: FragmentTransaction =
                            (ActivityUtils.parentActivity as FragmentActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(android.R.id.content, clawmachineCodeBannerFragment)
                        transaction.commit()
                        ActivityUtils.parentActivity = null
                    }
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "ClawMachineCodeBanner : " + e.message)
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

    override fun copyToClipboard(couponCode: String?) {
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
        finish()
    }

    override fun onCodeShown(code: String) {
        clawmachinePromotionCode = code
    }
}