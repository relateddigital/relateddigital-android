package com.relateddigital.relateddigital_android.inapp.clawmachine

import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.model.ClawMachine
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.InAppActionClickRequest

import com.relateddigital.relateddigital_android.network.requestHandler.SubsJsonRequest

class ClawMachineJavaScriptInterface internal constructor(webViewDialogFragment: ClawMachineWebDialogFragment,
                                                          @get:JavascriptInterface val response: String) {
    var mWebViewDialogFragment: ClawMachineWebDialogFragment = webViewDialogFragment
    private lateinit var mListener: ClawMachineCompleteInterface
    private lateinit var mCopyToClipboardInterface: ClawMachineCopyToClipboardInterface
    private lateinit var mShowCodeInterface: ClawMachineShowCodeInterface
    private val clawmachineModel: ClawMachine = Gson().fromJson(this.response, ClawMachine::class.java)

    private var subEmail = ""

    /**
     * This method closes ClawMachineActivity
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
            SubsJsonRequest.createSubsJsonRequest(mWebViewDialogFragment.requireContext(), clawmachineModel.actiondata!!.type!!,
                clawmachineModel.actid.toString(), clawmachineModel.actiondata!!.auth!!,
                email)
        } else {
            Log.e("ClawMachine : ", "Email entered is not valid!")
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
            report.impression = clawmachineModel.actiondata!!.report!!.impression
            report.click = clawmachineModel.actiondata!!.report!!.click
        } catch (e: Exception) {
            Log.e("ClawMachine : ", "There is no report to send!")
            e.printStackTrace()
            report = null
        }
        if (report != null) {
            InAppActionClickRequest.createInAppActionClickRequest(mWebViewDialogFragment.requireContext(), report)
        }
    }



    /**
     * This method saves the promotion code shown
     */
    @JavascriptInterface
    fun saveCodeGotten(code: String, email: String?, report: String?) {
        mShowCodeInterface.onCodeShown(code)
    }

    fun setClawMachineListeners(
        listener: ClawMachineCompleteInterface,
        copyToClipboardInterface: ClawMachineCopyToClipboardInterface,
        showCodeInterface: ClawMachineShowCodeInterface
    ) {
        mListener = listener
        mCopyToClipboardInterface = copyToClipboardInterface
        mShowCodeInterface = showCodeInterface
    }
}