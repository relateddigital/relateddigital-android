package com.relateddigital.relateddigital_android.inapp.bannercarousel

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.inapp.VisilabsCallback
import com.relateddigital.relateddigital_android.inapp.VisilabsResponse


class BannerRecyclerView : RecyclerView {
    private var mContext: Context = context
    var mBannerItemClickListener: BannerItemClickListener? = null
    var mBannerRequestListener: BannerRequestListener? = null

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle)

    fun requestBannerCarouselAction(context: Context,
                                    actionId: String? = null,
                                    bannerRequestListener: BannerRequestListener? = null,
                                    bannerItemClickListener: BannerItemClickListener? = null) {

        if (RelatedDigital.isBlocked(context)) {
            Log.e(LOG_TAG, "Too much server load, ignoring the request!")
            bannerRequestListener?.onRequestResult(false)
            return
        }

        if(RelatedDigital.getRelatedDigitalModel(context).getIsInAppNotificationEnabled()) {
            mBannerItemClickListener = bannerItemClickListener
            mBannerRequestListener = bannerRequestListener
            val parameters = HashMap<String, String>()
            if(actionId.isNullOrEmpty()) {
                parameters[Constants.REQUEST_ACTION_TYPE_KEY] = Constants.BANNER_CAROUSEL_ACTION_TYPE_VAL
            } else {
                parameters[Constants.REQUEST_ACTION_ID_KEY] = actionId
            }

            //TODO : real request here when BE gets ready
            /*RequestHandler.createStoryActionRequest(
                mContext,
                getBannerCallback(context),
                parameters
            )*/

            // TODO : remove this when BE gets ready
            val bannerCarouselAdapter = BannerCarouselAdapter(context, mBannerItemClickListener)
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            bannerCarouselAdapter.setBannerList(this)
            setHasFixedSize(true)
            adapter = bannerCarouselAdapter
            val snapHelper: SnapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(this)


        } else {
            Log.e(LOG_TAG, "In-app notification is not enabled." +
                        "Call RelatedDigital.setIsInAppNotificationEnabled() first")
            bannerRequestListener?.onRequestResult(false)
            return
        }
    }

    private fun getBannerCallback(context: Context): VisilabsCallback {
        return object : VisilabsCallback {
            override fun success(response: VisilabsResponse?) {
                mBannerRequestListener?.onRequestResult(true)
                try {
                    // TODO : get the real data and set it to adapter when BE gets ready
                } catch (ex: Exception) {
                    Log.e(LOG_TAG, ex.message, ex)
                    mBannerRequestListener?.onRequestResult(false)
                }
            }

            override fun fail(response: VisilabsResponse?) {
                Log.e(LOG_TAG, response!!.rawResponse)
                mBannerRequestListener?.onRequestResult(false)
            }
        }
    }

    companion object {
        const val LOG_TAG = "BannerRecyclerView"
    }
}