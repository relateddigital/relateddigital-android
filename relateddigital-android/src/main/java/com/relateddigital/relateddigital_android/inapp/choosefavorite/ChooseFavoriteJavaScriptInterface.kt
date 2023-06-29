package com.relateddigital.relateddigital_android.inapp.choosefavorite

import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.model.ChooseFavorite
import com.relateddigital.relateddigital_android.network.requestHandler.SubsJsonRequest

class ChooseFavoriteJavaScriptInterface internal constructor(webViewDialogFragment: ChooseFavoriteWebDialogFragment,
                                                             @get:JavascriptInterface val response: String) {
    var mWebViewDialogFragment: ChooseFavoriteWebDialogFragment = webViewDialogFragment
    private lateinit var mListener: ChooseFavoriteCompleteInterface
    private lateinit var mCopyToClipboardInterface: ChooseFavoriteCopyToClipboardInterface
    private lateinit var mShowCodeInterface: ChooseFavoriteShowCodeInterface
    private val chooseFavoriteModel: ChooseFavorite = Gson().fromJson(this.response, ChooseFavorite::class.java)

    private var subEmail = ""

    /**
     * This method closes ChooseFavoriteActivity
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
            SubsJsonRequest.createSubsJsonRequest(mWebViewDialogFragment.requireContext(), chooseFavoriteModel.actiondata!!.type!!,
                chooseFavoriteModel.actid.toString(), chooseFavoriteModel.actiondata!!.auth!!,
                email)
        } else {
            Log.e("ChooseFavorite : ", "Email entered is not valid!")
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
            report.impression = chooseFavoriteModel.actiondata!!.!!.impression
            report.click = chooseFavoriteModel.actiondata!!.report!!.click
        } catch (e: Exception) {
            Log.e("ChooseFavorite : ", "There is no report to send!")
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
   // @JavascriptInterface
    fun saveCodeGotten(code: String, email: String?, report: String?) {
        mShowCodeInterface.onCodeShown(code)
    }

    fun setChooseFavoriteListeners(
        listener: ChooseFavoriteCompleteInterface,
        copyToClipboardInterface: ChooseFavoriteCopyToClipboardInterface,
        showCodeInterface: ChooseFavoriteShowCodeInterface
    ) {
        mListener = listener
        mCopyToClipboardInterface = copyToClipboardInterface
        mShowCodeInterface = showCodeInterface
    }
}