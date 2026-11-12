package com.relateddigital.relateddigital_android.inapp.survey

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.model.SurveyModel
import com.relateddigital.relateddigital_android.model.SurveyResult
import com.relateddigital.relateddigital_android.network.requestHandler.InAppActionClickRequest

class WebViewJavascriptInterface(
    private val mWebViewDialogFragment: WebViewDialogFragment,
    response: String
) {
    private val mMainHandler = Handler(Looper.getMainLooper())

    private val TAG = "SurveyJSInterface"
    private val mResponse: String = response
    private val surveyModel: SurveyModel = Gson().fromJson(mResponse, SurveyModel::class.java)
    private var mListener: SurveyCompleteInterface? = null

    @JavascriptInterface
    fun close() {
        runOnMain {
            mWebViewDialogFragment.dismissAllowingStateLoss()
            mListener?.onCompleted()
        }
    }

    @JavascriptInterface
    fun sendReport(surveyResultJson: String) {
        Log.d(TAG, "Anket Sonuçları Alındı: $surveyResultJson")

        val context = mWebViewDialogFragment.context ?: return

        val parentActivity = mWebViewDialogFragment.activity ?: com.relateddigital.relateddigital_android.util.ActivityUtils.parentActivity

        try {
            val report = MailSubReport().apply {
                impression = surveyModel.actiondata?.report?.impression
                click = surveyModel.actiondata?.report?.click
            }
            InAppActionClickRequest.createInAppActionClickRequest(context, report, parentActivity)
        } catch (e: Exception) {
            Log.e("Survey", "Rapor gönderilirken bir hata oluştu: ${e.message}")
        }

        try {
            val result = Gson().fromJson(surveyResultJson, SurveyResult::class.java)
            result?.questions?.takeIf { it.isNotEmpty() }?.forEach { qa ->
                val parameters = HashMap<String, String>().apply {
                    put("OM.s_group", result.title ?: "")
                    put("OM.s_cat", qa.question ?: "")
                    put("OM.s_page", qa.answer ?: "")
                }
                RelatedDigital.customEvent(context, "survey-report", parameters, parentActivity)
                Log.d(TAG, "Custom Event Gönderildi: Soru='${qa.question}', Cevap='${qa.answer}'")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Anket sonuçları işlenirken veya gönderilirken hata oluştu.", e)
        }
    }

    @JavascriptInterface
    fun getResponse(): String = mResponse

    fun setSurveyListeners(listener: SurveyCompleteInterface) {
        mListener = listener
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
