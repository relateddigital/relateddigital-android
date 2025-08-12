package com.relateddigital.relateddigital_android.inapp.survey

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.api.JSApiClient
import com.relateddigital.relateddigital_android.api.ApiMethods
import com.relateddigital.relateddigital_android.model.SurveyModel
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.inapp.survey.SurveyCompleteInterface
import com.relateddigital.relateddigital_android.inapp.survey.WebViewDialogFragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class SurveyActivity : FragmentActivity(), SurveyCompleteInterface {

    private val LOG_TAG = "survey"

    private var jsonStr: String = ""
    private var response: SurveyModel? = null
    private var surveyJsStr: String = ""

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jsApi = JSApiClient.getClient(RelatedDigital.getRelatedDigitalModel(this).getRequestTimeoutInSecond())
            ?.create(ApiMethods::class.java)

        val headers = HashMap<String, String>()
        headers[Constants.USER_AGENT_REQUEST_KEY] = RelatedDigital.getRelatedDigitalModel(this).getUserAgent()

        val call: Call<ResponseBody> = jsApi?.getSurveyJsFile(headers)!!
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, responseJs: Response<ResponseBody>) {
                if (responseJs.isSuccessful) {
                    Log.i(LOG_TAG, "Getting survey.js is successful!")
                    try {
                        surveyJsStr = responseJs.body()?.string().orEmpty()
                        if (surveyJsStr.isEmpty()) {
                            Log.e(LOG_TAG, "Getting survey.js failed!")
                            finish()
                        } else {
                            if (savedInstanceState != null) {
                                jsonStr = savedInstanceState.getString("survey-json-str", "")
                            } else {
                                intent?.let { intent ->
                                    if (intent.hasExtra("survey-data")) {
                                        response = intent.getSerializableExtra("survey-data") as? SurveyModel
                                        if (response != null) {
                                            jsonStr = Gson().toJson(response)
                                        } else {
                                            Log.e(LOG_TAG, "Could not get the survey data properly!")
                                            finish()
                                        }
                                    } else {
                                        Log.e(LOG_TAG, "Could not get the survey data properly!")
                                        finish()
                                    }
                                }

                                if (jsonStr.isNotEmpty()) {
                                    val res = AppUtils.createSurveyFiles(this@SurveyActivity, jsonStr, surveyJsStr)
                                    if (res == null) {
                                        Log.e(LOG_TAG, "Could not get the survey data properly!")
                                        finish()
                                    } else {
                                        val webViewDialogFragment =
                                            WebViewDialogFragment.newInstance(
                                                res[0],
                                                res[1],
                                                res[2]
                                            )
                                        webViewDialogFragment.setSurveyListeners(this@SurveyActivity)
                                        webViewDialogFragment.display(supportFragmentManager)
                                    }
                                } else {
                                    Log.e(LOG_TAG, "Could not get the survey data properly!")
                                    finish()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, "Getting survey.js failed! - ${e.message}")
                        finish()
                    }
                } else {
                    Log.e(LOG_TAG, "Getting survey.js failed!")
                    finish()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(LOG_TAG, "Getting survey.js failed! - ${t.message}")
                finish()
            }
        })
    }

    override fun onSaveInstanceState(@NonNull outState: Bundle) {
        outState.putString("survey-json-str", jsonStr)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCompleted() {
        finish()
    }

    private fun sendDeeplinkToApp(deeplink: String) {
        val intent = Intent().apply {
            action = "InAppLink"
            putExtra("link", deeplink)
        }
        sendBroadcast(intent)
        Log.i(LOG_TAG, "Link sent successfully!")
    }
}