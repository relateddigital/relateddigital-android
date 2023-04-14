package com.relateddigital.relateddigital_android.inapp.findtowin

import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.FindToWin
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.network.RequestHandler
import java.util.*

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
    fun saveCodeGotten(code: String, email: String, report: String?) {
        sendPromotionCodeInfo(email = email, promotionCode = code)
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

    private fun sendPromotionCodeInfo(email: String, promotionCode: String) {
        val actionId = "act-" + findToWinModel.actid
        val parameters = HashMap<String, String>()
        parameters[Constants.PROMOTION_CODE_REQUEST_KEY] = promotionCode
        parameters[Constants.ACTION_ID_REQUEST_KEY] = actionId
        if (email.isNotEmpty()) {
            parameters[Constants.PROMOTION_CODE_EMAIL_REQUEST_KEY] = email
        }
        RelatedDigital.customEvent(
            context = mWebViewDialogFragment.requireContext(),
            pageName = Constants.PAGE_NAME_REQUEST_VAL,
            properties = parameters)
    }
}