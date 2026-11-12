package com.relateddigital.relateddigital_android.inapp.giftcatch

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.GiftRain
import com.relateddigital.relateddigital_android.model.GiftRainReport
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.InAppActionClickRequest
import com.relateddigital.relateddigital_android.network.requestHandler.SubsJsonRequest
import java.util.HashMap

class GiftCatchJavaScriptInterface internal constructor(webViewDialogFragment: GiftCatchWebDialogFragment,
                                                        @get:JavascriptInterface val response: String) {
    var mWebViewDialogFragment: GiftCatchWebDialogFragment = webViewDialogFragment
    private val mMainHandler = Handler(Looper.getMainLooper())
    private var mListener: GiftCatchCompleteInterface? = null
    private var mCopyToClipboardInterface: GiftCatchCopyToClipboardInterface? = null
    private var mShowCodeInterface: GiftCatchShowCodeInterface? = null
    private val giftRainModel: GiftRain = Gson().fromJson(this.response, GiftRain::class.java)

    private var subEmail = ""

    /**
     * This method closes GiftCatchActivity
     */
    @JavascriptInterface
    fun close() {
        runOnMain {
            mWebViewDialogFragment.dismissAllowingStateLoss()
            mListener?.onCompleted()
        }
    }

    /**
     * This method copies the coupon code to clipboard
     * and ends the activity
     *
     * @param couponCode - String: coupon code
     */
    @JavascriptInterface
    fun copyToClipboard(couponCode: String?, link: String?) {
        runOnMain {
            mWebViewDialogFragment.dismissAllowingStateLoss()
            mCopyToClipboardInterface?.copyToClipboard(couponCode, link)
        }
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
            val context = mWebViewDialogFragment.context ?: return
            val actionData = giftRainModel.actiondata ?: return
            SubsJsonRequest.createSubsJsonRequest(context, actionData.type.orEmpty(),
                giftRainModel.actid.toString(), actionData.auth.orEmpty(),
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
            report.impression = giftRainModel.actiondata?.report?.impression
            report.click = giftRainModel.actiondata?.report?.click
        } catch (e: Exception) {
            Log.e("Gift Rain : ", "There is no report to send!")
            e.printStackTrace()
            report = null
        }
        val context = mWebViewDialogFragment.context
        if (report != null && context != null) {
            InAppActionClickRequest.createInAppActionClickRequest(context, report)
        }
    }

    /**
     * This method saves the promotion code shown
     */
    @JavascriptInterface
    fun saveCodeGotten(code: String, email: String?, report: String?) {
        sendPromotionCodeInfo(email = email.orEmpty(), promotionCode = code)
        runOnMain {
            mShowCodeInterface?.onCodeShown(code)
        }
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

    private fun sendPromotionCodeInfo(email: String, promotionCode: String) {
        val actionId = "act-" + giftRainModel.actid
        val parameters = HashMap<String, String>()
        parameters[Constants.PROMOTION_CODE_REQUEST_KEY] = promotionCode
        parameters[Constants.ACTION_ID_REQUEST_KEY] = actionId
        if (email.isNotEmpty()) {
            parameters[Constants.PROMOTION_CODE_EMAIL_REQUEST_KEY] = email
        }
        val context = mWebViewDialogFragment.context ?: return
        RelatedDigital.customEvent(
            context = context,
            pageName = Constants.PAGE_NAME_REQUEST_VAL,
            properties = parameters)
    }

    private fun runOnMain(block: () -> Unit) {
        mMainHandler.post {
            if (!mWebViewDialogFragment.isAdded) {
                return@post
            }
            try {
                block()
            } catch (e: Exception) {
                Log.e("JavaScriptInterface", "Could not handle the web view call!", e)
            }
        }
    }
}
