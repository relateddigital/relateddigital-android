package com.relateddigital.relateddigital_android.inapp.customactions

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
import com.relateddigital.relateddigital_android.model.CustomActions
import com.relateddigital.relateddigital_android.model.CustomActionsExtendedProps
import com.relateddigital.relateddigital_android.util.ActivityUtils
import com.relateddigital.relateddigital_android.util.AppUtils
import java.net.URI
import java.util.HashMap

class CustomActionsActivity : FragmentActivity(){
    private var jsonStr: String? = ""
    private var mCustomActions: CustomActions? = null
    private lateinit var activity: FragmentActivity
    private var customActionsJsStr = ""
    private var customActionsPromotionCode = ""
    private var link = ""
    private lateinit var completeListener: CustomActionsCompleteInterface
    private lateinit var copyToClipboardListener: CustomActionsCopyToClipboardInterface
    private lateinit var showCodeListener: CustomActionsShowCodeInterface
    companion object {
        private const val LOG_TAG = "CustomActions"
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jsApi = JSApiClient.getClient(RelatedDigital.getRelatedDigitalModel(this).getRequestTimeoutInSecond())
            ?.create(ApiMethods::class.java)
        val headers = HashMap<String, String>()
        headers[Constants.USER_AGENT_REQUEST_KEY] = RelatedDigital.getRelatedDigitalModel(this).getUserAgent()

      //  jsonStr = savedInstanceState.getString("CustomActions-json-str", "")

        if (jsonStr != null && jsonStr != "") {
            val res = AppUtils.createCustomActionsCustomFontFiles(
                activity, jsonStr, customActionsJsStr
            )
            if(res == null) {
                Log.e(CustomActionsActivity.LOG_TAG, "Could not get the customActions data properly!")
                finish()
            } else {
                val webViewDialogFragment: CustomActionsWebDialogFragment =
                    CustomActionsWebDialogFragment.newInstance(res[0], res[1], res[2])
                webViewDialogFragment.setCustomActionsListeners(completeListener, copyToClipboardListener, showCodeListener)
                if (!isFinishing && !supportFragmentManager.isDestroyed) {
                    webViewDialogFragment.display(supportFragmentManager)
                } else {
                    Log.e(CustomActionsActivity.LOG_TAG, "Activity is finishing or FragmentManager is destroyed!")
                    finish()
                }
            }
        } else {
            Log.e(CustomActionsActivity.LOG_TAG, "Could not get the customActions data properly!")
            finish()
        }
    }

  /*  override fun onDestroy() {
        super.onDestroy()
        if (customActionsPromotionCode.isNotEmpty()) {
            try {
                val extendedProps = Gson().fromJson(
                    URI(response!!.actiondata!!.ExtendedProps).path,
                    CustomActionsExtendedProps::class.java
                )

                if (!extendedProps.promocodeBannerButtonLabel.isNullOrEmpty()) {
                    if(ActivityUtils.parentActivity != null) {
                        val customActionsCodeBannerFragment =
                            CustomActionsCodeBannerFragment.newInstance(extendedProps, customActionsPromotionCode)

                        val transaction: FragmentTransaction =
                            (ActivityUtils.parentActivity as FragmentActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(android.R.id.content, customActionsCodeBannerFragment)
                        transaction.commit()
                        ActivityUtils.parentActivity = null
                    }
                }
            } catch (e: Exception) {
                Log.e(CustomActionsActivity.LOG_TAG, "CustomActionsCodeBanner : " + e.message)
            }
        }

        if (link.isNotEmpty()) {
            val uri: Uri
            try {
                uri = Uri.parse(link)
                val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(viewIntent)
            } catch (e: Exception) {
                Log.w(CustomActionsActivity.LOG_TAG, "Can't parse notification URI, will not take any action", e)
            }
        }
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
        customActionsPromotionCode = code
    }*/
}
