package com.relateddigital.relateddigital_android.inapp.customactions

import android.media.session.PlaybackState
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.api.ApiMethods
import com.relateddigital.relateddigital_android.api.JSApiClient
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.inapp.giftbox.GiftBoxActivity
import com.relateddigital.relateddigital_android.inapp.giftbox.GiftBoxCompleteInterface
import com.relateddigital.relateddigital_android.inapp.giftbox.GiftBoxCopyToClipboardInterface
import com.relateddigital.relateddigital_android.inapp.giftbox.GiftBoxShowCodeInterface
import com.relateddigital.relateddigital_android.inapp.giftbox.GiftBoxWebDialogFragment
import com.relateddigital.relateddigital_android.model.CustomActions
import com.relateddigital.relateddigital_android.model.ShakeToWin
import com.relateddigital.relateddigital_android.util.AppUtils
import java.util.HashMap

class CustomActionsActivity : FragmentActivity(){
    private var jsonStr: String? = ""
    private var mCustomActions: CustomActions? = null
    private lateinit var activity: FragmentActivity
    private var customActionsJsStr = ""
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

      //  jsonStr = savedInstanceState.getString("giftbox-json-str", "")

        if (jsonStr != null && jsonStr != "") {
            val res = AppUtils.createCustomActionsCustomFontFiles(
                activity, jsonStr, customActionsJsStr
            )
            if(res == null) {
                Log.e(CustomActionsActivity.LOG_TAG, "Could not get the giftbox data properly!")
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
            Log.e(CustomActionsActivity.LOG_TAG, "Could not get the giftbox data properly!")
            finish()
        }
    }
}