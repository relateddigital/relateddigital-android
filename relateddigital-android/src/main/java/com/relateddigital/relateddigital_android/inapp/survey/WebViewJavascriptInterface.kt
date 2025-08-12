package com.relateddigital.relateddigital_android.inapp.survey

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

    private val TAG = "SurveyJSInterface"
    private val mResponse: String = response
    private val surveyModel: SurveyModel = Gson().fromJson(mResponse, SurveyModel::class.java)
    private var mListener: SurveyCompleteInterface? = null

    @JavascriptInterface
    fun close() {
        mWebViewDialogFragment.dismiss()
        mListener?.onCompleted()
    }

    @JavascriptInterface
    fun sendReport(surveyResultJson: String) {
        Log.d(TAG, "Anket Sonuçları Alındı: $surveyResultJson")

        try {
            val report = MailSubReport().apply {
                impression = surveyModel.actiondata?.report?.impression
                click = surveyModel.actiondata?.report?.click
            }
            InAppActionClickRequest.createInAppActionClickRequest(mWebViewDialogFragment.requireContext(), report)
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
                RelatedDigital.customEvent(mWebViewDialogFragment.requireContext(), "survey-report", parameters)
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
}