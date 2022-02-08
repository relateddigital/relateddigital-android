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
import com.relateddigital.relateddigital_android.model.*
import com.relateddigital.relateddigital_android.util.PersistentTargetManager
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.net.URI
import java.net.URISyntaxException
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
        var extendedProps: StorySkinBasedExtendedProps? = null
        try {
            extendedProps = Gson().fromJson(
                URI(extendedPropsEncoded).path,
                StorySkinBasedExtendedProps::class.java
            )
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        if (!extendedProps?.storyz_label_color.isNullOrEmpty()) {
            storyHolder.tvStoryName.setTextColor(Color.parseColor(extendedProps?.storyz_label_color))
        }

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
        if (mVisilabsSkinBasedResponse!!.Story!![0].actiondata!!.stories!![position]
                .getItems()!!.isNotEmpty()
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
        return mVisilabsSkinBasedResponse!!.Story!![0].actiondata!!.stories!!.size
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
            var borderColorString: String = extendedProps!!.storyz_img_borderColor!!
            if (borderColorString == "") {
                borderColorString = "#161616"
            }
            val borderColor =
                if (shown) Color.rgb(127, 127, 127) else Color.parseColor(borderColorString)
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = borderRadius
            shape.setStroke(3, borderColor)
            ivStory.background = shape
        }

        fun setCircleViewProperties(shown: Boolean) {
            civStory.visibility = View.VISIBLE
            var borderColorString: String = extendedProps!!.storyz_img_borderColor!!
            if (borderColorString == "") {
                borderColorString = "#161616"
            }
            val borderColor =
                if (shown) Color.rgb(127, 127, 127) else Color.parseColor(borderColorString)
            civStory.borderColor = borderColor
            civStory.borderWidth = 3
        }

        init {
            val extendedPropsEncoded = mExtendsProps
            try {
                extendedProps = Gson().fromJson(
                    URI(extendedPropsEncoded)
                        .path, StorySkinBasedExtendedProps::class.java
                )
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
    }

    private fun cacheImagesBeforeDisplaying() {
        isFirstRun = false
        val actiondata: StorySkinBasedActionData = mVisilabsSkinBasedResponse!!.Story!![0].actiondata!!
        for (i in actiondata.stories!!.indices) {
            for (j in actiondata.stories!![i].getItems()!!.indices) {
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