package com.relateddigital.relateddigital_android.network

import android.content.Context
import android.util.Log
import com.relateddigital.relateddigital_android.api.ApiMethods
import com.relateddigital.relateddigital_android.api.LoggerApiClient
import com.relateddigital.relateddigital_android.api.RealTimeApiClient
import com.relateddigital.relateddigital_android.api.SApiClient
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Domain
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.model.RelatedDigitalModel
import com.relateddigital.relateddigital_android.model.Request
import com.relateddigital.relateddigital_android.util.InAppNotificationTimer
import okhttp3.Headers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

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
                            applySuccessConditions(response, model, Domain.LOG_LOGGER, context)
                        } else {
                            applyFailConditions(call, model, context)
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        applyFailConditions(call, model, context)
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
                            applySuccessConditions(response, model, Domain.LOG_REAL_TIME, context)
                        } else {
                            applyFailConditions(call, model, context)
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        applyFailConditions(call, model, context)
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
                            applySuccessConditions(response, model, Domain.LOG_S, context)
                        } else {
                            applyFailConditions(call, model, context)
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        applyFailConditions(call, model, context)
                    }
                })
            }

            Domain.IN_APP_NOTIFICATION_ACT_JSON -> {
                try {
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
                                try {
                                    val inAppMessages = response.body()
                                    Log.i(
                                            LOG_TAG,
                                            "Successful InApp Request : " + call.request().url.toString()
                                    )
                                    val timer = Timer("InAppNotification Delay Timer", false)
                                    val timerTask = InAppNotificationTimer(null, 0,
                                            inAppMessages, currentRequest.parent, model, context)
                                    var delay: Long = 0
                                    if(timerTask.getMessage() != null) {
                                        delay = timerTask.getMessage()!!.mActionData!!.mWaitingTime!!.toLong() * 1000
                                    }
                                    timer.schedule(timerTask, delay)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Log.w(LOG_TAG, "Could not parse the response for the" +
                                            " request - not in the expected format : " +
                                            response.raw().request.url.toString()
                                    )
                                }
                            } else {
                                Log.w(
                                        LOG_TAG,
                                        "Fail InApp Request : " + call.request().url.toString()
                                )
                                Log.w(LOG_TAG, "Fail Request Response Code : " + response.code())
                            }
                        }

                        override fun onFailure(call: Call<List<InAppMessage>?>, t: Throwable) {
                            Log.w(LOG_TAG, "Fail InApp Request : " + call.request().url.toString())
                            Log.w(LOG_TAG, "Fail Request Message : " + t.message)
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(LOG_TAG, "Could not parse the response!")
                }
            }
        }
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
                    if (fields[0].toLowerCase(Locale.ROOT).contains(
                                    Constants.LOAD_BALANCE_PREFIX.toLowerCase(
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
                    if (fields[0].toLowerCase(Locale.ROOT).contains(
                                    Constants.OM_3_KEY.toLowerCase(
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
            response: Response<Void?>, model: RelatedDigitalModel,
            type: Domain, context: Context
    ) {
        Log.i(LOG_TAG, "Successful Request : " + response.raw().request.url.toString())
        parseAndSetResponseHeaders(response.headers(), type, model)
        removeFromQueue()
        isSendingARequest = false
        retryCounter = 0
        send(model, context)
    }

    private fun applyFailConditions(call: Call<Void?>, model: RelatedDigitalModel, context: Context) {
        Log.w(LOG_TAG, "Fail Request : " + call.request().url.toString())
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