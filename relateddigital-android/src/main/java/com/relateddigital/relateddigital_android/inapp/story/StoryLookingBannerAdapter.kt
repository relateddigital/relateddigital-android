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
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.inapp.SafeColor
import com.relateddigital.relateddigital_android.inapp.parseEncodedExtendedProps
import com.relateddigital.relateddigital_android.inapp.toIntOr
import com.relateddigital.relateddigital_android.model.BannerStories
import com.relateddigital.relateddigital_android.model.StoryBannerExtendedProps
import com.relateddigital.relateddigital_android.model.StoryLookingBannerResponse
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.StoryImpressionClickRequest
import com.relateddigital.relateddigital_android.util.PersistentTargetManager
import com.squareup.picasso.Picasso
import java.net.URI
import java.util.*
import de.hdodenhof.circleimageview.CircleImageView
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
            val locLink: String = storyLink.orEmpty()
            StoryImpressionClickRequest.createStoryImpressionClickRequest(mContext, mStoryLookingBanner?.Story?.firstOrNull()?.actiondata?.report?.click)
            Log.i("StoryActivityShows ", "$actionid : $storyTitle")
            if (actionid != null && storyTitle != null) {
                PersistentTargetManager.saveShownStory(mContext, actionid, storyTitle)
            }
            setStoryList(mStoryLookingBanner, mExtendsProps)
            mRecyclerView?.adapter?.notifyDataSetChanged()
            if (mStoryItemClickListener != null) {
                mStoryItemClickListener!!.storyItemClicked(locLink)
            }
        }
        val bannerExtendedProps: StoryBannerExtendedProps? =
            parseEncodedExtendedProps(extendedPropsEncoded, StoryBannerExtendedProps::class.java)
        SafeColor.parseOrNull(bannerExtendedProps?.storylb_label_color)?.let { storyHolder.tvStoryName.setTextColor(it) }
        storyHolder.tvStoryName.typeface = getFontFamily(
            mContext,
            bannerExtendedProps?.font_family,
            bannerExtendedProps?.custom_font_family_android
        )

        if (bannerExtendedProps == null) {
            return
        }
        if (bannerExtendedProps.storylb_img_boxShadow.equals("")) {
            storyHolder.flCircleShadow.visibility = View.VISIBLE
        }
        val borderRadius: String? = bannerExtendedProps.storylb_img_borderRadius
        if (borderRadius != null || bannerExtendedProps.shape != null) {
            if (bannerExtendedProps.shape.equals("rectangle", ignoreCase = true)) {
                val rectangleBorderRadius = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
                storyHolder.ivStory.layoutParams.width = (160 * mContext.resources.displayMetrics.density).toInt()
                storyHolder.ivStory.layoutParams.height = (284 * mContext.resources.displayMetrics.density).toInt()
                storyHolder.flRectangleShadow.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                storyHolder.flRectangleShadow.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                if (moveShownToEnd) {
                    storyHolder.setRectangleViewProperties(rectangleBorderRadius, shown)
                } else {
                    storyHolder.setRectangleViewProperties(rectangleBorderRadius, isItShown(position))
                }
            } else if (borderRadius != null) {
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
    }

    override fun getItemCount(): Int {
        return mStoryLookingBanner?.Story?.firstOrNull()?.actiondata?.stories?.size ?: 0
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
        var flRectangleShadow: FrameLayout = itemView.findViewById(R.id.fl_rect)
        fun setRectangleViewProperties(borderRadius: FloatArray, shown: Boolean) {
            val props = bannerExtendedProps ?: return
            val borderColor = if (shown) Color.rgb(127, 127, 127) else SafeColor.parse(props.storylb_img_borderColor, DEFAULT_BORDER_COLOR)
            ivStory.visibility = View.VISIBLE
            if (props.storylb_img_boxShadow.equals("")) {
                flRectangleShadow.background = null
            }
            ivStory.visibility = View.VISIBLE
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = borderRadius
            shape.setStroke(props.storylb_img_borderWidth.toIntOr(1) * 2, borderColor)
            ivStory.background = shape
        }

        fun setCircleViewProperties(shown: Boolean) {
            val props = bannerExtendedProps ?: return
            val borderColor = if (shown) Color.rgb(127, 127, 127) else SafeColor.parse(props.storylb_img_borderColor, DEFAULT_BORDER_COLOR)
            if (props.storylb_img_boxShadow.equals("")) {
                flCircleShadow.background = null
            }
            civStory.visibility = View.VISIBLE
            civStory.borderColor = borderColor
            civStory.borderWidth = props.storylb_img_borderWidth.toIntOr(1) * 2
        }

        init {
            bannerExtendedProps = parseEncodedExtendedProps(mExtendsProps, StoryBannerExtendedProps::class.java)
        }
    }

    companion object {
        private val DEFAULT_BORDER_COLOR = Color.rgb(22, 22, 22)
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