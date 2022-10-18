package com.relateddigital.relateddigital_android.inapp.findtowin

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
import com.relateddigital.relateddigital_android.model.FindToWin
import com.relateddigital.relateddigital_android.model.FindToWinExtendedProps
import com.relateddigital.relateddigital_android.util.ActivityUtils
import com.relateddigital.relateddigital_android.util.AppUtils
import java.net.URI

class FindToWinActivity : FragmentActivity(), FindToWinCompleteInterface,
    FindToWinCopyToClipboardInterface, FindToWinShowCodeInterface {
    private var jsonStr: String? = ""
    private var response: FindToWin? = null
    private var findToWinPromotionCode = ""
    private var link = ""

    companion object {
        private const val LOG_TAG = "FindToWin"
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                this, jsonStr
            )
            if(res == null) {
                Log.e(LOG_TAG, "Could not get the find-to-win data properly!")
                finish()
            } else {
                val webViewDialogFragment: FindToWinWebDialogFragment =
                    FindToWinWebDialogFragment.newInstance(res[0], res[1], res[2])
                webViewDialogFragment.setFindToWinListeners(this, this, this)
                webViewDialogFragment.display(supportFragmentManager)
            }
        } else {
            Log.e(LOG_TAG, "Could not get the find-to-win data properly!")
            finish()
        }
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