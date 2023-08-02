package com.relateddigital.relateddigital_android.api

import com.relateddigital.relateddigital_android.model.*
import com.relateddigital.relateddigital_android.model.Retention
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiMethods {
    //Methods to send info to the server
    @GET("{dataSource}/om.gif")
    fun sendToLogger(
            @Path("dataSource") dataSource: String,
            @HeaderMap headers: Map<String, String>,
            @QueryMap queryParameters: Map<String, String>
    ): Call<Void>

    @GET("{dataSource}/om.gif")
    fun sendToRealTime(
            @Path("dataSource") dataSource: String,
            @HeaderMap headers: Map<String, String>,
            @QueryMap queryParameters: Map<String, String>
    ): Call<Void>

    @GET("subsjson")
    fun sendSubsJsonRequestToS(
            @HeaderMap headers: Map<String, String>,
            @QueryMap queryParameters: Map<String, String>
    ): Call<Void>

    //Methods to get InApp-type responses
    @GET("actjson")
    fun getGeneralRequestJsonResponse(
            @HeaderMap headers: Map<String, String>,
            @QueryMap queryParameters: Map<String, String>
    ): Call<List<InAppMessage>>

    @GET("actjson")
    fun getInAppRequestResponse(
            @HeaderMap headers: Map<String, String>,
            @QueryMap queryParameters: Map<String, String>
    ): Call<List<InAppMessage>>

    @GET("actjson")
    fun getNpsWithNumbersRequestResponse(
        @HeaderMap headers: Map<String, String>,
        @QueryMap queryParameters: Map<String, String>
    ): Call<List<InAppMessage>>

    //Methods to get Story, MailSubsForm, FAvs-type responses
    @GET("mobile")
    fun getGeneralActionRequestJsonResponse(
            @HeaderMap headers: Map<String, String>,
            @QueryMap queryParameters: Map<String, String>
    ): Call<ResponseBody>

    @GET("mobile")
    fun getActionRequestResponse(
            @HeaderMap headers: Map<String, String>,
            @QueryMap queryParameters: Map<String, String>
    ): Call<ActionResponse>

    @GET("mobile")
    fun getFavsRequestResponse(
            @HeaderMap headers: Map<String, String>,
            @QueryMap queryParameters: Map<String, String>
    ): Call<FavsResponse>

    //Methods to get target responses
    @GET("json")
    fun getGeneralTargetRequestJsonResponse(
            @HeaderMap headers: Map<String, String>,
            @QueryMap queryParameters: Map<String, String>
    ): Call<ResponseBody>

    //Method to get promotion code
    @GET("promotion")
    fun getPromotionCodeRequestJsonResponse(
            @HeaderMap headers: Map<String, String>,
            @QueryMap queryParameters: Map<String, String>
    ): Call<ResponseBody>

    //Methods to get Geofence List and to send info of a geofence trigger
    @GET("geojson")
    fun getGeneralGeofenceRequestJsonResponse(
            @HeaderMap headers: Map<String, String>,
            @QueryMap queryParameters: Map<String, String>
    ): Call<ResponseBody>

    @GET("geojson")
    fun getGeofenceListRequestResponse(
            @HeaderMap headers: Map<String, String>,
            @QueryMap queryParameters: Map<String, String>
    ): Call<List<GeofenceListResponse>>

    //Methods for retention and subscription requests
    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("/subscription")
    fun saveSubscription(
            @Header("User-Agent") userAgent: String,
            @Body subscription: Subscription
    ): Call<Void>

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("/retention")
    fun report(@Header("User-Agent") userAgent: String, @Body retention: Retention): Call<Void>

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("/log/mobileSdk")
    fun sendLogToGraylog(@Body graylogModel: GraylogModel?): Call<Void>

    //Methods for js files -- mbls domain
    @GET("/spin_to_win.js")
    fun getSpinToWinJsFile(
        @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>

    @GET("/find_to_win.js")
    fun getFindToWinJsFile(
        @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>

    @GET("/swiping.js")
    fun getSwipingJsFile(
        @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>

    @GET("/giftbox.js")
    fun getGiftBoxJsFile(
        @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>

    @GET("/slot_machine.js")
    fun getSlotMachineJsFile(
        @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>

    @GET("/gift_catch.js")
    fun getGiftCatchJsFile(
        @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>


    //Methods for remote config
    @GET("rc.json")
    fun getRemoteConfig(
            @HeaderMap headers: Map<String, String>
    ): Call<List<String>>
}