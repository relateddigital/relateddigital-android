package com.relateddigital.relateddigital_android.inapp.giftcatch

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.model.GiftCatchExtendedProps
import com.relateddigital.relateddigital_android.util.ActivityUtils

class GiftCatchActivity : FragmentActivity(), GiftCatchCompleteInterface,
    GiftCatchCopyToClipboardInterface, GiftCatchShowCodeInterface {
    private var jsonStr: String? = ""
    private var giftCatchPromotionCode = ""

    companion object {
        private const val LOG_TAG = "GiftCatch"
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO : Get this from SpinToWin
        val webViewDialogFragment: GiftCatchWebDialogFragment =
            GiftCatchWebDialogFragment.newInstance("gift_catch_index.html", "test")
        webViewDialogFragment.setGiftCatchListeners(this, this, this)
        webViewDialogFragment.display(supportFragmentManager)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("gift-catch-json-str", jsonStr)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO: open the lines when real data comes
        /*if (giftCatchPromotionCode.isNotEmpty()) {
            try {
                val extendedProps = Gson().fromJson(
                    URI(response!!.actiondata!!.extendedProps).path,
                    GiftCatchExtendedProps::class.java
                )

                if (!extendedProps.promocode_banner_button_label.isNullOrEmpty()) {*/
                    if(ActivityUtils.parentActivity != null) {
                        val giftCatchCodeBannerFragment =
                            GiftCatchCodeBannerFragment.newInstance(GiftCatchExtendedProps(), giftCatchPromotionCode)

                        val transaction: FragmentTransaction =
                            (ActivityUtils.parentActivity as FragmentActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(android.R.id.content, giftCatchCodeBannerFragment)
                        transaction.commit()
                        ActivityUtils.parentActivity = null
                    }/*
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "GiftCatchCodeBanner : " + e.message)
            }
        }*/
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

    override fun onCodeShown(code: String) {
        giftCatchPromotionCode = code
    }
}