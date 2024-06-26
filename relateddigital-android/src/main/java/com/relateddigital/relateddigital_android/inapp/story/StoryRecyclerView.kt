package com.relateddigital.relateddigital_android.inapp.story

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.inapp.VisilabsCallback
import com.relateddigital.relateddigital_android.inapp.VisilabsResponse
import com.relateddigital.relateddigital_android.model.StoryLookingBannerResponse
import com.relateddigital.relateddigital_android.model.StorySkinBasedResponse
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.StoryActionRequest

class StoryRecyclerView : RecyclerView {
    private var mContext: Context = context
    var mStoryItemClickListener: StoryItemClickListener? = null

    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {}

    fun setStoryAction(context: Context, storyItemClickListener: StoryItemClickListener?) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(TAG, "Too much server load, ignoring the request!")
            return
        }

        if(RelatedDigital.getRelatedDigitalModel(context).getIsInAppNotificationEnabled()) {
            mStoryItemClickListener = storyItemClickListener
            val parameters = HashMap<String, String>()
            parameters[Constants.REQUEST_ACTION_TYPE_KEY] = Constants.STORY_ACTION_TYPE_VAL
            StoryActionRequest.createStoryActionRequest(
                mContext,
                getStoryCallback(context, null),
                parameters
            )
        }
    }

    fun setStoryActionId(context: Context, actionId: String, storyItemClickListener: StoryItemClickListener?) {
        if (RelatedDigital.isBlocked(mContext)) {
            Log.w(TAG, "Too much server load, ignoring the request!")
            return
        }
        if(RelatedDigital.getRelatedDigitalModel(context).getIsInAppNotificationEnabled()) {
            mStoryItemClickListener = storyItemClickListener
            val parameters = HashMap<String, String>()
            parameters[Constants.REQUEST_ACTION_ID_KEY] = actionId
            StoryActionRequest.createStoryActionRequest(
                mContext,
                getStoryCallback(context, null),
                parameters
            )
        }
    }

    fun setStoryActionWithRequestCallback(context: Context, storyItemClickListener: StoryItemClickListener?,
                                          storyRequestListener: StoryRequestListener?) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(TAG, "Too much server load, ignoring the request!")
            return
        }
        if(RelatedDigital.getRelatedDigitalModel(context).getIsInAppNotificationEnabled()) {
            mStoryItemClickListener = storyItemClickListener
            val parameters = HashMap<String, String>()
            parameters[Constants.REQUEST_ACTION_TYPE_KEY] = Constants.STORY_ACTION_TYPE_VAL
            StoryActionRequest.createStoryActionRequest(
                mContext,
                getStoryCallback(context, storyRequestListener),
                parameters
            )
        }
    }

    fun setStoryActionIdWithRequestCallback(context: Context, actionId: String,
                                            storyItemClickListener: StoryItemClickListener?,
                                            storyRequestListener: StoryRequestListener?) {
        if (RelatedDigital.isBlocked(context)) {
            Log.w(TAG, "Too much server load, ignoring the request!")
            return
        }
        if(RelatedDigital.getRelatedDigitalModel(context).getIsInAppNotificationEnabled()) {
            mStoryItemClickListener = storyItemClickListener
            val parameters = HashMap<String, String>()
            parameters[Constants.REQUEST_ACTION_ID_KEY] = actionId
            StoryActionRequest.createStoryActionRequest(
                mContext,
                getStoryCallback(context, storyRequestListener),
                parameters
            )
        }
    }

    private fun getStoryCallback(context: Context?, storyRequestListener: StoryRequestListener?): VisilabsCallback {
        return object : VisilabsCallback {
            override fun success(response: VisilabsResponse?) {
                storyRequestListener?.onRequestResult(true)
                try {
                    val storyLookingBannerResponse: StoryLookingBannerResponse = Gson().fromJson(response!!.rawResponse, StoryLookingBannerResponse::class.java)
                    if (storyLookingBannerResponse.Story!!.isEmpty()) {
                        Log.i(TAG, "There is no story to show.")
                        return
                    }
                    if (storyLookingBannerResponse.Story!![0].actiondata!!
                                    .taTemplate.equals(Constants.STORY_LOOKING_BANNERS)) {
                        val storyLookingBannerAdapter = StoryLookingBannerAdapter(context!!, mStoryItemClickListener)
                        storyLookingBannerAdapter.setStoryList(storyLookingBannerResponse,
                                storyLookingBannerResponse.Story!![0].actiondata!!.extendedProps)
                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        setHasFixedSize(true)
                        adapter = storyLookingBannerAdapter
                    } else if (storyLookingBannerResponse.Story!![0].actiondata!!
                                    .taTemplate.equals(Constants.STORY_SKIN_BASED)) {
                        run {
                            val skinBased: StorySkinBasedResponse = Gson().fromJson(response
                                    .rawResponse, StorySkinBasedResponse::class.java)
                            val skinBasedAdapter = StorySkinBasedAdapter(context!!)
                            skinBasedAdapter.setStoryListener(mStoryItemClickListener)
                            skinBasedAdapter.setStoryList(skinBased, skinBased.Story!![0].actiondata!!.ExtendedProps)
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            setHasFixedSize(true)
                            adapter = skinBasedAdapter
                        }
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message, ex)
                    storyRequestListener?.onRequestResult(false)
                }
            }

            override fun fail(response: VisilabsResponse?) {
                Log.e(TAG, response!!.rawResponse)
                storyRequestListener?.onRequestResult(false)
            }
        }
    }

    private fun getStoryCallbackSync(context: Context?, activity: Activity): VisilabsCallback {
        return object : VisilabsCallback {
            override fun success(response: VisilabsResponse?) {
                try {
                    val storyLookingBannerResponse: StoryLookingBannerResponse = Gson().fromJson(response!!.rawResponse, StoryLookingBannerResponse::class.java)
                    if (storyLookingBannerResponse.Story!!.isEmpty()) {
                        Log.i(TAG, "There is no story to show.")
                        return
                    }
                    activity.runOnUiThread {
                        if (storyLookingBannerResponse.Story!![0].actiondata!!
                                        .taTemplate.equals(Constants.STORY_LOOKING_BANNERS)) {
                            val storyLookingBannerAdapter = StoryLookingBannerAdapter(context!!, mStoryItemClickListener)
                            storyLookingBannerAdapter.setStoryList(storyLookingBannerResponse,
                                    storyLookingBannerResponse.Story!![0].actiondata!!.extendedProps)
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            setHasFixedSize(true)
                            adapter = storyLookingBannerAdapter
                        } else if (storyLookingBannerResponse.Story!![0].actiondata!!
                                        .taTemplate.equals(Constants.STORY_SKIN_BASED)) {
                            val skinBased: StorySkinBasedResponse = Gson().fromJson(response
                                    .rawResponse, StorySkinBasedResponse::class.java)
                            val skinBasedAdapter = StorySkinBasedAdapter(context!!)
                            skinBasedAdapter.setStoryListener(mStoryItemClickListener)
                            skinBasedAdapter.setStoryList(skinBased, skinBased.Story!![0].actiondata!!.ExtendedProps)
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            setHasFixedSize(true)
                            adapter = skinBasedAdapter
                        }
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message, ex)
                }
            }

            override fun fail(response: VisilabsResponse?) {
                Log.e(TAG, response!!.rawResponse)
            }
        }
    }

    companion object {
        const val TAG = "VisilabsRecyclerView"
    }
}