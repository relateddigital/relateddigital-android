package com.relateddigital.relateddigital_android.network

import androidx.fragment.app.FragmentTransaction
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.api.*
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.inapp.InAppManager
import com.relateddigital.relateddigital_android.inapp.VisilabsResponse
import com.relateddigital.relateddigital_android.inapp.findtowin.FindToWinActivity
import com.relateddigital.relateddigital_android.inapp.giftbox.GiftBoxActivity
import com.relateddigital.relateddigital_android.inapp.giftcatch.GiftCatchActivity
import com.relateddigital.relateddigital_android.inapp.mailsubsform.MailSubscriptionFormHalfFragment
import com.relateddigital.relateddigital_android.inapp.notification.InAppNotificationFragment
import com.relateddigital.relateddigital_android.inapp.scratchtowin.ScratchToWinActivity
import com.relateddigital.relateddigital_android.inapp.shaketowin.ShakeToWinActivity
import com.relateddigital.relateddigital_android.inapp.socialproof.SocialProofFragment
import com.relateddigital.relateddigital_android.inapp.spintowin.SpinToWinActivity
import com.relateddigital.relateddigital_android.model.*
import com.relateddigital.relateddigital_android.model.Retention
import com.relateddigital.relateddigital_android.push.EuromessageCallback
import com.relateddigital.relateddigital_android.recommendation.RecommendationUtils
import com.relateddigital.relateddigital_android.util.ActivityUtils
import com.relateddigital.relateddigital_android.util.InAppNotificationTimer
import com.relateddigital.relateddigital_android.util.RetryCounterManager
import com.relateddigital.relateddigital_android.util.SharedPref
import okhttp3.Headers
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object RequestSender {
    private const val LOG_TAG = "Request Sender"
    private val requestQueue = ArrayList<Request>()
    private var isSendingARequest = false
    private var retryCounter = 0
    fun addToQueue(request: Request, model: RelatedDigitalModel, context: Context) {
        requestQueue.add(request)
        send(model, context)
    }

    private fun send(model: RelatedDigitalModel, context: Context) {
        if (isSendingARequest || requestQueue.isEmpty()) {
            return
        }

        isSendingARequest = true

        val currentRequest = requestQueue[0]

        when (currentRequest.domain) {
            Domain.LOG_LOGGER -> {
                val loggerApiInterface =
                        LoggerApiClient.getClient(model.getRequestTimeoutInSecond())
                                ?.create(ApiMethods::class.java)
                val call: Call<Void> = loggerApiInterface?.sendToLogger(
                        model.getDataSource(),
                        currentRequest.headerMap, currentRequest.queryMap
                )!!
                call.enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            applySuccessConditions(
                                    response.headers(), response.raw().request.url.toString(),
                                    model, Domain.LOG_LOGGER, context
                            )
                        } else {
                            applyFailConditions(call.request().url.toString(), model, context)
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        applyFailConditions(call.request().url.toString(), model, context)
                    }
                })
            }

            Domain.LOG_REAL_TIME -> {
                val realTimeApiInterface =
                        RealTimeApiClient.getClient(model.getRequestTimeoutInSecond())
                                ?.create(ApiMethods::class.java)
                val call: Call<Void> = realTimeApiInterface?.sendToRealTime(
                        model.getDataSource(),
                        currentRequest.headerMap, currentRequest.queryMap
                )!!
                call.enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            applySuccessConditions(
                                    response.headers(), response.raw().request.url.toString(),
                                    model, Domain.LOG_REAL_TIME, context
                            )
                        } else {
                            applyFailConditions(call.request().url.toString(), model, context)
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        applyFailConditions(call.request().url.toString(), model, context)
                    }
                })
            }

            Domain.LOG_S -> {
                val sApiInterface = SApiClient.getClient(model.getRequestTimeoutInSecond())
                        ?.create(ApiMethods::class.java)
                val call: Call<Void> = sApiInterface?.sendSubsJsonRequestToS(
                        currentRequest.headerMap, currentRequest.queryMap
                )!!
                call.enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            applySuccessConditions(
                                    response.headers(), response.raw().request.url.toString(),
                                    model, Domain.LOG_S, context
                            )
                        } else {
                            applyFailConditions(call.request().url.toString(), model, context)
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        applyFailConditions(call.request().url.toString(), model, context)
                    }
                })
            }

            Domain.IN_APP_NOTIFICATION_ACT_JSON -> {
                val actJsonInterface = SApiClient.getClient(model.getRequestTimeoutInSecond())
                    ?.create(ApiMethods::class.java)
                val call: Call<List<InAppMessage>> =
                    actJsonInterface?.getGeneralRequestJsonResponse(
                        currentRequest.headerMap, currentRequest.queryMap
                    )!!
                call.enqueue(object : Callback<List<InAppMessage>?> {
                    override fun onResponse(
                        call: Call<List<InAppMessage>?>,
                        response: Response<List<InAppMessage>?>
                    ) {
                        if (response.isSuccessful) {
                            applySuccessConditions(
                                response.headers(), response.raw().request.url.toString(),
                                model, Domain.IN_APP_NOTIFICATION_ACT_JSON, context
                            )
                            try {
                                val inAppMessages = response.body()
                                if (!inAppMessages.isNullOrEmpty()) {
                                    Log.i(
                                        LOG_TAG,
                                        "Successful InApp Request : " + call.request().url.toString()
                                    )
                                    val timer = Timer("InAppNotification Delay Timer", false)
                                    val timerTask = InAppNotificationTimer(
                                        null, 0,
                                        inAppMessages, currentRequest.parent, model, context
                                    )
                                    var delay: Long = 0
                                    if (timerTask.getMessage() != null) {
                                        delay =
                                            timerTask.getMessage()!!.mActionData!!.mWaitingTime!!.toLong() * 1000
                                    }
                                    timer.schedule(timerTask, delay)
                                    if (inAppMessages[0].mActionData?.mMsgType == "nps_with_numbers") {
                                        if (inAppMessages[0].mActionData?.mDisplayType == "inline") {
                                            val visilabsResponse = VisilabsResponse(
                                                null, JSONArray(Gson().toJson(inAppMessages)), null, null, null
                                            )
                                            currentRequest.visilabsCallback?.success(visilabsResponse)
                                        }
                                    }
                                } else {
                                    Log.w(
                                        LOG_TAG, "Empty or null inAppMessages list: " +
                                                response.raw().request.url.toString()
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.w(
                                    LOG_TAG, "Could not parse the response for the" +
                                            " request - not in the expected format : " +
                                            response.raw().request.url.toString()
                                )
                            }
                        } else {
                            applyFailConditions(call.request().url.toString(), model, context)
                            Log.w(
                                LOG_TAG,
                                "Fail InApp Request : " + call.request().url.toString()
                            )
                            Log.w(LOG_TAG, "Fail Request Response Code : " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<List<InAppMessage>?>, t: Throwable) {
                        applyFailConditions(call.request().url.toString(), model, context)
                        Log.w(LOG_TAG, "Fail InApp Request : " + call.request().url.toString())
                        Log.w(LOG_TAG, "Fail Request Message : " + t.message)
                    }
                })
            }


            Domain.IN_APP_ACTION_MOBILE -> {
                val mobileInterface = SApiClient.getClient(model.getRequestTimeoutInSecond())
                        ?.create(ApiMethods::class.java)
                val call: Call<ActionResponse> =
                        mobileInterface?.getActionRequestResponse(
                                currentRequest.headerMap, currentRequest.queryMap
                        )!!
                call.enqueue(object : Callback<ActionResponse> {
                    override fun onResponse(
                            call: Call<ActionResponse>,
                            response: Response<ActionResponse>
                    ) {
                        if (response.isSuccessful) {
                            try {
                                applySuccessConditions(
                                        response.headers(), response.raw().request.url.toString(),
                                        model, Domain.IN_APP_ACTION_MOBILE, context
                                )
                                val actionsResponse = response.body()
                                if (actionsResponse != null) {
                                    when {
                                        !actionsResponse.mSpinToWinList.isNullOrEmpty() -> {
                                            ActivityUtils.parentActivity = currentRequest.parent
                                            val intent =
                                                    Intent(
                                                            currentRequest.parent,
                                                            SpinToWinActivity::class.java
                                                    )
                                            val spinToWinModel: SpinToWin =
                                                    actionsResponse.mSpinToWinList!![0]
                                            intent.putExtra("spin-to-win-data", spinToWinModel)
                                            currentRequest.parent!!.startActivity(intent)
                                        }
                                        !actionsResponse.mScratchToWinList.isNullOrEmpty() -> {
                                            val intent =
                                                    Intent(
                                                            currentRequest.parent,
                                                            ScratchToWinActivity::class.java
                                                    )
                                            val scratchToWinModel: ScratchToWin =
                                                    actionsResponse.mScratchToWinList!![0]
                                            intent.putExtra(
                                                    "scratch-to-win-data",
                                                    scratchToWinModel
                                            )
                                            currentRequest.parent!!.startActivity(intent)
                                        }
                                        !actionsResponse.mShakeToWinList.isNullOrEmpty() -> {
                                            ActivityUtils.parentActivity = currentRequest.parent
                                            val intent =
                                                Intent(
                                                    currentRequest.parent,
                                                    ShakeToWinActivity::class.java
                                                )
                                            val shakeToWinModel: ShakeToWin =
                                                actionsResponse.mShakeToWinList!![0]
                                            intent.putExtra(
                                                "shake-to-win-data",
                                                shakeToWinModel
                                            )
                                            currentRequest.parent!!.startActivity(intent)
                                        }
                                        !actionsResponse.mMailSubscriptionForm.isNullOrEmpty() -> {

                                            if(!actionsResponse.mMailSubscriptionForm!![0].actiondata!!.taTemplate.isNullOrEmpty()
                                                && actionsResponse.mMailSubscriptionForm!![0].actiondata!!.taTemplate == "customizable") {
                                                val mailSubscriptionFormHalfFragment = MailSubscriptionFormHalfFragment.newInstance(
                                                    actionsResponse.mMailSubscriptionForm!![0].actiondata, actionsResponse.mMailSubscriptionForm!![0].actid!!
                                                )

                                                val transaction : FragmentTransaction = (currentRequest.parent!! as FragmentActivity).supportFragmentManager.beginTransaction()
                                                transaction.replace(android.R.id.content, mailSubscriptionFormHalfFragment)
                                                transaction.commit()
                                            } else {
                                                InAppManager(
                                                    model.getCookieId()!!,
                                                    model.getDataSource()
                                                ).showMailSubscriptionForm(
                                                    actionsResponse.mMailSubscriptionForm!![0],
                                                    currentRequest.parent!!
                                                )
                                            }
                                        }
                                        !actionsResponse.mProductStatNotifierList.isNullOrEmpty() -> {
                                            val socialProofFragment: SocialProofFragment = SocialProofFragment.newInstance(actionsResponse.mProductStatNotifierList!![0])

                                            val transaction : FragmentTransaction= (currentRequest.parent!! as FragmentActivity).supportFragmentManager.beginTransaction()
                                            transaction.replace(android.R.id.content, socialProofFragment)
                                            transaction.commit()
                                        }
                                        !actionsResponse.mDrawer.isNullOrEmpty() -> {
                                            val inAppNotificationFragment = InAppNotificationFragment.newInstance(
                                                actionsResponse.mDrawer!![0]
                                            )

                                            val transaction : FragmentTransaction= (currentRequest.parent!! as FragmentActivity).supportFragmentManager.beginTransaction()
                                            transaction.replace(android.R.id.content, inAppNotificationFragment)
                                            transaction.commit()
                                        }
                                        !actionsResponse.mGiftRain.isNullOrEmpty() -> {
                                            ActivityUtils.parentActivity = currentRequest.parent
                                            val intent =
                                                Intent(
                                                    currentRequest.parent,
                                                    GiftCatchActivity::class.java
                                                )
                                            val giftRainModel: GiftRain =
                                                actionsResponse.mGiftRain!![0]
                                            intent.putExtra("gift-rain-data", giftRainModel)
                                            currentRequest.parent!!.startActivity(intent)
                                        }
                                        !actionsResponse.mGiftBox.isNullOrEmpty() -> {
                                            ActivityUtils.parentActivity = currentRequest.parent
                                            val intent =
                                                Intent(
                                                    currentRequest.parent,
                                                    GiftBoxActivity::class.java
                                                )
                                            val giftBoxModel: GiftBox =
                                                actionsResponse.mGiftBox!![0]
                                            intent.putExtra("gift-box-data", giftBoxModel)
                                            currentRequest.parent!!.startActivity(intent)
                                        }
                                        !actionsResponse.mFindToWin.isNullOrEmpty() -> {
                                            ActivityUtils.parentActivity = currentRequest.parent
                                            val intent =
                                                Intent(
                                                    currentRequest.parent,
                                                    FindToWinActivity::class.java
                                                )
                                            val findToWinModel: FindToWin =
                                                actionsResponse.mFindToWin!![0]
                                            intent.putExtra("find-to-win-data", findToWinModel)
                                            currentRequest.parent!!.startActivity(intent)
                                        }
                                        !actionsResponse.mAppBanner.isNullOrEmpty() -> {
                                            val visilabsResponse = VisilabsResponse(
                                                JSONObject(Gson().toJson(actionsResponse.mAppBanner!![0])),
                                                null,
                                                null,
                                                null,
                                                null
                                            )
                                            currentRequest.visilabsCallback?.success(visilabsResponse)
                                        }
                                        else -> {
                                            Log.e(
                                                    LOG_TAG,
                                                    "Response is null : " + response.raw().request.url.toString()
                                            )
                                            val visilabsResponse = VisilabsResponse(
                                                null,
                                                null,
                                                "Response is empty",
                                                null,
                                                "Response is empty"
                                            )
                                            currentRequest.visilabsCallback?.fail(visilabsResponse)
                                        }
                                    }
                                } else {
                                    Log.e(
                                            LOG_TAG,
                                            "Response is null : " + response.raw().request.url.toString()
                                    )
                                    val visilabsResponse = VisilabsResponse(
                                        null,
                                        null,
                                        "Response is empty",
                                        null,
                                        "Response is empty"
                                    )
                                    currentRequest.visilabsCallback?.fail(visilabsResponse)
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                                Log.e(
                                        LOG_TAG,
                                        "Could not parse the response for the request : " + response.raw().request.url.toString()
                                )
                                Log.e(
                                        LOG_TAG,
                                        "Fail Request : " + call.request().url.toString()
                                )
                                Log.e(
                                        LOG_TAG,
                                        "Fail Request Message : The response is not in the correct format"
                                )
                                val visilabsResponse = VisilabsResponse(
                                    null,
                                    null,
                                    "Fail Request Message : The response is not in the correct format",
                                    null,
                                    "Fail Request Message : The response is not in the correct format"
                                )
                                currentRequest.visilabsCallback?.fail(visilabsResponse)
                            }
                        } else {
                            applyFailConditions(call.request().url.toString(), model, context)
                            Log.w(
                                    LOG_TAG,
                                    "Fail InApp Request : " + call.request().url.toString()
                            )
                            Log.w(LOG_TAG, "Fail Request Response Code : " + response.code())
                            val visilabsResponse = VisilabsResponse(
                                null,
                                null,
                                "Fail InApp Request",
                                null,
                                "Fail InApp Request"
                            )
                            currentRequest.visilabsCallback?.fail(visilabsResponse)
                        }
                    }

                    override fun onFailure(call: Call<ActionResponse>, t: Throwable) {
                        applyFailConditions(call.request().url.toString(), model, context)
                        Log.e(
                                LOG_TAG,
                                "Fail Request Message : " + t.message
                        )
                        val visilabsResponse = VisilabsResponse(
                            null,
                            null,
                            "Fail Request",
                            null,
                            "Fail Request"
                        )
                        currentRequest.visilabsCallback?.fail(visilabsResponse)
                    }
                })
            }

            Domain.IN_APP_SPIN_TO_WIN_PROMO_CODE -> {
                val sInterface = SApiClient.getClient(model.getRequestTimeoutInSecond())
                        ?.create(ApiMethods::class.java)
                val call: Call<ResponseBody> = sInterface!!.getPromotionCodeRequestJsonResponse(
                        currentRequest.headerMap,
                        currentRequest.queryMap
                )
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                            call: Call<ResponseBody?>?,
                            response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            applySuccessConditions(
                                    response.headers(), response.raw().request.url.toString(),
                                    model, Domain.IN_APP_SPIN_TO_WIN_PROMO_CODE, context
                            )
                            var rawJsonResponse = ""
                            try {
                                rawJsonResponse = response.body()!!.string()
                                if (rawJsonResponse != "") {
                                    val jsonResponse = JSONObject(rawJsonResponse)
                                    if (jsonResponse.getBoolean("success") && !jsonResponse.getString("promocode")
                                                    .equals(
                                                            ""
                                                    )
                                    ) {
                                        Log.i(
                                                LOG_TAG,
                                                "Success Request : " + response.raw().request.url.toString()
                                        )
                                        val visilabsResponse = VisilabsResponse(
                                                jsonResponse,
                                                null,
                                                null,
                                                null,
                                                null
                                        )
                                        currentRequest.visilabsCallback!!.success(visilabsResponse)
                                    } else {
                                        Log.e(
                                                LOG_TAG,
                                                "Empty promotion code - auth issue" + response.raw().request.url.toString()
                                        )
                                        val visilabsResponse = VisilabsResponse(
                                                null,
                                                null,
                                                "Empty promotion code - auth issue",
                                                null,
                                                "Empty promotion code - auth issue"
                                        )
                                        currentRequest.visilabsCallback!!.fail(visilabsResponse)
                                    }
                                } else {
                                    Log.e(
                                            LOG_TAG,
                                            "Empty response for the request : " + response.raw().request.url.toString()
                                    )
                                    val visilabsResponse = VisilabsResponse(
                                            null,
                                            null,
                                            "empty string",
                                            null,
                                            "empty string"
                                    )
                                    currentRequest.visilabsCallback!!.fail(visilabsResponse)
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                                Log.e(
                                        LOG_TAG,
                                        "Could not parse the response for the request : " + response.raw().request.url.toString()
                                )
                                val visilabsResponse = VisilabsResponse(
                                        null,
                                        null,
                                        rawJsonResponse,
                                        null,
                                        rawJsonResponse
                                )
                                currentRequest.visilabsCallback!!.fail(visilabsResponse)
                            }
                        } else {
                            applyFailConditions(call!!.request().url.toString(), model, context)
                            Log.w(
                                    LOG_TAG,
                                    "Fail InApp Request : " + call.request().url.toString()
                            )
                            Log.w(LOG_TAG, "Fail Request Response Code : " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>?, t: Throwable) {
                        applyFailConditions(call!!.request().url.toString(), model, context)
                        Log.e(
                                LOG_TAG,
                                "Fail Request Message : " + t.message
                        )
                        val visilabsResponse = VisilabsResponse(
                                null,
                                null,
                                t.message,
                                t,
                                t.message!!
                        )
                        currentRequest.visilabsCallback!!.fail(visilabsResponse)
                    }
                })
            }

            Domain.IN_APP_STORY_MOBILE -> {
                val sInterface = SApiClient.getClient(model.getRequestTimeoutInSecond())
                        ?.create(ApiMethods::class.java)
                val call: Call<ResponseBody> = sInterface!!.getGeneralActionRequestJsonResponse(
                        currentRequest.headerMap,
                        currentRequest.queryMap
                )
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                            call: Call<ResponseBody?>?,
                            response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            applySuccessConditions(
                                    response.headers(), response.raw().request.url.toString(),
                                    model, Domain.IN_APP_STORY_MOBILE, context
                            )
                            var rawJsonResponse = ""
                            try {
                                rawJsonResponse = response.body()!!.string()
                                if (rawJsonResponse != "") {
                                    val mainObject = JSONObject(rawJsonResponse)
                                    val storyArray: JSONArray? = mainObject.optJSONArray("Story")
                                    if (storyArray != null && storyArray.length() > 0) {
                                        val visilabsResponse = VisilabsResponse(
                                                JSONObject(rawJsonResponse),
                                                null,
                                                null,
                                                null,
                                                null
                                        )
                                        currentRequest.visilabsCallback!!.success(visilabsResponse)
                                    } else {
                                        Log.e(
                                                LOG_TAG,
                                                "Empty response for the request : " + response.raw().request.url.toString()
                                        )
                                        val visilabsResponse = VisilabsResponse(
                                                null,
                                                null,
                                                "empty string",
                                                null,
                                                "empty string"
                                        )
                                        currentRequest.visilabsCallback!!.fail(visilabsResponse)
                                    }
                                } else {
                                    Log.e(
                                            LOG_TAG,
                                            "Empty response for the request : " + response.raw().request.url.toString()
                                    )
                                    val visilabsResponse = VisilabsResponse(
                                            null,
                                            null,
                                            "empty string",
                                            null,
                                            "empty string"
                                    )
                                    currentRequest.visilabsCallback!!.fail(visilabsResponse)
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                                Log.e(
                                        LOG_TAG,
                                        "Could not parse the response for the request : " + response.raw().request.url.toString()
                                )
                                val visilabsResponse =
                                        VisilabsResponse(null, null, rawJsonResponse, null, rawJsonResponse)
                                currentRequest.visilabsCallback!!.fail(visilabsResponse)
                            }
                        } else {
                            applyFailConditions(call!!.request().url.toString(), model, context)
                            Log.w(
                                    LOG_TAG,
                                    "Fail InApp Request : " + call.request().url.toString()
                            )
                            Log.w(LOG_TAG, "Fail Request Response Code : " + response.code())
                            val visilabsResponse = VisilabsResponse(null, null,
                                    "Fail Request Response Code : " + response.code(), null,
                                    "Fail Request Response Code : " + response.code())
                            currentRequest.visilabsCallback!!.fail(visilabsResponse)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>?, t: Throwable) {
                        applyFailConditions(call!!.request().url.toString(), model, context)
                        Log.e(
                                LOG_TAG,
                                "Fail Request Message : " + t.message
                        )
                        val visilabsResponse = VisilabsResponse(null, null, t.message, t, t.message)
                        currentRequest.visilabsCallback!!.fail(visilabsResponse)
                    }
                })
            }

            Domain.IN_APP_RECOMMENDATION_JSON -> {
                val sInterface = SApiClient.getClient(model.getRequestTimeoutInSecond())
                        ?.create(ApiMethods::class.java)
                val call: Call<ResponseBody> = sInterface!!.getGeneralTargetRequestJsonResponse(
                        currentRequest.headerMap,
                        currentRequest.queryMap
                )
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                            call: Call<ResponseBody?>?,
                            response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            applySuccessConditions(
                                    response.headers(), response.raw().request.url.toString(),
                                    model, Domain.IN_APP_RECOMMENDATION_JSON, context
                            )
                            var rawJsonResponse = ""
                            try {
                                rawJsonResponse = response.body()!!.string()
                                if (rawJsonResponse != "") {
                                    val visilabsResponse = VisilabsResponse(
                                            RecommendationUtils.formJsonObject(rawJsonResponse),
                                            RecommendationUtils.formJsonArray(rawJsonResponse),
                                            null,
                                            null,
                                            null
                                    )
                                    currentRequest.visilabsCallback!!.success(visilabsResponse)
                                } else {
                                    Log.e(
                                            LOG_TAG,
                                            "Empty response for the request : " + response.raw().request.url.toString()
                                    )
                                    val visilabsResponse = VisilabsResponse(
                                            null,
                                            null,
                                            "empty string",
                                            null,
                                            "empty string"
                                    )
                                    currentRequest.visilabsCallback!!.fail(visilabsResponse)
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                                Log.e(
                                        LOG_TAG,
                                        "Could not parse the response for the request : " + response.raw().request.url.toString()
                                )
                                val visilabsResponse =
                                        VisilabsResponse(null, null, rawJsonResponse, null, rawJsonResponse)
                                currentRequest.visilabsCallback!!.fail(visilabsResponse)
                            }
                        } else {
                            applyFailConditions(call!!.request().url.toString(), model, context)
                            Log.w(
                                    LOG_TAG,
                                    "Fail InApp Request : " + call.request().url.toString()
                            )
                            Log.w(LOG_TAG, "Fail Request Response Code : " + response.code())
                            val visilabsResponse = VisilabsResponse(null, null,
                                    "Fail Request Response Code : " + response.code(), null,
                                    "Fail Request Response Code : " + response.code())
                            currentRequest.visilabsCallback!!.fail(visilabsResponse)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>?, t: Throwable) {
                        applyFailConditions(call!!.request().url.toString(), model, context)
                        Log.e(
                                LOG_TAG,
                                "Fail Request Message : " + t.message
                        )
                        val visilabsResponse = VisilabsResponse(null, null, t.message, t, t.message)
                        currentRequest.visilabsCallback!!.fail(visilabsResponse)
                    }
                })
            }

            Domain.IN_APP_FAVS_RESPONSE_MOBILE -> {
                val sInterface = SApiClient.getClient(model.getRequestTimeoutInSecond())
                        ?.create(ApiMethods::class.java)
                val call: Call<ResponseBody> = sInterface!!.getGeneralActionRequestJsonResponse(
                        currentRequest.headerMap,
                        currentRequest.queryMap
                )
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                            call: Call<ResponseBody?>?,
                            response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            applySuccessConditions(
                                    response.headers(), response.raw().request.url.toString(),
                                    model, Domain.IN_APP_FAVS_RESPONSE_MOBILE, context
                            )
                            var rawJsonResponse = ""
                            try {
                                rawJsonResponse = response.body()!!.string()
                                if (rawJsonResponse != "") {
                                    val visilabsResponse = VisilabsResponse(
                                            JSONObject(rawJsonResponse),
                                            null,
                                            null,
                                            null,
                                            null
                                    )
                                    currentRequest.visilabsCallback!!.success(visilabsResponse)
                                } else {
                                    Log.e(
                                            LOG_TAG,
                                            "Empty response for the request : " + response.raw().request.url.toString()
                                    )
                                    val visilabsResponse = VisilabsResponse(
                                            null,
                                            null,
                                            "empty string",
                                            null,
                                            "empty string"
                                    )
                                    currentRequest.visilabsCallback!!.fail(visilabsResponse)
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                                Log.e(
                                        LOG_TAG,
                                        "Could not parse the response for the request : " + response.raw().request.url.toString()
                                )
                                val visilabsResponse =
                                        VisilabsResponse(null, null, rawJsonResponse, null, rawJsonResponse)
                                currentRequest.visilabsCallback!!.fail(visilabsResponse)
                            }
                        } else {
                            applyFailConditions(call!!.request().url.toString(), model, context)
                            Log.w(
                                    LOG_TAG,
                                    "Fail InApp Request : " + call.request().url.toString()
                            )
                            Log.w(LOG_TAG, "Fail Request Response Code : " + response.code())
                            val visilabsResponse = VisilabsResponse(null, null,
                                    "Fail Request Response Code : " + response.code(), null,
                                    "Fail Request Response Code : " + response.code())
                            currentRequest.visilabsCallback!!.fail(visilabsResponse)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>?, t: Throwable) {
                        applyFailConditions(call!!.request().url.toString(), model, context)
                        Log.e(
                                LOG_TAG,
                                "Fail Request Message : " + t.message
                        )
                        val visilabsResponse = VisilabsResponse(null, null, t.message, t, t.message)
                        currentRequest.visilabsCallback!!.fail(visilabsResponse)
                    }
                })
            }

            Domain.GEOFENCE_GET_LIST -> {
                val sInterface = SApiClient.getClient(model.getRequestTimeoutInSecond())
                    ?.create(ApiMethods::class.java)
                val call: Call<List<GeofenceListResponse>> = sInterface!!.getGeofenceListRequestResponse(
                    currentRequest.headerMap,
                    currentRequest.queryMap
                )
                call.enqueue(object : Callback<List<GeofenceListResponse>> {
                    override fun onResponse(
                        call: Call<List<GeofenceListResponse>?>?,
                        response: Response<List<GeofenceListResponse>>
                    ) {
                        if (response.isSuccessful) {
                            applySuccessConditions(
                                response.headers(), response.raw().request.url.toString(),
                                model, Domain.GEOFENCE_GET_LIST, context
                            )
                            try {
                                val geofenceGetListResponse: List<GeofenceListResponse>? =
                                    response.body()
                                currentRequest.geofenceGetListCallback!!.success(
                                    geofenceGetListResponse,
                                    response.raw().request.url.toString()
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.e(
                                    LOG_TAG,
                                    "Could not parse the response for the request : " + response.raw().request.url.toString()
                                )
                                try {
                                    currentRequest.geofenceGetListCallback!!.fail(
                                        Throwable(response.body().toString()),
                                        call!!.request().url.toString()
                                    )
                                } catch (c: Exception) {
                                    c.printStackTrace()
                                    currentRequest.geofenceGetListCallback!!.fail(
                                        Throwable("The response is not in the correct format"),
                                        call!!.request().url.toString()
                                    )
                                }
                            }
                        } else {
                            applyFailConditions(call!!.request().url.toString(), model, context)
                            currentRequest.geofenceGetListCallback!!.fail(
                                Throwable("The response is not in the correct format"),
                                call.request().url.toString())
                        }
                    }

                    override fun onFailure(call: Call<List<GeofenceListResponse>>, t: Throwable) {
                        applyFailConditions(call.request().url.toString(), model, context)
                        currentRequest.geofenceGetListCallback!!.fail(t, call.request().url.toString())
                    }
                })
            }

            Domain.GEOFENCE_TRIGGER -> {
                val sInterface = SApiClient.getClient(model.getRequestTimeoutInSecond())
                    ?.create(ApiMethods::class.java)
                val call: Call<ResponseBody> = sInterface!!.getGeneralGeofenceRequestJsonResponse(
                    currentRequest.headerMap,
                    currentRequest.queryMap
                )
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            applySuccessConditions(
                                response.headers(), response.raw().request.url.toString(),
                                model, Domain.GEOFENCE_TRIGGER, context
                            )
                            val rawJsonResponse: String
                            try {
                                rawJsonResponse = response.body()!!.string()
                                if (rawJsonResponse != "") {
                                    Log.i(
                                        LOG_TAG,
                                        "Success Request : " + response.raw().request.url.toString()
                                    )
                                        if (rawJsonResponse == "ok" || rawJsonResponse == "\"ok\"") {
                                            Log.i(
                                                LOG_TAG,
                                                "Successful Request : Sent the info of Geofence trigger"
                                            )
                                        } else {
                                            Log.e(
                                                LOG_TAG,
                                                "Fail Request : Could not send the info of Geofence trigger"
                                            )
                                        }
                                } else {
                                    Log.e(
                                        LOG_TAG,
                                        "Empty response for the request : " + response.raw().request.url.toString()
                                    )
                                    Log.e(
                                        LOG_TAG,
                                        "Fail Request : Could not send the info of Geofence trigger"
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.e(
                                    LOG_TAG,
                                    "Could not parse the response for the request : " + response.raw().request.url.toString()
                                )
                                Log.e(
                                    LOG_TAG,
                                    "Fail Request : Could not send the info of Geofence trigger"
                                )
                            }
                        } else {
                            applyFailConditions(call!!.request().url.toString(), model, context)
                            Log.e(
                                LOG_TAG,
                                "Fail Request " + call.request().url.toString()
                            )
                            Log.e(
                                LOG_TAG,
                                "Fail Request : Could not send the info of Geofence trigger"
                            )
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        applyFailConditions(call.request().url.toString(), model, context)
                        Log.e(
                            LOG_TAG,
                            "Fail Request " + call.request().url.toString()
                        )
                        Log.e(
                            LOG_TAG,
                            "Fail Request : Could not send the info of Geofence trigger"
                        )
                    }
                })
            }
        }
    }

    fun sendSubscriptionRequest(context: Context, model: RelatedDigitalModel, counterId: Int,
                                callback: EuromessageCallback? = null) {
        val subscription = Subscription(context, model)

        val subscriptionInterface = SubscriptionApiClient.getClient(model.getRequestTimeoutInSecond())
            ?.create(ApiMethods::class.java)
        val call: Call<Void> = subscriptionInterface!!.saveSubscription(
            model.getUserAgent(),
            subscription
        )

        if(counterId != -1) {
            call.enqueue(object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>?,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        RetryCounterManager.clearCounter(counterId)
                        saveSubscription(context, model)
                        Log.i(
                           LOG_TAG,
                            "Sending the subscription is success"
                        )
                        callback?.success()
                    } else {
                        if (RetryCounterManager.getCounterValue(counterId) >= 3) {
                            RetryCounterManager.clearCounter(counterId)
                            Log.e(
                                LOG_TAG,
                                "Sending the subscription is failed after 3 attempts!!!"
                            )
                            call!!.cancel()
                            callback?.fail(response.message())
                        } else {
                            RetryCounterManager.increaseCounter(counterId)
                            sendSubscriptionRequest(context, model, counterId)
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if (RetryCounterManager.getCounterValue(counterId) >= 3) {
                        RetryCounterManager.clearCounter(counterId)
                        Log.e(
                            LOG_TAG,
                            "Sending the subscription is failed after 3 attempts!!!"
                        )
                        call.cancel()
                        t.printStackTrace()
                        callback?.fail(t.message)
                    } else {
                        RetryCounterManager.increaseCounter(counterId)
                        sendSubscriptionRequest(context, model, counterId)
                    }
                }
            })
        } else {
            call.enqueue(object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>?,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        RetryCounterManager.clearCounter(counterId)
                        saveSubscription(context, model)
                        Log.i(
                            LOG_TAG,
                            "Sending the subscription is success"
                        )
                        callback?.success()
                    } else {
                        if (RetryCounterManager.getCounterValue(counterId) >= 3) {
                            RetryCounterManager.clearCounter(counterId)
                            Log.e(
                                LOG_TAG,
                                "Sending the subscription is failed after 3 attempts!!!"
                            )
                            call!!.cancel()
                            callback?.fail(response.message())
                        } else {
                            RetryCounterManager.increaseCounter(counterId)
                            sendSubscriptionRequest(context, model, counterId)
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if (RetryCounterManager.getCounterValue(counterId) >= 3) {
                        RetryCounterManager.clearCounter(counterId)
                        Log.e(
                            LOG_TAG,
                            "Sending the subscription is failed after 3 attempts!!!"
                        )
                        call.cancel()
                        t.printStackTrace()
                        callback?.fail(t.message)
                    } else {
                        RetryCounterManager.increaseCounter(counterId)
                        sendSubscriptionRequest(context, model, counterId)
                    }
                }
            })
        }
    }

    fun sendRetentionRequest(context: Context, retention: Retention, counterId: Int) {
        val retentionInterface = RetentionApiClient.getClient(RelatedDigital.
        getRelatedDigitalModel(context).getRequestTimeoutInSecond())
            ?.create(ApiMethods::class.java)
        val call: Call<Void> = retentionInterface!!.report(
            RelatedDigital.
            getRelatedDigitalModel(context).getUserAgent(),
            retention
        )

        if(counterId != -1) {
            call.enqueue(object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>?,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        RetryCounterManager.clearCounter(counterId)
                        Log.i(
                            LOG_TAG,
                            "Sending the deliver request is success"
                        )
                    } else {
                        if (RetryCounterManager.getCounterValue(counterId) >= 3) {
                            RetryCounterManager.clearCounter(counterId)
                            Log.e(
                                LOG_TAG,
                                "Sending the deliver request is failed after 3 attempts!!!"
                            )
                            call!!.cancel()
                        } else {
                            RetryCounterManager.increaseCounter(counterId)
                            sendRetentionRequest(context, retention, counterId)
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if (RetryCounterManager.getCounterValue(counterId) >= 3) {
                        RetryCounterManager.clearCounter(counterId)
                        Log.e(
                            LOG_TAG,
                            "Sending the deliver request is failed after 3 attempts!!!"
                        )
                        call.cancel()
                        t.printStackTrace()
                    } else {
                        RetryCounterManager.increaseCounter(counterId)
                        sendRetentionRequest(context, retention, counterId)
                    }
                }
            })
        } else {
            call.enqueue(object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>?,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        RetryCounterManager.clearCounter(counterId)
                        Log.i(
                            LOG_TAG,
                            "Sending the deliver request is success"
                        )
                    } else {
                        if (RetryCounterManager.getCounterValue(counterId) >= 3) {
                            RetryCounterManager.clearCounter(counterId)
                            Log.e(
                                LOG_TAG,
                                "Sending the deliver request is failed after 3 attempts!!!"
                            )
                            call!!.cancel()
                        } else {
                            RetryCounterManager.increaseCounter(counterId)
                            sendRetentionRequest(context, retention, counterId)
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if (RetryCounterManager.getCounterValue(counterId) >= 3) {
                        RetryCounterManager.clearCounter(counterId)
                        Log.e(
                            LOG_TAG,
                            "Sending the deliver request is failed after 3 attempts!!!"
                        )
                        call.cancel()
                        t.printStackTrace()
                    } else {
                        RetryCounterManager.increaseCounter(counterId)
                        sendRetentionRequest(context, retention, counterId)
                    }
                }
            })
        }
    }

    private fun saveSubscription(context: Context, model: RelatedDigitalModel) {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val dateNow = dateFormat.format(Calendar.getInstance().time)
        SharedPref.writeString(context, Constants.LAST_SUBS_DATE_KEY, dateNow)
        SharedPref.writeString(context, Constants.LAST_SUBS_KEY, Gson().toJson(model))
    }

    private fun parseAndSetResponseHeaders(
            responseHeaders: Headers, type: Domain,
            model: RelatedDigitalModel
    ) {
        val names = responseHeaders.names()
        if (names.isNotEmpty()) {
            val cookies = ArrayList<String>()
            for (i in names.indices) {
                val str = responseHeaders.name(i)
                if (str == "set-cookie" || str == "cookie") {
                    cookies.add(responseHeaders.value(i))
                }
            }
            if (cookies.size > 0) {
                for (cookie in cookies) {
                    val fields = cookie.split(";").toTypedArray()
                    if (fields[0].lowercase(Locale.ROOT).contains(
                            Constants.LOAD_BALANCE_PREFIX.lowercase(
                                Locale.ROOT
                            )
                            )) {
                        val cookieKeyValue = fields[0].split("=").toTypedArray()
                        if (cookieKeyValue.size > 1) {
                            val cookieKey = cookieKeyValue[0]
                            val cookieValue = cookieKeyValue[1]
                            if (type == Domain.LOG_LOGGER && model.getCookie() != null) {
                                model.getCookie()!!.setLoggerCookieKey(cookieKey)
                                model.getCookie()!!.setLoggerCookieValue(cookieValue)
                            } else if (type == Domain.LOG_REAL_TIME && model.getCookie() != null) {
                                model.getCookie()!!.setRealTimeCookieKey(cookieKey)
                                model.getCookie()!!.setRealTimeCookieValue(cookieValue)
                            }
                        }
                    }
                    if (fields[0].lowercase(Locale.ROOT).contains(
                            Constants.OM_3_KEY.lowercase(
                                Locale.ROOT
                            )
                            )) {
                        val cookieKeyValue = fields[0].split("=").toTypedArray()
                        if (cookieKeyValue.size > 1 || model.getCookie() != null) {
                            val cookieValue = cookieKeyValue[1]
                            if (type == Domain.LOG_LOGGER && model.getCookie() != null) {
                                model.getCookie()!!.setLoggerOM3rdCookieValue(cookieValue)
                            } else if (type == Domain.LOG_REAL_TIME && model.getCookie() != null) {
                                model.getCookie()!!.setRealOM3rdTimeCookieValue(cookieValue)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun removeFromQueue() {
        requestQueue.removeAt(0)
    }

    private fun applySuccessConditions(
            headers: Headers, url: String, model: RelatedDigitalModel,
            type: Domain, context: Context
    ) {
        Log.i(LOG_TAG, "Successful Request : $url")
        parseAndSetResponseHeaders(headers, type, model)
        removeFromQueue()
        isSendingARequest = false
        retryCounter = 0
        send(model, context)
    }

    private fun applyFailConditions(url: String, model: RelatedDigitalModel, context: Context) {
        Log.e(LOG_TAG, "Fail Request : $url")
        isSendingARequest = false
        retryCounter++
        if (retryCounter >= 3) {
            Log.w(LOG_TAG, "Could not send the request after 3 attempts!!!")
            removeFromQueue()
            retryCounter = 0
        }
        send(model, context)
    }
}