package com.relateddigital.relateddigital_android_core.inapp.findtowin

import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.relateddigital.relateddigital_android_core.model.FindToWin
import com.relateddigital.relateddigital_android_core.model.MailSubReport
import com.relateddigital.relateddigital_android_core.network.RequestHandler

class FindToWinJavaScriptInterface internal constructor(webViewDialogFragment: FindToWinWebDialogFragment,
                                                        @get:JavascriptInterface val response: String) {
    var mWebViewDialogFragment: FindToWinWebDialogFragment = webViewDialogFragment
    private lateinit var mListener: FindToWinCompleteInterface
    private lateinit var mCopyToClipboardInterface: FindToWinCopyToClipboardInterface
    private lateinit var mShowCodeInterface: FindToWinShowCodeInterface
    private val findToWinModel: FindToWin = Gson().fromJson(this.response, FindToWin::class.java)

    private var subEmail = ""

    /**
     * This method closes FindToWinActivity
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
            RequestHandler.createSubsJsonRequest(mWebViewDialogFragment.requireContext(), findToWinModel.actiondata!!.type!!,
                findToWinModel.actid.toString(), findToWinModel.actiondata!!.auth!!,
                email)
        } else {
            Log.e("FindToWin : ", "Email entered is not valid!")
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
            report.impression = findToWinModel.actiondata!!.report!!.impression
            report.click = findToWinModel.actiondata!!.report!!.click
        } catch (e: Exception) {
            Log.e("FindToWin : ", "There is no report to send!")
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
    fun saveCodeGotten(code: String, email: String?, report: String?) {
        mShowCodeInterface.onCodeShown(code)
    }

    fun setFindToWinListeners(
        listener: FindToWinCompleteInterface,
        copyToClipboardInterface: FindToWinCopyToClipboardInterface,
        showCodeInterface: FindToWinShowCodeInterface
    ) {
        mListener = listener
        mCopyToClipboardInterface = copyToClipboardInterface
        mShowCodeInterface = showCodeInterface
    }
}