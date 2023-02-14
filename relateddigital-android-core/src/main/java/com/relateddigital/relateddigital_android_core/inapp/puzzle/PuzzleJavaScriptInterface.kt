package com.relateddigital.relateddigital_android_core.inapp.puzzle

import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.relateddigital.relateddigital_android_core.model.Puzzle
import com.relateddigital.relateddigital_android_core.model.MailSubReport
import com.relateddigital.relateddigital_android_core.network.RequestHandler

class PuzzleJavaScriptInterface internal constructor(webViewDialogFragment: PuzzleWebDialogFragment,
                                                        @get:JavascriptInterface val response: String) {
    var mWebViewDialogFragment: PuzzleWebDialogFragment = webViewDialogFragment
    private lateinit var mListener: PuzzleCompleteInterface
    private lateinit var mCopyToClipboardInterface: PuzzleCopyToClipboardInterface
    private lateinit var mShowCodeInterface: PuzzleShowCodeInterface
    private val puzzleModel: Puzzle = Gson().fromJson(this.response, Puzzle::class.java)

    private var subEmail = ""

    /**
     * This method closes PuzzleActivity
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
            RequestHandler.createSubsJsonRequest(mWebViewDialogFragment.requireContext(), puzzleModel.actiondata!!.type!!,
                puzzleModel.actid.toString(), puzzleModel.actiondata!!.auth!!,
                email)
        } else {
            Log.e("Puzzle : ", "Email entered is not valid!")
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
            report.impression = puzzleModel.actiondata!!.report!!.impression
            report.click = puzzleModel.actiondata!!.report!!.click
        } catch (e: Exception) {
            Log.e("Puzzle : ", "There is no report to send!")
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

    fun setPuzzleListeners(
        listener: PuzzleCompleteInterface,
        copyToClipboardInterface: PuzzleCopyToClipboardInterface,
        showCodeInterface: PuzzleShowCodeInterface
    ) {
        mListener = listener
        mCopyToClipboardInterface = copyToClipboardInterface
        mShowCodeInterface = showCodeInterface
    }
}