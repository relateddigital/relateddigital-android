package com.relateddigital.relateddigital_android.inapp.giftcatch

import android.webkit.JavascriptInterface

class GiftCatchJavaScriptInterface internal constructor(webViewDialogFragment: GiftCatchWebDialogFragment,
                                                        @get:JavascriptInterface val response: String) {
    var mWebViewDialogFragment: GiftCatchWebDialogFragment = webViewDialogFragment
    private lateinit var mListener: GiftCatchCompleteInterface
    private lateinit var mCopyToClipboardInterface: GiftCatchCopyToClipboardInterface
    private lateinit var mShowCodeInterface: GiftCatchShowCodeInterface

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
    fun saveCodeGotten() {
        //TODO : send it to the interface callback to show it on banner
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

    private fun sendPromotionCodeInfo(promotionCode: String, sliceText: String) {
        // TODO : check if this is necessary
    }
}