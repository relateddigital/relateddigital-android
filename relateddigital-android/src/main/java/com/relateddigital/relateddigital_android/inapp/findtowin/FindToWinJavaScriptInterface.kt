package com.relateddigital.relateddigital_android.inapp.findtowin

import android.webkit.JavascriptInterface

class FindToWinJavaScriptInterface internal constructor(webViewDialogFragment: FindToWinWebDialogFragment,
                                                        @get:JavascriptInterface val response: String) {
    var mWebViewDialogFragment: FindToWinWebDialogFragment = webViewDialogFragment
    private lateinit var mListener: FindToWinCompleteInterface
    private lateinit var mCopyToClipboardInterface: FindToWinCopyToClipboardInterface
    private lateinit var mShowCodeInterface: FindToWinShowCodeInterface

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
    fun copyToClipboard(couponCode: String?) {
        mWebViewDialogFragment.dismiss()
        mCopyToClipboardInterface.copyToClipboard(couponCode)
    }

    /**
     * This method sends a subscription request for the email entered
     *
     * @param email : String - the value entered for email
     */
    @JavascriptInterface
    fun subscribeEmail(email: String?) {
        //TODO get it from SpinToWin
    }

    /**
     * This method sends the report to the server
     */
    @JavascriptInterface
    fun sendReport() {
        //TODO get it from SpinToWin
    }

    /**
     * This method saves the promotion code shown
     */
    @JavascriptInterface
    fun saveCodeGotten(code: String) {
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

    private fun sendPromotionCodeInfo(promotionCode: String, sliceText: String) {
        // TODO : check if this is necessary
    }
}