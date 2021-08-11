package com.relateddigital.relateddigital_android.network

import android.app.Activity
import android.content.Context
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.LoadBalanceCookie
import com.relateddigital.relateddigital_android.model.RelatedDigitalModel
import com.relateddigital.relateddigital_android.util.GoogleUtils
import com.relateddigital.relateddigital_android.util.SharedPref
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RequestHandler {
    private var mNrv = 0
    private var mPviv = 0
    private var mTvc = 0
    private var mLvt: String? = null
    private val loadBalanceCookie = LoadBalanceCookie()

    fun createRequest(context: Context, model: RelatedDigitalModel?, pageName: String,
                      properties: HashMap<String, String>?, parent: Activity? = null) {
        updateSessionParameters(context, pageName)
        if (properties != null) {
            if (properties.containsKey(Constants.COOKIE_ID_REQUEST_KEY)) {
                model!!.setCookieId(context, properties[Constants.COOKIE_ID_REQUEST_KEY]!!)
                properties.remove(Constants.COOKIE_ID_REQUEST_KEY)
            }
            if (properties.containsKey(Constants.EXVISITOR_ID_REQUEST_KEY)) {
                if (model!!.getExVisitorId().isNotEmpty() && model.getExVisitorId() !=
                        properties[Constants.EXVISITOR_ID_REQUEST_KEY]) {
                    model.setCookieId(context, null)
                }
                model.setExVisitorId(context, properties[Constants.EXVISITOR_ID_REQUEST_KEY]!!)
                properties.remove(Constants.EXVISITOR_ID_REQUEST_KEY)
            }
            if (properties.containsKey(Constants.TOKEN_ID_REQUEST_KEY)) {
                if (properties[Constants.TOKEN_ID_REQUEST_KEY] != null) {
                    model!!.setToken(context, properties[Constants.TOKEN_ID_REQUEST_KEY]!!)
                }
                properties.remove(Constants.TOKEN_ID_REQUEST_KEY)
            }
            if (properties.containsKey(Constants.APP_ID_REQUEST_KEY)) {
                if (properties[Constants.APP_ID_REQUEST_KEY] != null) {
                    if(GoogleUtils.checkPlayService(context)) {
                        model!!.setGoogleAppAlias(context, properties[Constants.APP_ID_REQUEST_KEY]!!)
                    } else{
                        model!!.setHuaweiAppAlias(context, properties[Constants.APP_ID_REQUEST_KEY]!!)
                    }
                }
                properties.remove(Constants.APP_ID_REQUEST_KEY)
            }

            val timeOfEvent = System.currentTimeMillis() / 1000

            val queryMap = HashMap<String, String>()
            queryMap[Constants.ORGANIZATION_ID_REQUEST_KEY] = model!!.getOrganizationId()
            queryMap[Constants.SITE_ID_REQUEST_KEY] = model.getProfileId()
            queryMap[Constants.DATE_REQUEST_KEY] = timeOfEvent.toString()
            queryMap[Constants.URI_REQUEST_KEY] = pageName
            queryMap[Constants.COOKIE_ID_REQUEST_KEY] = model.getCookieId()!!
            queryMap[Constants.CHANNEL_REQUEST_KEY] = model.getChannel()
            queryMap[Constants.MAPPL_REQUEST_KEY] = "true"
            queryMap[Constants.SDK_VERSION_REQUEST_KEY] = model.getSdkVersion()
            queryMap[Constants.NRV_REQUEST_KEY] = mNrv.toString()
            queryMap[Constants.PVIV_REQUEST_KEY] = mPviv.toString()
            queryMap[Constants.TVC_REQUEST_KEY] = mTvc.toString()
            queryMap[Constants.LVT_REQUEST_KEY] = mLvt.toString()
        }
    }

    private fun updateSessionParameters(context: Context, pageName: String) {
        val dateNow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val lastEventTime: String = SharedPref.readString(context,
                Constants.LAST_EVENT_TIME_KEY, "")
        if (lastEventTime.isEmpty()) {
            mNrv = 1
            mPviv = 1
            mTvc = 1
            mLvt = dateNow
            SharedPref.writeString(context, Constants.TVC_KEY, "1")
            SharedPref.writeString(context, Constants.LAST_EVENT_TIME_KEY, dateNow)
        } else {
            if (isPreviousSessionOver(lastEventTime, dateNow)) {
                mPviv = 1
                SharedPref.writeString(context, Constants.PVIV_KEY, mPviv.toString())
                val prevTvc: Int = SharedPref.readString(context, Constants.TVC_KEY,
                        "1").toInt()
                mTvc = prevTvc + 1
                SharedPref.writeString(context, Constants.TVC_KEY, mTvc.toString())
                if (pageName != "/OM_evt.gif") {
                    mLvt = dateNow
                    SharedPref.writeString(context, Constants.LAST_EVENT_TIME_KEY, dateNow)
                } else {
                    mLvt = lastEventTime
                }
            } else {
                if (pageName != "/OM_evt.gif") {
                    val prevPviv: Int = SharedPref.readString(context,
                            Constants.PVIV_KEY, "1").toInt()
                    mPviv = prevPviv + 1
                    SharedPref.writeString(context, Constants.PVIV_KEY, mPviv.toString())
                } else {
                    mPviv = SharedPref.readString(context, Constants.PVIV_KEY,
                            "1").toInt()
                }
                mTvc = SharedPref.readString(context, Constants.TVC_KEY, "1").toInt()
                mLvt = lastEventTime
            }
            mNrv = if (mTvc > 1) {
                0
            } else {
                1
            }
        }
    }

    private fun isPreviousSessionOver(lastEventTime: String, dateNow: String): Boolean {
        val res: Boolean
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        res = try {
            val previousEventDate = simpleDateFormat.parse(lastEventTime)
            val currentEventDate = simpleDateFormat.parse(dateNow)
            val differenceInMs = currentEventDate!!.time - previousEventDate!!.time
            differenceInMs > 1800000 // 30 mins
        } catch (e: ParseException) {
            e.printStackTrace()
            false
        }
        return res
    }
}