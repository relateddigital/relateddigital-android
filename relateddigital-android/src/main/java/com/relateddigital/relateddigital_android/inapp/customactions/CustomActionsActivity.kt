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
import com.relateddigital.relateddigital_android.inapp.shaketowin.ShakeToWinActivity
import com.relateddigital.relateddigital_android.model.CustomActions
import com.relateddigital.relateddigital_android.model.CustomActionsExtendedProps
import com.relateddigital.relateddigital_android.model.ShakeToWin
import com.relateddigital.relateddigital_android.util.ActivityUtils
import com.relateddigital.relateddigital_android.util.AppUtils
import java.net.URI
import java.util.HashMap

class CustomActionsActivity : FragmentActivity(), CustomActionsCompleteInterface, CustomActionsCopyToClipboardInterface, CustomActionsShowCodeInterface{
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
        activity = this
        completeListener = this
        copyToClipboardListener = this
        showCodeListener = this

     customActions


}

    private val customActions: Unit
        get() {
            val intent = intent
            if (intent != null) {
                if (intent.hasExtra("custom-actions-data")) {
                    mCustomActions =
                        intent.getSerializableExtra("custom-actions-data") as CustomActions?
                }
            }
            if (mCustomActions == null) {
                Log.e(LOG_TAG, "Could not get the content from the server!")
                finish()
            }
        }

    override fun onCompleted() {
        TODO("Not yet implemented")
    }

    override fun copyToClipboard(couponCode: String?, link: String?) {
        TODO("Not yet implemented")
    }

    override fun onCodeShown(code: String) {
        TODO("Not yet implemented")
    }

}
