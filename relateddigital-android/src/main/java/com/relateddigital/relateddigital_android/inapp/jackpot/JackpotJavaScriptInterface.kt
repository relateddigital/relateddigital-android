package com.relateddigital.relateddigital_android.inapp.jackpot

import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.model.Jackpot
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.network.requestHandler.InAppActionClickRequest
import com.relateddigital.relateddigital_android.network.requestHandler.SubsJsonRequest

class JackpotJavaScriptInterface  internal constructor(webViewDialogFragment: JackpotWebDialogFragment,
                                                       @get:JavascriptInterface val response: String) {
    var mWebViewDialogFragment: JackpotWebDialogFragment = webViewDialogFragment
    private lateinit var mListener: JackpotCompleteInterface
    private lateinit var mCopyToClipboardInterface: JackpotCopyToClipboardInterface
    private lateinit var mShowCodeInterface: JackpotShowCodeInterface
    private val jackpotModel: Jackpot = Gson().fromJson(this.response, Jackpot::class.java)

    private var subEmail = ""

    /**
     * This method closes JackpotActivity
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
            SubsJsonRequest.createSubsJsonRequest(mWebViewDialogFragment.requireContext(), jackpotModel.actiondata!!.type!!,
                jackpotModel.actid.toString(), jackpotModel.actiondata!!.auth!!,
                email)
        } else {
            Log.e("Jackpot : ", "Email entered is not valid!")
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
            report.impression = jackpotModel.actiondata!!.report!!.impression
            report.click = jackpotModel.actiondata!!.report!!.click
        } catch (e: Exception) {
            Log.e("Jackpot : ", "There is no report to send!")
            e.printStackTrace()
            report = null
        }
        if (report != null) {
            InAppActionClickRequest.createInAppActionClickRequest(mWebViewDialogFragment.requireContext(), report)
        }
    }



    /**
     * This method saves the promotion code shown
     */
    @JavascriptInterface
    fun saveCodeGotten(code: String, email: String?, report: String?) {
        mShowCodeInterface.onCodeShown(code)
    }

    fun setJackpotListeners(
        listener: JackpotCompleteInterface,
        copyToClipboardInterface: JackpotCopyToClipboardInterface,
        showCodeInterface: JackpotShowCodeInterface
    ) {
        mListener = listener
        mCopyToClipboardInterface = copyToClipboardInterface
        mShowCodeInterface = showCodeInterface
    }
}