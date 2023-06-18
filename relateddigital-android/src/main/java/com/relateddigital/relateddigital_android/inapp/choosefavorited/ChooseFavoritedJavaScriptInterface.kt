package com.relateddigital.relateddigital_android.inapp.choosefavorited

import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.model.ChooseFavorited
import com.relateddigital.relateddigital_android.network.requestHandler.SubsJsonRequest

class ChooseFavoritedJavaScriptInterface internal constructor(webViewDialogFragment: ChooseFavoritedWebDialogFragment,
                                                              @get:JavascriptInterface val response: String) {
    var mWebViewDialogFragment: ChooseFavoritedWebDialogFragment = webViewDialogFragment
    private lateinit var mListener: ChooseFavoritedCompleteInterface
    private lateinit var mCopyToClipboardInterface: ChooseFavoritedCopyToClipboardInterface
    private lateinit var mShowCodeInterface: ChooseFavoritedShowCodeInterface
    private val chooseFavoritedModel: ChooseFavorited = Gson().fromJson(this.response, ChooseFavorited::class.java)

    private var subEmail = ""

    /**
     * This method closes ChooseFavoritedActivity
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
            SubsJsonRequest.createSubsJsonRequest(mWebViewDialogFragment.requireContext(), chooseFavoritedModel.actiondata!!.type!!,
                chooseFavoritedModel.actid.toString(), chooseFavoritedModel.actiondata!!.auth!!,
                email)
        } else {
            Log.e("ChooseFavorited : ", "Email entered is not valid!")
        }
    }

    /**
     * This method sends the report to the server
     */
    /*
    @JavascriptInterface
    fun sendReport() {
        var report: MailSubReport?
        try {
            report = MailSubReport()
            report.impression = chooseFavoritedModel.actiondata!!.!!.impression
            report.click = chooseFavoritedModel.actiondata!!.report!!.click
        } catch (e: Exception) {
            Log.e("ChooseFavorited : ", "There is no report to send!")
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
    @JavascriptInterface
    fun saveCodeGotten(code: String, email: String?, report: String?) {
        mShowCodeInterface.onCodeShown(code)
    }

    fun setChooseFavoritedListeners(
        listener: ChooseFavoritedCompleteInterface,
        copyToClipboardInterface: ChooseFavoritedCopyToClipboardInterface,
        showCodeInterface: ChooseFavoritedShowCodeInterface
    ) {
        mListener = listener
        mCopyToClipboardInterface = copyToClipboardInterface
        mShowCodeInterface = showCodeInterface
    }
}