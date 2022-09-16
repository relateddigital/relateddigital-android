package com.relateddigital.relateddigital_android.inapp.giftcatch

import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.model.GiftRain
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.network.RequestHandler

class GiftCatchJavaScriptInterface internal constructor(webViewDialogFragment: GiftCatchWebDialogFragment,
                                                        @get:JavascriptInterface val response: String) {
    var mWebViewDialogFragment: GiftCatchWebDialogFragment = webViewDialogFragment
    private lateinit var mListener: GiftCatchCompleteInterface
    private lateinit var mCopyToClipboardInterface: GiftCatchCopyToClipboardInterface
    private lateinit var mShowCodeInterface: GiftCatchShowCodeInterface
    private val giftRainModel: GiftRain = Gson().fromJson(this.response, GiftRain::class.java)

    private var subEmail = ""

    /**
     * This method closes GiftCatchActivity
     */
    @JavascriptInterface
    fun close() {
        mWebViewDialogFragment.dismiss()
        mListener.onCompleted()
    }

    /**
     * This method copies the coupon code to clipboard
     * and ends the activity
     *
     * @param couponCode - String: coupon code
     */
    @JavascriptInterface
    fun copyToClipboard(couponCode: String?, link: String?) {
        mWebViewDialogFragment.dismiss()
        mCopyToClipboardInterface.copyToClipboard(couponCode, link)
    }

    /**
     * This method sends a subscription request for the email entered
     *
     * @param email : String - the value entered for email
     */
    @JavascriptInterface
    fun subscribeEmail(email: String?) {
        if (!email.isNullOrEmpty()) {
            subEmail = email
            RequestHandler.createSubsJsonRequest(mWebViewDialogFragment.requireContext(), giftRainModel.actiondata!!.type!!,
                giftRainModel.actid.toString(), giftRainModel.actiondata!!.auth!!,
                email)
        } else {
            Log.e("Gift Rain : ", "Email entered is not valid!")
        }
    }

    /**
     * This method sends the report to the server
     */
    @JavascriptInterface
    fun sendReport() {
        var report: MailSubReport?
        try {
            report = MailSubReport()
            report.impression = giftRainModel.actiondata!!.report!!.impression
            report.click = giftRainModel.actiondata!!.report!!.click
        } catch (e: Exception) {
            Log.e("Gift Rain : ", "There is no report to send!")
            e.printStackTrace()
            report = null
        }
        if (report != null) {
            RequestHandler.createInAppActionClickRequest(mWebViewDialogFragment.requireContext(), report)
        }
    }

    /**
     * This method saves the promotion code shown
     */
    @JavascriptInterface
    fun saveCodeGotten(code: String) {
        mShowCodeInterface.onCodeShown(code)
    }

    fun setGiftCatchListeners(
        listener: GiftCatchCompleteInterface,
        copyToClipboardInterface: GiftCatchCopyToClipboardInterface,
        showCodeInterface: GiftCatchShowCodeInterface
    ) {
        mListener = listener
        mCopyToClipboardInterface = copyToClipboardInterface
        mShowCodeInterface = showCodeInterface
    }
}