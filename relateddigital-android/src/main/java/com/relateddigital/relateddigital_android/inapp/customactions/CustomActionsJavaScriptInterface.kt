package com.relateddigital.relateddigital_android.inapp.customactions

import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.model.CustomActions
import com.relateddigital.relateddigital_android.network.requestHandler.SubsJsonRequest

class CustomActionsJavaScriptInterface internal constructor(webViewDialogFragment: CustomActionsWebDialogFragment,@get:JavascriptInterface val response : String) {

    var mWebViewDialogFragment: CustomActionsWebDialogFragment = webViewDialogFragment
    private lateinit var mListener: CustomActionsCompleteInterface
    private lateinit var mCopyToClipboardInterface: CustomActionsCopyToClipboardInterface
    private lateinit var mShowCodeInterface: CustomActionsShowCodeInterface
    private val customActionsModel: CustomActions = Gson().fromJson(this.response, CustomActions::class.java)

    private var subEmail = ""

    /**
     * This method closes CustomActionsActivity
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
   /* fun subscribeEmail(email: String?) {
        if (!email.isNullOrEmpty()) {
            subEmail = email
            SubsJsonRequest.createSubsJsonRequest(mWebViewDialogFragment.requireContext(), customActionsModel.actiondata!!.type!!,
                customActionsModel.actid.toString(), customActionsModel.actiondata!!.auth!!,
                email)
        } else {
            Log.e("CustomActions : ", "Email entered is not valid!")
        }
    } */

    /**
     * This method sends the report to the server
     */
    /*
    @JavascriptInterface
    fun sendReport() {
        var report: MailSubReport?
        try {
            report = MailSubReport()
            report.impression = customActionsModel.actiondata!!.!!.impression
            report.click = customActionsModel.actiondata!!.report!!.click
        } catch (e: Exception) {
            Log.e("CustomActions : ", "There is no report to send!")
            e.printStackTrace()
            report = null
        }
        if (report != null) {
            RequestHandler.createInAppActionClickRequest(mWebViewDialogFragment.requireContext(), report)
        }
    }

    */

    /**
     * This method saves the promotion code shown
     */
    //@JavascriptInterface
    fun saveCodeGotten(code: String, email: String?, report: String?) {
        mShowCodeInterface.onCodeShown(code)
    }

    fun setCustomActionsListeners(
        listener: CustomActionsCompleteInterface,
        copyToClipboardInterface: CustomActionsCopyToClipboardInterface,
        showCodeInterface: CustomActionsShowCodeInterface
    ) {
        mListener = listener
        mCopyToClipboardInterface = copyToClipboardInterface
        mShowCodeInterface = showCodeInterface
    }
}