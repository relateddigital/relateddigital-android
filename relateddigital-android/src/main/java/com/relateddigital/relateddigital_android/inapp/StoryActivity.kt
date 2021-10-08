package com.relateddigital.relateddigital_android.inapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.SkinBasedStories
import com.relateddigital.relateddigital_android.model.StoryItems
import com.relateddigital.relateddigital_android.model.StorySkinBasedActionData
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.util.PersistentTargetManager
import com.squareup.picasso.Picasso
import java.util.*

class StoryActivity : Activity(), StoriesProgressView.StoriesListener {
    private lateinit var mStoriesProgressView: StoriesProgressView
    private lateinit var mIvStory: ImageView
    private lateinit var mVideoView: VideoView
    private var mStoryItemPosition = 0
    private var mPressTime = 0L
    private var mLimit = 500L
    private var mStories: SkinBasedStories? = null
    private var mBannerActionData: StorySkinBasedActionData? = null
    private var mActionId: String? = null
    private lateinit var mBtnStory: Button
    private lateinit var mReverse: View
    private lateinit var mSkip: View
    private lateinit var mIvClose: ImageView
    private lateinit var mIvCover: ImageView
    private lateinit var mTvCover: TextView
    private var mGestureDetector: GestureDetector? = null
    private var mStoryPosition = 0
    private var mOnTouchListener: OnTouchListener? = null
    private var mVideoLastPosition = 0
    private var mRetriever: MediaMetadataRetriever? = null
    private var mActivity: Activity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_story)
        mActivity = this
        if (intent != null) {
            try {
                mBannerActionData =
                    intent.getSerializableExtra(Constants.ACTION_DATA) as StorySkinBasedActionData?
                mActionId = intent.getSerializableExtra(Constants.ACTION_ID) as String?
                mStoryPosition = intent.extras!!.getInt(Constants.STORY_POSITION)
                mStoryItemPosition = intent.extras!!.getInt(Constants.STORY_ITEM_POSITION)
                mStories = mBannerActionData!!.stories!![mStoryPosition]
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Could not get the story data properly, finishing...")
                e.printStackTrace()
                finish()
            }
        } else {
            Log.e(LOG_TAG, "Could not get the story data properly, finishing...")
            finish()
        }
        mRetriever = MediaMetadataRetriever()
        calculateDisplayTimeVideo()
        setTouchEvents()
        setInitialView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchEvents() {
        mGestureDetector = GestureDetector(applicationContext, GestureListener())
        mOnTouchListener = OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mPressTime = System.currentTimeMillis()
                    mStoriesProgressView.pause()
                    if (mVideoView.visibility == View.VISIBLE) {
                        mVideoLastPosition = mVideoView.currentPosition
                        if (mVideoView.isPlaying) {
                            mVideoView.pause()
                        }
                    }
                    return@OnTouchListener false
                }
                MotionEvent.ACTION_UP -> {
                    val now = System.currentTimeMillis()
                    mStoriesProgressView.resume()
                    if (mVideoView.visibility == View.VISIBLE) {
                        mVideoView.seekTo(mVideoLastPosition)
                        mVideoView.start()
                    }
                    return@OnTouchListener mLimit < now - mPressTime
                }
            }
            mGestureDetector!!.onTouchEvent(event)
        }
    }

    private fun setInitialView() {
        mIvStory = findViewById(R.id.iv_story)
        mVideoView = findViewById(R.id.video_story_view)
        mIvCover = findViewById(R.id.civ_cover)
        mTvCover = findViewById(R.id.tv_cover)
        mIvClose = findViewById(R.id.ivClose)
        mBtnStory = findViewById(R.id.btn_story)
        mStoriesProgressView = findViewById(R.id.stories)
        mReverse = findViewById(R.id.reverse)
        mSkip = findViewById(R.id.skip)
        val title: String = mStories!!.title!!
        Log.i("StoryActivityShows ", mActionId + " : " + mStories!!.title)
        PersistentTargetManager.saveShownStory(applicationContext, mActionId!!, mStories!!.title!!)
        mVisilabsSkinBasedAdapter!!.setStoryList(
            mVisilabsSkinBasedAdapter!!.mVisilabsSkinBasedResponse!!,
            mVisilabsSkinBasedAdapter!!.mExtendsProps
        )
        mRecyclerView!!.adapter!!.notifyDataSetChanged()
        mStoriesProgressView.setStoriesCount(mStories!!.getItems()!!.size)
        mStoriesProgressView.setStoriesListener(this)
        if (mStories!!.getItems()!![0].fileType.equals(Constants.STORY_PHOTO_KEY)) {
            mStoriesProgressView.setStoryDuration((
                mStories!!.getItems()
                !![mStoryItemPosition].displayTime!!.toInt() * 1000).toLong()
            )
        } else {
            mStoriesProgressView.setStoryDuration((
                mStories!!.getItems()
                !![mStoryItemPosition].displayTime!!.toInt().toLong())
            )
        }
        val impressionReport: String = mBannerActionData!!.report!!.impression!!
        RequestHandler.createStoryImpressionClickRequest(applicationContext, impressionReport)
        if (!mStories!!.thumbnail.equals("")) {
            Picasso.get().load(mStories!!.thumbnail).into(mIvCover)
        }
        mTvCover.text = mStories!!.title
        setStoryItem(mStories!!.getItems()!![mStoryItemPosition])
        mIvClose.setOnClickListener { onBackPressed() }
        bindReverseView()
        bindSkipView()
    }

    private fun bindSkipView() {
        mSkip.setOnClickListener { mStoriesProgressView.skip() }
        mSkip.setOnTouchListener(mOnTouchListener)
    }

    private fun bindReverseView() {
        mReverse.setOnClickListener { mStoriesProgressView.reverse() }
        mReverse.setOnTouchListener(mOnTouchListener)
    }

    override fun onNext() {
        RequestHandler.createStoryImpressionClickRequest(applicationContext, mBannerActionData!!.report!!.impression)
        if (mStories!!.getItems()!!.size > mStoryItemPosition + 1) {
            setStoryItem(mStories!!.getItems()!![++mStoryItemPosition])
        }
    }

    override fun onPrev() {
        RequestHandler.createStoryImpressionClickRequest(applicationContext, mBannerActionData!!.report!!.impression)
        if (mStoryItemPosition - 1 < 0) {
            if (mStoryPosition - 1 < mBannerActionData!!.stories!!.size && mStoryPosition - 1 > -1) {
                mStoryPosition--
                val previousStoryGroupLatestPosition: Int =
                    mBannerActionData!!.stories!![mStoryPosition]
                        .getItems()!!.size - 1
                startStoryGroup(previousStoryGroupLatestPosition)
            } else {
                onBackPressed()
            }
        } else {
            setStoryItem(mStories!!.getItems()!![--mStoryItemPosition])
        }
    }

    override fun onComplete() {
        mStoryPosition++
        if (mStoryPosition < mBannerActionData!!.stories!!.size) {
            val nextStoryGroupFirstPosition = 0
            startStoryGroup(nextStoryGroupFirstPosition)
        } else {
            onBackPressed()
        }
    }

    private fun startStoryGroup(itemPosition: Int) {
        val intent = Intent(applicationContext, StoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags =
            intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY // Adds the FLAG_ACTIVITY_NO_HISTORY flag
        intent.putExtra(Constants.STORY_ITEM_POSITION, itemPosition)
        intent.putExtra(Constants.STORY_POSITION, mStoryPosition)
        intent.putExtra(Constants.ACTION_DATA, mBannerActionData)
        intent.putExtra(Constants.ACTION_ID, mActionId)
        startActivity(intent)
    }

    override fun onDestroy() {
        mStoriesProgressView.destroy()
        mRetriever!!.release()
        super.onDestroy()
    }

    private fun setStoryItem(item: StoryItems) {
        if (item.fileType.equals(Constants.STORY_PHOTO_KEY)) {
            mStoriesProgressView.setStoryDuration((
                mStories!!.getItems()!![mStoryItemPosition].displayTime!!.toInt() * 1000).toLong()
            )
        } else {
            mStoriesProgressView.setStoryDuration((
                mStories!!.getItems()!![mStoryItemPosition].displayTime!!.toInt()).toLong()
            )
        }
        if (item.fileType.equals(Constants.STORY_PHOTO_KEY)) {
            mVideoView.visibility = View.INVISIBLE
            mIvStory.visibility = View.VISIBLE
            if (!item.fileSrc.equals("")) {
                Picasso.get().load(item.fileSrc).into(mIvStory)
            }
            mStoriesProgressView.startStories(mStoryItemPosition)
        } else if (item.fileType.equals(Constants.STORY_VIDEO_KEY)) {
            mVideoView.visibility = View.VISIBLE
            mIvStory.visibility = View.INVISIBLE
            if (!item.fileSrc.equals("")) {
                mVideoView.setVideoURI(Uri.parse(item.fileSrc))
            }
            mVideoView.setOnPreparedListener {
                mStoriesProgressView.startStories(
                    mStoryItemPosition
                )
            }
            mVideoView.requestFocus()
            mVideoView.start()
        }
        if (!item.buttonText.equals("")) {
            mBtnStory.visibility = View.VISIBLE
            mBtnStory.text = item.buttonText
            mBtnStory.setTextColor(Color.parseColor(item.buttonTextColor))
            mBtnStory.setBackgroundColor(Color.parseColor(item.buttonColor))
        } else {
            mBtnStory.visibility = View.GONE
        }
        mBtnStory.setOnClickListener {
            RequestHandler.createStoryImpressionClickRequest(applicationContext, mBannerActionData!!.report!!.click)
            mActivity!!.finish()
            if (mStoryItemClickListener != null) {
                mStoryItemClickListener!!.storyItemClicked(item.targetUrl)
            }
        }
    }

    private fun calculateDisplayTimeVideo() {
        val items: List<StoryItems> = mStories!!.getItems()!!
        for (i in items.indices) {
            if (items[i].fileType.equals(Constants.STORY_VIDEO_KEY)) {
                mRetriever!!.setDataSource(items[i].fileSrc, HashMap())
                val time =
                    mRetriever!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val timeInMilliSec = time!!.toLong()
                mStories!!.getItems()!![i]
                    .displayTime = (timeInMilliSec + VIDEO_DURATION_OFFSET).toString()
            }
        }
    }

    companion object {
        private const val LOG_TAG = "Story Activity"
        private const val VIDEO_DURATION_OFFSET = 1000
        var mStoryItemClickListener: StoryItemClickListener? = null
        var mRecyclerView: RecyclerView? = null
        var mVisilabsSkinBasedAdapter: StorySkinBasedAdapter? = null
        fun setStoryItemClickListener(storyItemClickListener: StoryItemClickListener?) {
            mStoryItemClickListener = storyItemClickListener
        }

        fun setRecyclerView(recyclerView: RecyclerView?) {
            mRecyclerView = recyclerView
        }

        fun setVisilabsSkinBasedAdapter(visilabsSkinBasedAdapter: StorySkinBasedAdapter?) {
            mVisilabsSkinBasedAdapter = visilabsSkinBasedAdapter
        }
    }
}