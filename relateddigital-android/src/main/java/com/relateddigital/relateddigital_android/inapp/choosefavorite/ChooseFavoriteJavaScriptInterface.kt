package com.relateddigital.relateddigital_android.inapp.choosefavorite

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.model.ChooseFavorite
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.network.requestHandler.InAppActionClickRequest
import com.relateddigital.relateddigital_android.network.requestHandler.SubsJsonRequest

class ChooseFavoriteJavaScriptInterface internal constructor(webViewDialogFragment: ChooseFavoriteWebDialogFragment,
                                                             @get:JavascriptInterface val response: String) {
    var mWebViewDialogFragment: ChooseFavoriteWebDialogFragment = webViewDialogFragment
    private val mMainHandler = Handler(Looper.getMainLooper())
    private var mListener: ChooseFavoriteCompleteInterface? = null
    private var mCopyToClipboardInterface: ChooseFavoriteCopyToClipboardInterface? = null
    private var mShowCodeInterface: ChooseFavoriteShowCodeInterface? = null
    private val chooseFavoriteModel: ChooseFavorite = Gson().fromJson(this.response, ChooseFavorite::class.java)

    private var subEmail = ""

    /**
     * This method closes ChooseFavoriteActivity
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
            val actionData = chooseFavoriteModel.actiondata ?: return
            SubsJsonRequest.createSubsJsonRequest(context, actionData.type.orEmpty(),
                chooseFavoriteModel.actid.toString(), actionData.auth.orEmpty(),
                email)
        } else {
            Log.e("ChooseFavorite : ", "Email entered is not valid!")
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
            report.impression = chooseFavoriteModel.actiondata?.report?.impression
            report.click = chooseFavoriteModel.actiondata?.report?.click
        } catch (e: Exception) {
            Log.e("ChooseFavorite : ", "There is no report to send!")
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
        runOnMain {
            mShowCodeInterface?.onCodeShown(code)
        }
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
