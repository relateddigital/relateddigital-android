package com.relateddigital.relateddigital_android.network

import android.util.Log
import com.relateddigital.relateddigital_android.api.ApiMethods
import com.relateddigital.relateddigital_android.api.LoggerApiClient
import com.relateddigital.relateddigital_android.api.RealTimeApiClient
import com.relateddigital.relateddigital_android.api.SApiClient
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Domain
import com.relateddigital.relateddigital_android.model.RelatedDigitalModel
import com.relateddigital.relateddigital_android.model.Request
import okhttp3.Headers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RequestSender {
    private const val LOG_TAG = "Related Digital"
    private val requestQueue = ArrayList<Request>()
    private var isSendingARequest = false
    fun addToQueue(request: Request, model: RelatedDigitalModel) {
        requestQueue.add(request)
        send(model)
    }

    private fun send(model: RelatedDigitalModel) {
        // TODO after a successful response get the cookie from the header and set it to the model
        if(isSendingARequest || requestQueue.isEmpty()) {
            return
        }

        isSendingARequest = true

        val currentRequest = requestQueue[0]

        when(currentRequest.domain) {
            Domain.LOGGER -> {
                val loggerApiInterface = LoggerApiClient.getClient(model.getRequestTimeoutInSecond())
                        ?.create(ApiMethods::class.java)
                val call: Call<Void> = loggerApiInterface?.sendToLogger(model.getDataSource(),
                        currentRequest.headerMap, currentRequest.queryMap)!!
                call.enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        Log.i(LOG_TAG, "Successful Request : " + response.raw().request.url.toString())
                        parseAndSetResponseHeaders(response.headers(), "logger", model)
                        removeFromQueue()
                        isSendingARequest = false
                        send(model)
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        Log.i(LOG_TAG, "Fail Request : " + call.request().url.toString())
                        // TODO retry here
                    }
                })
            }

            Domain.REAL_TIME -> {
                val realTimeApiInterface = RealTimeApiClient.getClient(model.getRequestTimeoutInSecond())
                        ?.create(ApiMethods::class.java)
                val call: Call<Void> = realTimeApiInterface?.sendToRealTime(model.getDataSource(),
                        currentRequest.headerMap, currentRequest.queryMap)!!
                call.enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        Log.i(LOG_TAG, "Successful Request : " + response.raw().request.url.toString())
                        parseAndSetResponseHeaders(response.headers(), "realTime", model)
                        removeFromQueue()
                        isSendingARequest = false
                        send(model)
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        Log.i(LOG_TAG, "Fail Request : " + call.request().url.toString())
                        // TODO retry here
                    }
                })
            }

            Domain.S -> {
                val sApiInterface = SApiClient.getClient(model.getRequestTimeoutInSecond())
                        ?.create(ApiMethods::class.java)
                val call: Call<Void> = sApiInterface?.sendSubsJsonRequestToS(
                        currentRequest.headerMap, currentRequest.queryMap)!!
                call.enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        Log.i(LOG_TAG, "Successful Request : " + response.raw().request.url.toString())
                        parseAndSetResponseHeaders(response.headers(), "realTime", model)
                        removeFromQueue()
                        isSendingARequest = false
                        send(model)
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        Log.i(LOG_TAG, "Fail Request : " + call.request().url.toString())
                        // TODO retry here
                    }
                })
            }
        }
    }

    private fun parseAndSetResponseHeaders(responseHeaders: Headers, type: String,
                                           model: RelatedDigitalModel) {
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
                    if (fields[0].toLowerCase().contains(Constants.LOAD_BALANCE_PREFIX.toLowerCase())) {
                        val cookieKeyValue = fields[0].split("=").toTypedArray()
                        if (cookieKeyValue.size > 1) {
                            val cookieKey = cookieKeyValue[0]
                            val cookieValue = cookieKeyValue[1]
                            if (type == "logger" && model.getCookie() != null) {
                                model.getCookie()!!.setLoggerCookieKey(cookieKey)
                                model.getCookie()!!.setLoggerCookieValue(cookieValue)
                            } else if (type == "realTime" && model.getCookie() != null) {
                                model.getCookie()!!.setRealTimeCookieKey(cookieKey)
                                model.getCookie()!!.setRealTimeCookieValue(cookieValue)
                            }
                        }
                    }
                    if (fields[0].toLowerCase().contains(Constants.OM_3_KEY.toLowerCase())) {
                        val cookieKeyValue = fields[0].split("=").toTypedArray()
                        if (cookieKeyValue.size > 1 || model.getCookie() != null) {
                            val cookieValue = cookieKeyValue[1]
                            if (type == "logger" && model.getCookie() != null) {
                                model.getCookie()!!.setLoggerOM3rdCookieValue(cookieValue)
                            } else if (type == "realTime" && model.getCookie() != null) {
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
}