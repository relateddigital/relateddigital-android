package com.relateddigital.relateddigital_android.inapp.bannercarousel

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.model.AppBanner


class BannerCarouselAdapter(private val mContext: Context,
                            private val mBannerItemClickListener: BannerItemClickListener?): RecyclerView.Adapter<BannerCarouselAdapter.BannerHolder>() {
    private lateinit var mRecyclerView: RecyclerView
    private var isSwipe = true
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null
    private var mPosition = 0
    private var isScrolling = false
    private var mAppBanner: AppBanner? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view: View = if(isSwipe) {
            inflater.inflate(R.layout.banner_carousel_swipe_list_item, parent, false)
        } else {
            inflater.inflate(R.layout.banner_carousel_slide_list_item, parent, false)
        }
        return BannerHolder(view)
    }

    override fun onBindViewHolder(bannerHolder: BannerHolder, position: Int) {
        if(isSwipe) {
            Glide.with(mContext)
                .asBitmap()
                .transform(
                    MultiTransformation(
                        CenterCrop(),
                        GranularRoundedCorners(30f, 30f, 30f, 30f)
                    )
                )
                .load(mAppBanner!!.actionData!!.appBanners!![position].image)
                .into(bannerHolder.swipeImageView!!)

            bannerHolder.dotIndicator!!.removeAllViews()

            for (i in 0 until itemCount) {
                val view = View(mContext)
                view.setBackgroundResource(R.drawable.dot_indicator_banner_default)
                val layoutParams = LinearLayout.LayoutParams(
                    20, 20)
                layoutParams.setMargins(10, 0, 10, 0)
                view.layoutParams = layoutParams
                bannerHolder.dotIndicator!!.addView(view)
            }


            for (i in 0 until itemCount) {
                if (i == position) {
                    bannerHolder.dotIndicator!!.getChildAt(i)
                        .setBackgroundResource(R.drawable.dot_indicator_banner_selected)
                } else {
                    bannerHolder.dotIndicator!!.getChildAt(i)
                        .setBackgroundResource(R.drawable.dot_indicator_banner_default)
                }
            }

            bannerHolder.swipeImageView!!.setOnClickListener {
                mBannerItemClickListener!!.bannerItemClicked(mAppBanner!!.actionData!!.appBanners!![position].androidLink)
            }
        } else {
            Glide.with(mContext)
                .asBitmap()
                .transform(
                    MultiTransformation(
                        CenterCrop(),
                        GranularRoundedCorners(30f, 30f, 30f, 30f)
                    )
                )
                .load(mAppBanner!!.actionData!!.appBanners!![position].image)
                .into(bannerHolder.slideImageView!!)

            val numStr = (position + 1).toString() + "/" + itemCount.toString()
            bannerHolder.numberIndicator!!.text = numStr

            bannerHolder.slideImageView!!.setOnClickListener {
                mBannerItemClickListener!!.bannerItemClicked(mAppBanner!!.actionData!!.appBanners!![position].androidLink)
            }
        }
    }

    override fun getItemCount(): Int {
        return mAppBanner?.actionData?.appBanners?.size ?: 0
    }

    fun setBannerList(recyclerView: RecyclerView, appBanner: AppBanner) {
        mAppBanner = appBanner
        isSwipe = mAppBanner!!.actionData!!.transitionAction == "swipe"
        val slidePeriod = 3

        mRecyclerView = recyclerView
        if (!isSwipe) {
            mRecyclerView.layoutManager =
                object : LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false) {
                    override fun canScrollHorizontally(): Boolean {
                        if(isScrolling) {
                            return true
                        }
                        return false
                    }
                }

            mRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(recyclerView.scrollState == RecyclerView.SCROLL_STATE_SETTLING){
                        isScrolling = false
                    }
                }
            })
            mHandler = Handler(Looper.getMainLooper())
            mRunnable = object : Runnable {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                override fun run() {
                    isScrolling = true
                    if (mPosition == itemCount - 1) {
                        mPosition = 0
                    } else {
                        mPosition++
                    }
                    mRecyclerView.smoothScrollToPosition(mPosition)
                    val myProcess = ActivityManager.RunningAppProcessInfo()
                    ActivityManager.getMyMemoryState(myProcess)
                    if (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_GONE) {
                        mHandler!!.postDelayed(this, (slidePeriod * 1000).toLong())
                    } else {
                        mHandler!!.removeCallbacks(mRunnable!!)
                    }
                }
            }
            mHandler!!.postDelayed(mRunnable!!, (slidePeriod * 1000).toLong())
        }

        notifyDataSetChanged()
    }

    inner class BannerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var swipeImageView: ImageView? = null
        var slideImageView: ImageView? = null
        var dotIndicator: LinearLayout? = null
        var numberIndicator: TextView? = null

        init {
            if(isSwipe) {
                swipeImageView = itemView.findViewById(R.id.banner_swipe_image_item)
                dotIndicator = itemView.findViewById(R.id.banner_dot_indicator_item)
            } else {
                slideImageView = itemView.findViewById(R.id.banner_slide_image_item)
                numberIndicator = itemView.findViewById(R.id.banner_number_indicator_item)
            }
        }
    }
}