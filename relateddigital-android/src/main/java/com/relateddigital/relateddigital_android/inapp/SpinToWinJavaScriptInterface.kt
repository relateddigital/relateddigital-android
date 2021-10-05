package com.relateddigital.relateddigital_android.inapp

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.model.Slice
import com.relateddigital.relateddigital_android.model.SpinToWin
import com.relateddigital.relateddigital_android.network.RequestHandler
import org.json.JSONObject
import java.util.*

class SpinToWinJavaScriptInterface internal constructor(webViewDialogFragment: SpinToWinWebDialogFragment,
                                                        @get:JavascriptInterface val response: String) {
    var mWebViewDialogFragment: SpinToWinWebDialogFragment = webViewDialogFragment
    private var mListener: SpinToWinCompleteInterface? = null
    private var mCopyToClipboardInterface: SpinToWinCopyToClipboardInterface? = null

    private val spinToWinModel: SpinToWin = Gson().fromJson(this.response, SpinToWin::class.java)
    private var subEmail = ""

    /**
     * This method closes SpinToWinActivity
     */
    @JavascriptInterface
    fun close() {
        mWebViewDialogFragment.dismiss()
        mListener!!.onCompleted()
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
        mCopyToClipboardInterface!!.copyToClipboard(couponCode)
    }

    /**
     * This method sends a subscription request for the email entered
     *
     * @param email : String - the value entered for email
     */
    @JavascriptInterface
    fun subscribeEmail(email: String?) {
        if (email != "" && email != null) {
            subEmail = email
            RequestHandler.createSubsJsonRequest(mWebViewDialogFragment.requireContext(), spinToWinModel.actiondata!!.type!!,
                    spinToWinModel.actid.toString(), spinToWinModel.actiondata!!.auth!!,
                    email)
        } else {
            Log.e("Spin to Win : ", "Email entered is not valid!")
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
            report.impression = spinToWinModel.actiondata!!.report!!.impression
            report.click = spinToWinModel.actiondata!!.report!!.click
        } catch (e: Exception) {
            Log.e("Spin to Win : ", "There is no report to send!")
            e.printStackTrace()
            report = null
        }
        if (report != null) {
            RequestHandler.createInAppActionClickRequest(mWebViewDialogFragment.requireContext(), report)
        }
    }

    /**
     * This method makes a request to the ad server to get the coupon code
     */
    @get:JavascriptInterface
    @get:RequiresApi(api = Build.VERSION_CODES.KITKAT)
    val promotionCode: Unit
        get() {
            var selectedCode = ""
            var selectedSliceText = ""
            val promotionCodes: MutableList<String> = ArrayList()
            val sliceTexts: MutableList<String> = ArrayList()
            val promotionIndexes: MutableList<Int> = ArrayList()
            var selectedIndex = -1
            val promoAuth: String = spinToWinModel.actiondata!!.promoAuth!!
            val actId: Int = spinToWinModel.actid!!
            for (i in spinToWinModel.actiondata!!.slices!!.indices) {
                val slice: Slice = spinToWinModel.actiondata!!.slices!![i]
                if (slice.type.equals("promotion")) {
                    promotionCodes.add(slice.code!!)
                    promotionIndexes.add(i)
                    sliceTexts.add(slice.displayName!!)
                }
            }
            if (promotionCodes.size > 0) {
                try {
                    val random = Random()
                    val randomIndex = random.nextInt(promotionCodes.size)
                    selectedCode = promotionCodes[randomIndex]
                    selectedIndex = promotionIndexes[randomIndex]
                    selectedSliceText = sliceTexts[randomIndex]
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (selectedIndex != -1) {
                try {
                    val queryParameters = HashMap<String, String>()
                    queryParameters["promotionid"] = selectedCode
                    queryParameters["promoauth"] = promoAuth
                    queryParameters["actionid"] = actId.toString()
                    RequestHandler.createSpinToWinPromoCodeRequest(mWebViewDialogFragment.requireContext(),
                            getVisilabsCallback(selectedIndex,
                                    selectedSliceText), queryParameters)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val mainHandler = Handler(Looper.getMainLooper())
                val myRunnable = Runnable {
                    mWebViewDialogFragment.getWebView()!!.evaluateJavascript(
                            "window.chooseSlice(-1, undefined);", null)
                }
                mainHandler.post(myRunnable)
            }
        }

    private fun getVisilabsCallback(idx: Int, sliceText: String): VisilabsCallback {
        return object : VisilabsCallback {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            override fun success(response: VisilabsResponse?) {
                val rawResponse: String = response!!.rawResponse
                val jsonResponse = JSONObject(rawResponse)
                val promotionCode: String = jsonResponse.getString("promocode")
                val mainHandler = Handler(Looper.getMainLooper())
                val myRunnable = Runnable {
                    sendPromotionCodeInfo(promotionCode, sliceText)
                    mWebViewDialogFragment.getWebView()!!.evaluateJavascript(
                            "window.chooseSlice($idx,'$promotionCode');",
                            null)
                }
                mainHandler.post(myRunnable)
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            override fun fail(response: VisilabsResponse?) {
                val mainHandler = Handler(Looper.getMainLooper())
                val myRunnable = Runnable {
                    mWebViewDialogFragment.getWebView()!!.evaluateJavascript(
                            "window.chooseSlice(-1, undefined);", null)
                }
                mainHandler.post(myRunnable)
            }
        }
    }

    fun setSpinToWinListeners(listener: SpinToWinCompleteInterface?,
                              copyToClipboardInterface: SpinToWinCopyToClipboardInterface?) {
        mListener = listener
        mCopyToClipboardInterface = copyToClipboardInterface
    }

    private fun sendPromotionCodeInfo(promotionCode: String, sliceText: String) {
        val parameters = HashMap<String, String>()
        parameters[Constants.PROMOTION_CODE_REQUEST_KEY] = promotionCode
        parameters[Constants.ACTION_ID_REQUEST_KEY] = "act-" + spinToWinModel.actid.toString()
        if (subEmail.isNotEmpty()) {
            parameters[Constants.PROMOTION_CODE_EMAIL_REQUEST_KEY] = subEmail
        }
        parameters[Constants.PROMOTION_CODE_TITLE_REQUEST_KEY] = spinToWinModel.actiondata!!.promocodeTitle!!
        if (sliceText.isNotEmpty()) {
            parameters[Constants.PROMOTION_CODE_SLICE_TEXT_REQUEST_KEY] = sliceText
        }
        RelatedDigital.customEvent(
                context = mWebViewDialogFragment.requireContext(),
                pageName = Constants.PAGE_NAME_REQUEST_VAL,
                properties = parameters)
    }

}