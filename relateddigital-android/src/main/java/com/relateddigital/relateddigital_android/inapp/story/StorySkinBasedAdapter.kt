package com.relateddigital.relateddigital_android.inapp.story

import android.content.Context
import android.content.Intent
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
import com.relateddigital.relateddigital_android.model.*
import com.relateddigital.relateddigital_android.util.PersistentTargetManager
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.net.URI
import java.util.*
import com.relateddigital.relateddigital_android.util.AppUtils.getFontFamily


class StorySkinBasedAdapter(var mContext: Context) :
    RecyclerView.Adapter<StorySkinBasedAdapter.StoryHolder>() {
    private var mRecyclerView: RecyclerView? = null
    var mVisilabsSkinBasedResponse: StorySkinBasedResponse? = null
    var mExtendsProps: String? = null
    private var isFirstRun = true
    private var moveShownToEnd = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryHolder {
        if (isFirstRun) {
            cacheImagesBeforeDisplaying()
        }
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.story_item, parent, false)
        return StoryHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onBindViewHolder(storyHolder: StoryHolder, position: Int) {
        val story: SkinBasedStories =
            mVisilabsSkinBasedResponse!!.Story!![0].actiondata!!.stories!![position]
        val storyTitle: String? = story.title
        val storyImage: String? = story.thumbnail
        val shown: Boolean = story.shown
        storyHolder.tvStoryName.text = storyTitle
        if (storyImage != "") {
            Picasso.get().load(storyImage).fit().into(storyHolder.ivStory)
            Picasso.get().load(storyImage).fit().into(storyHolder.civStory)
        }
        val extendedPropsEncoded = mExtendsProps
        val extendedProps: StorySkinBasedExtendedProps? =
            parseEncodedExtendedProps(extendedPropsEncoded, StorySkinBasedExtendedProps::class.java)

        SafeColor.parseOrNull(extendedProps?.storyz_label_color)?.let { storyHolder.tvStoryName.setTextColor(it) }

        storyHolder.tvStoryName.typeface = getFontFamily(
            mContext,
            extendedProps?.font_family,
            extendedProps?.custom_font_family_android
        )

        storyHolder.civStory.setOnClickListener { clickEvent(position) }
        storyHolder.ivStory.setOnClickListener { clickEvent(position) }
        val borderRadius: String? =
            extendedProps?.storyz_img_borderRadius
        if (borderRadius != null) {
            when (borderRadius) {
                Constants.STORY_CIRCLE -> if (moveShownToEnd) {
                    storyHolder.setCircleViewProperties(shown)
                } else {
                    storyHolder.setCircleViewProperties(isItShown(position))
                }
                Constants.STORY_ROUNDED_RECTANGLE -> {
                    val roundedRectangleBorderRadius =
                        floatArrayOf(15f, 15f, 15f, 15f, 15f, 15f, 15f, 15f)
                    if (moveShownToEnd) {
                        storyHolder.setRectangleViewProperties(roundedRectangleBorderRadius, shown)
                    } else {
                        storyHolder.setRectangleViewProperties(
                            roundedRectangleBorderRadius,
                            isItShown(position)
                        )
                    }
                }
                Constants.STORY_RECTANGLE -> {
                    val rectangleBorderRadius = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
                    if (moveShownToEnd) {
                        storyHolder.setRectangleViewProperties(rectangleBorderRadius, shown)
                    } else {
                        storyHolder.setRectangleViewProperties(
                            rectangleBorderRadius,
                            isItShown(position)
                        )
                    }
                }
                else -> storyHolder.setCircleViewProperties(shown)
            }
        }
    }

    private fun clickEvent(position: Int) {
        if (mVisilabsSkinBasedResponse?.Story?.firstOrNull()?.actiondata?.stories?.getOrNull(position)
                ?.getItems().isNullOrEmpty().not()
        ) {
            val story = StoryActivity()
            StoryActivity.setRecyclerView(mRecyclerView)
            StoryActivity.setVisilabsSkinBasedAdapter(this)
            val intent = Intent(mContext, story.javaClass)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags =
                intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY // Adds the FLAG_ACTIVITY_NO_HISTORY flag
            intent.putExtra(Constants.STORY_POSITION, position)
            intent.putExtra(Constants.STORY_ITEM_POSITION, 0)
            intent.putExtra(
                Constants.ACTION_DATA,
                mVisilabsSkinBasedResponse!!.Story!![0].actiondata
            )
            intent.putExtra(
                Constants.ACTION_ID,
                mVisilabsSkinBasedResponse!!.Story!![0].actid
            )
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mVisilabsSkinBasedResponse?.Story?.firstOrNull()?.actiondata?.stories?.size ?: 0
    }

    fun setStoryList(storySkinBasedResponse: StorySkinBasedResponse, extendsProps: String?) {
        try {
            val extendedProps: StorySkinBasedExtendedProps = Gson().fromJson(
                URI(
                    storySkinBasedResponse
                        .Story!![0].actiondata!!.ExtendedProps
                ).path, StorySkinBasedExtendedProps::class.java
            )
            moveShownToEnd = extendedProps.moveShownToEnd
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (moveShownToEnd) {
            val shownStoriesMap: Map<String, List<String>> =
                PersistentTargetManager.getShownStories(mContext)
            if (shownStoriesMap.containsKey(
                    storySkinBasedResponse.Story!![0].actid
                )
            ) {
                val shownTitles =
                    shownStoriesMap[storySkinBasedResponse.Story!![0].actid]
                val notShownStories: MutableList<SkinBasedStories> = ArrayList<SkinBasedStories>()
                val shownStories: MutableList<SkinBasedStories> = ArrayList<SkinBasedStories>()
                if (!shownTitles.isNullOrEmpty()) {
                    for (s in storySkinBasedResponse.Story!![0].actiondata!!
                        .stories!!) {
                        if (shownTitles.contains(s.title)) {
                            s.shown = true
                            shownStories.add(s)
                        } else {
                            notShownStories.add(s)
                        }
                    }
                    notShownStories.addAll(shownStories)
                    storySkinBasedResponse.Story!![0].actiondata!!
                        .stories = notShownStories
                }
            }
        }
        mExtendsProps = extendsProps
        mVisilabsSkinBasedResponse = storySkinBasedResponse
    }

    fun setStoryListener(storyItemClickListener: StoryItemClickListener?) {
        StoryActivity.setStoryItemClickListener(storyItemClickListener)
    }

    inner class StoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvStoryName: TextView = itemView.findViewById(R.id.tv_story_name)
        var civStory: CircleImageView = itemView.findViewById(R.id.civ_story)
        var ivStory: ImageView = itemView.findViewById(R.id.iv_story)
        var llStoryContainer: LinearLayout = itemView.findViewById(R.id.ll_story)
        private var extendedProps: StorySkinBasedExtendedProps? = null
        var frameLayout: FrameLayout = itemView.findViewById(R.id.fl_circle)
        fun setRectangleViewProperties(borderRadius: FloatArray, shown: Boolean) {
            ivStory.visibility = View.VISIBLE
            val borderColor =
                if (shown) Color.rgb(127, 127, 127) else SafeColor.parse(extendedProps?.storyz_img_borderColor, Color.rgb(22, 22, 22))
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = borderRadius
            shape.setStroke(3, borderColor)
            ivStory.background = shape
        }

        fun setCircleViewProperties(shown: Boolean) {
            civStory.visibility = View.VISIBLE
            val borderColor =
                if (shown) Color.rgb(127, 127, 127) else SafeColor.parse(extendedProps?.storyz_img_borderColor, Color.rgb(22, 22, 22))
            civStory.borderColor = borderColor
            civStory.borderWidth = 3
        }

        init {
            extendedProps = parseEncodedExtendedProps(mExtendsProps, StorySkinBasedExtendedProps::class.java)
        }
    }

    private fun cacheImagesBeforeDisplaying() {
        isFirstRun = false
        val actiondata: StorySkinBasedActionData = mVisilabsSkinBasedResponse?.Story?.firstOrNull()?.actiondata ?: return
        for (i in actiondata.stories.orEmpty().indices) {
            for (j in actiondata.stories!![i].getItems().orEmpty().indices) {
                if (actiondata.stories!![i].getItems()!![j].fileType
                        .equals(Constants.STORY_PHOTO_KEY)
                ) {
                    if (!actiondata.stories!![i].getItems()!![j].fileSrc.equals("")) {
                        try {
                            Picasso.get()
                                .load(actiondata.stories!![i].getItems()!![j].fileSrc)
                                .fetch()
                        } catch (e: Exception) {
                            Log.w("Story Activity", "URL for the image is empty!")
                        }
                    }
                }
            }
        }
    }

    private fun isItShown(position: Int): Boolean {
        var result = false
        val shownStoriesMap: Map<String, List<String>> =
            PersistentTargetManager.getShownStories(mContext)
        if (shownStoriesMap.containsKey(mVisilabsSkinBasedResponse!!.Story!![0].actid)) {
            val shownTitles =
                shownStoriesMap[mVisilabsSkinBasedResponse!!.Story!![0].actid]
            if (!shownTitles.isNullOrEmpty()) {
                result = shownTitles.contains(
                    mVisilabsSkinBasedResponse!!.Story!![0].actiondata!!.stories!![position].title
                )
            }
        }
        return result
    }
}