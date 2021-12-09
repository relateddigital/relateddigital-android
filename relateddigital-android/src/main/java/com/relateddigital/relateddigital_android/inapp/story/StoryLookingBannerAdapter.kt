package com.relateddigital.relateddigital_android.inapp.story

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.BuildConfig
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.BannerStories
import com.relateddigital.relateddigital_android.model.StoryBannerExtendedProps
import com.relateddigital.relateddigital_android.model.StoryLookingBannerResponse
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.util.PersistentTargetManager
import com.squareup.picasso.Picasso
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import de.hdodenhof.circleimageview.CircleImageView
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.AppUtils.getFontFamily


class StoryLookingBannerAdapter(var mContext: Context, var mStoryItemClickListener: StoryItemClickListener?) : RecyclerView.Adapter<StoryLookingBannerAdapter.StoryHolder>() {
    private var mRecyclerView: RecyclerView? = null
    private var mStoryLookingBanner: StoryLookingBannerResponse? = null
    var mExtendsProps: String? = null
    private var moveShownToEnd = false
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.story_item, parent, false)
        return StoryHolder(view)
    }

    override fun onBindViewHolder(storyHolder: StoryHolder, position: Int) {
        val actionid: String? = mStoryLookingBanner!!.Story!![0].actid
        val story: BannerStories = mStoryLookingBanner!!.Story!![0].actiondata!!.stories!![position]
        val storyTitle: String? = story.title
        val storyImage: String? = story.smallImg
        val storyLink: String? = story.link
        val shown: Boolean = story.shown
        storyHolder.tvStoryName.text = storyTitle
        if (storyImage != "") {
            Picasso.get().load(storyImage).fit().into(storyHolder.ivStory)
            Picasso.get().load(storyImage).fit().into(storyHolder.civStory)
        }
        val extendedPropsEncoded = mExtendsProps
        storyHolder.llStoryContainer.setOnClickListener {
            val locLink: String = mStoryLookingBanner!!.Story!![0].actiondata!!.stories!![position].link!!
            RequestHandler.createStoryImpressionClickRequest(mContext, mStoryLookingBanner!!.Story!![0].actiondata!!.report!!.click)
            Log.i("StoryActivityShows ", "$actionid : $storyTitle")
            PersistentTargetManager.saveShownStory(mContext, actionid!!, storyTitle!!)
            setStoryList(mStoryLookingBanner, mExtendsProps)
            mRecyclerView!!.adapter!!.notifyDataSetChanged()
            if (mStoryItemClickListener != null) {
                mStoryItemClickListener!!.storyItemClicked(locLink)
            }
        }
        var bannerExtendedProps: StoryBannerExtendedProps? = null
        try {
            bannerExtendedProps = Gson().fromJson(URI(extendedPropsEncoded).path, StoryBannerExtendedProps::class.java)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        storyHolder.tvStoryName.setTextColor(Color.parseColor(bannerExtendedProps?.storylb_label_color))
        storyHolder.tvStoryName.typeface = getFontFamily(
            mContext,
            bannerExtendedProps?.font_family,
            bannerExtendedProps?.custom_font_family_android
        )

        if (BuildConfig.DEBUG && bannerExtendedProps == null) {
            error("Assertion failed")
        }
        if (bannerExtendedProps!!.storylb_img_boxShadow.equals("")) {
            storyHolder.flCircleShadow.visibility = View.VISIBLE
        }
        storyHolder.tvStoryName.setTextColor(Color.parseColor(bannerExtendedProps.storylb_label_color))
        val borderRadius: String? = bannerExtendedProps.storylb_img_borderRadius
        if (borderRadius != null) {
            when (borderRadius) {
                Constants.STORY_CIRCLE -> if (moveShownToEnd) {
                    storyHolder.setCircleViewProperties(shown)
                } else {
                    storyHolder.setCircleViewProperties(isItShown(position))
                }
                Constants.STORY_ROUNDED_RECTANGLE -> {
                    val roundedRectangleBorderRadius = floatArrayOf(15f, 15f, 15f, 15f, 15f, 15f, 15f, 15f)
                    if (moveShownToEnd) {
                        storyHolder.setRectangleViewProperties(roundedRectangleBorderRadius, shown)
                    } else {
                        storyHolder.setRectangleViewProperties(roundedRectangleBorderRadius, isItShown(position))
                    }
                }
                Constants.STORY_RECTANGLE -> {
                    val rectangleBorderRadius = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
                    if (moveShownToEnd) {
                        storyHolder.setRectangleViewProperties(rectangleBorderRadius, shown)
                    } else {
                        storyHolder.setRectangleViewProperties(rectangleBorderRadius, isItShown(position))
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mStoryLookingBanner!!.Story!![0].actiondata!!.stories!!.size
    }

    fun setStoryList(storyLookingBanner: StoryLookingBannerResponse?, extendsProps: String?) {
        try {
            val bannerExtendedProps: StoryBannerExtendedProps = Gson().fromJson(URI(storyLookingBanner!!.Story!![0].actiondata!!.extendedProps).path, StoryBannerExtendedProps::class.java)
            moveShownToEnd = bannerExtendedProps.moveShownToEnd
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (moveShownToEnd) {
            val shownStoriesMap: Map<String, List<String>> = PersistentTargetManager.getShownStories(mContext)
            if (shownStoriesMap.containsKey(storyLookingBanner!!.Story!![0].actid)) {
                val shownTitles = shownStoriesMap[storyLookingBanner.Story!![0].actid]
                val notShownStories: MutableList<BannerStories> = ArrayList<BannerStories>()
                val shownStories: MutableList<BannerStories> = ArrayList<BannerStories>()
                if (!shownTitles.isNullOrEmpty()) {
                    for (s in storyLookingBanner.Story!![0].actiondata!!.stories!!) {
                        if (shownTitles.contains(s.title)) {
                            s.shown = true
                            shownStories.add(s)
                        } else {
                            notShownStories.add(s)
                        }
                    }
                    notShownStories.addAll(shownStories)
                    storyLookingBanner.Story!![0].actiondata!!.stories = notShownStories
                }
            }
        }
        mExtendsProps = extendsProps
        mStoryLookingBanner = storyLookingBanner
    }

    inner class StoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvStoryName: TextView = itemView.findViewById(R.id.tv_story_name)
        var civStory: CircleImageView = itemView.findViewById(R.id.civ_story)
        var ivStory: ImageView = itemView.findViewById(R.id.iv_story)
        var llStoryContainer: LinearLayout = itemView.findViewById(R.id.ll_story)
        private var bannerExtendedProps: StoryBannerExtendedProps? = null
        var flCircleShadow: FrameLayout = itemView.findViewById(R.id.fl_circle)
        private var flRectangleShadow: FrameLayout = itemView.findViewById(R.id.fl_rect)
        fun setRectangleViewProperties(borderRadius: FloatArray, shown: Boolean) {
            val borderColor = if (shown) Color.rgb(127, 127, 127) else Color.parseColor(bannerExtendedProps!!.storylb_img_borderColor)
            ivStory.visibility = View.VISIBLE
            if (bannerExtendedProps!!.storylb_img_boxShadow.equals("")) {
                flRectangleShadow.background = null
            }
            ivStory.visibility = View.VISIBLE
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = borderRadius
            shape.setStroke(bannerExtendedProps!!.storylb_img_borderWidth!!.toInt() * 2, borderColor)
            ivStory.background = shape
        }

        fun setCircleViewProperties(shown: Boolean) {
            val borderColor = if (shown) Color.rgb(127, 127, 127) else Color.parseColor(bannerExtendedProps!!.storylb_img_borderColor)
            if (bannerExtendedProps!!.storylb_img_boxShadow.equals("")) {
                flCircleShadow.background = null
            }
            civStory.visibility = View.VISIBLE
            civStory.borderColor = borderColor
            civStory.borderWidth = bannerExtendedProps!!.storylb_img_borderWidth!!.toInt() * 2
        }

        init {
            val extendedPropsEncoded = mExtendsProps
            try {
                bannerExtendedProps = Gson().fromJson(URI(extendedPropsEncoded).path,
                        StoryBannerExtendedProps::class.java)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
    }

    private fun isItShown(position: Int): Boolean {
        var result = false
        val shownStoriesMap: Map<String, List<String>> = PersistentTargetManager.getShownStories(mContext)
        if (shownStoriesMap.containsKey(mStoryLookingBanner!!.Story!![0].actid)) {
            val shownTitles = shownStoriesMap[mStoryLookingBanner!!.Story!![0].actid]
            if (!shownTitles.isNullOrEmpty()) {
                result = shownTitles.contains(mStoryLookingBanner!!.Story!![0].actiondata!!.stories!![position].title)
            }
        }
        return result
    }
}