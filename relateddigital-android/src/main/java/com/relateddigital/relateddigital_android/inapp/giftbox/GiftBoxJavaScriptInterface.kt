package com.relateddigital.relateddigital_android.inapp.giftbox

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.model.GiftBox
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.SubsJsonRequest

class GiftBoxJavaScriptInterface internal constructor(webViewDialogFragment: GiftBoxWebDialogFragment,
                                                      @get:JavascriptInterface val response: String) {
    var mWebViewDialogFragment: GiftBoxWebDialogFragment = webViewDialogFragment
    private val mMainHandler = Handler(Looper.getMainLooper())
    private var mListener: GiftBoxCompleteInterface? = null
    private var mCopyToClipboardInterface: GiftBoxCopyToClipboardInterface? = null
    private var mShowCodeInterface: GiftBoxShowCodeInterface? = null
    private val giftboxModel: GiftBox = Gson().fromJson(this.response, GiftBox::class.java)

    private var subEmail = ""

    /**
     * This method closes GiftBoxActivity
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
            val actionData = giftboxModel.actiondata ?: return
            SubsJsonRequest.createSubsJsonRequest(context, actionData.type.orEmpty(),
                giftboxModel.actid.toString(), actionData.auth.orEmpty(),
                email)
        } else {
            Log.e("GiftBox : ", "Email entered is not valid!")
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
            report.impression = giftboxModel.actiondata!!.!!.impression
            report.click = giftboxModel.actiondata!!.report!!.click
        } catch (e: Exception) {
            Log.e("GiftBox : ", "There is no report to send!")
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
        runOnMain {
            mShowCodeInterface?.onCodeShown(code)
        }
    }

    fun setGiftBoxListeners(
        listener: GiftBoxCompleteInterface,
        copyToClipboardInterface: GiftBoxCopyToClipboardInterface,
        showCodeInterface: GiftBoxShowCodeInterface
    ) {
        mListener = listener
        mCopyToClipboardInterface = copyToClipboardInterface
        mShowCodeInterface = showCodeInterface
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
