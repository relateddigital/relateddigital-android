package com.relateddigital.relateddigital_android.inapp.spintowin

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
import com.relateddigital.relateddigital_android.model.SpinToWin
import com.relateddigital.relateddigital_android.util.AppUtils

class SpinToWinActivity : FragmentActivity(), SpinToWinCompleteInterface, SpinToWinCopyToClipboardInterface {
    private var jsonStr: String? = ""
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            jsonStr = savedInstanceState.getString("spin-to-win-json-str", "")
        } else {
            val intent = intent
            if (intent != null && intent.hasExtra("spin-to-win-data")) {
                val response: SpinToWin? = intent.getSerializableExtra("spin-to-win-data") as SpinToWin?
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
                webViewDialogFragment.setSpinToWinListeners(this, this)
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
}