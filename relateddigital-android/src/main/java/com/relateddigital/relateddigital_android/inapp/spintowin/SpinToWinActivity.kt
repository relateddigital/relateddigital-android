package com.relateddigital.relateddigital_android.inapp.spintowin

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.model.SpinToWin
import com.relateddigital.relateddigital_android.model.SpinToWinExtendedProps
import com.relateddigital.relateddigital_android.util.ActivityUtils
import com.relateddigital.relateddigital_android.util.AppUtils
import java.net.URI


class SpinToWinActivity : FragmentActivity(), SpinToWinCompleteInterface,
    SpinToWinCopyToClipboardInterface, SpinToWinShowCodeInterface {
    private var jsonStr: String? = ""
    private var response: SpinToWin? = null
    private var spinToWinPromotionCode = ""

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            jsonStr = savedInstanceState.getString("spin-to-win-json-str", "")
        } else {
            val intent = intent
            if (intent != null && intent.hasExtra("spin-to-win-data")) {
                response = intent.getSerializableExtra("spin-to-win-data") as SpinToWin?
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
                this, jsonStr
            )
            if(res == null) {
                Log.e(LOG_TAG, "Could not get the spin-to-win data properly!")
                finish()
            } else {
                val webViewDialogFragment: SpinToWinWebDialogFragment =
                    SpinToWinWebDialogFragment.newInstance(res[0], res[1], res[2])
                webViewDialogFragment.setSpinToWinListeners(this, this, this)
                webViewDialogFragment.display(supportFragmentManager)
            }
        } else {
            Log.e(LOG_TAG, "Could not get the spin-to-win data properly!")
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("spin-to-win-json-str", jsonStr)
        super.onSaveInstanceState(outState)
    }

    override fun onCompleted() {
        finish()
    }

    override fun copyToClipboard(couponCode: String?) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Coupon Code", couponCode)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(applicationContext, getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show()
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
    }

    override fun onCodeShown(code: String) {
        spinToWinPromotionCode = code
    }
}