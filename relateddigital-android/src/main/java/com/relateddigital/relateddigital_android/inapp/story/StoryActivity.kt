package com.relateddigital.relateddigital_android.inapp.story

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.SkinBasedStories
import com.relateddigital.relateddigital_android.model.StoryItems
import com.relateddigital.relateddigital_android.model.StorySkinBasedActionData
import com.relateddigital.relateddigital_android.model.StorySkinBasedExtendedProps
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.AppUtils.getFontFamily
import com.relateddigital.relateddigital_android.util.PersistentTargetManager
import com.squareup.picasso.Picasso
import java.net.URI
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
    private var mExtendedProps: StorySkinBasedExtendedProps? = null
    private var mActionId: String? = null
    private lateinit var mBtnStory: Button
    private lateinit var mReverse: View
    private lateinit var mSkip: View
    private lateinit var mIvClose: ImageView
    private lateinit var mIvCover: ImageView
    private lateinit var mTvCover: TextView
    private lateinit var mCountdownEndGifView: ImageView
    private var mGestureDetector: GestureDetector? = null
    private var mStoryPosition = 0
    private var mOnTouchListener: OnTouchListener? = null
    private var mVideoLastPosition = 0
    private var mRetriever: MediaMetadataRetriever? = null
    private var mActivity: Activity? = null
    private var isCountDownTimer = false
    private var mCountDownContainer: LinearLayout? = null
    private var mCountDownTopText: TextView? = null
    private var mCountDownBotText:TextView? = null
    private var mCountDownTimer: RelativeLayout? = null
    private var mWeekNum: TextView? = null
    private var mDivider1:TextView? = null
    private var mDayNum:TextView? = null
    private var mDivider2:TextView? = null
    private var mHourNum:TextView? = null
    private var mDivider3:TextView? = null
    private var mMinuteNum: TextView? = null
    private var mDivider4:TextView? = null
    private var mSecNum:TextView? = null
    private var mWeekStr:TextView? = null
    private var mDayStr:TextView? = null
    private var mHourStr:TextView? = null
    private var mMinuteStr:TextView? = null
    private var mSecStr:TextView? = null
    private var mWeekNumber: Short = 0
    private var mDayNumber: Short = 0
    private var mHourNumber: Short = 0
    private var mMinuteNumber: Short = 0
    private var mSecondNumber: Short = 0
    private var mTimerCountDown: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
        setContentView(R.layout.activity_story)
        mActivity = this
        if (intent != null) {
            try {
                mBannerActionData =
                    intent.getSerializableExtra(Constants.ACTION_DATA) as StorySkinBasedActionData?
                mExtendedProps = Gson().fromJson(
                    URI(mBannerActionData?.ExtendedProps).path,
                    StorySkinBasedExtendedProps::class.java
                )
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
        mCountDownContainer = findViewById(R.id.count_down_container)
        mCountDownTopText = findViewById(R.id.count_down_top_text)
        mCountDownBotText = findViewById(R.id.count_down_bot_text)
        mCountDownTimer = findViewById(R.id.countdown_timer)
        mWeekNum = findViewById(R.id.week_num)
        mDivider1 = findViewById(R.id.divider1)
        mDayNum = findViewById(R.id.day_num)
        mDivider2 = findViewById(R.id.divider2)
        mHourNum = findViewById(R.id.hour_num)
        mDivider3 = findViewById(R.id.divider3)
        mMinuteNum = findViewById(R.id.minute_num)
        mDivider4 = findViewById(R.id.divider4)
        mSecNum = findViewById(R.id.sec_num)
        mWeekStr = findViewById(R.id.week_str)
        mDayStr = findViewById(R.id.day_str)
        mHourStr = findViewById(R.id.hour_str)
        mMinuteStr = findViewById(R.id.minute_str)
        mSecStr = findViewById(R.id.sec_str)
        mCountdownEndGifView = findViewById(R.id.countdown_end_gif)

        mCountdownEndGifView.visibility = View.GONE

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
        if (mExtendedProps != null && !mExtendedProps!!.storyz_label_color.isNullOrEmpty()) {
            mTvCover.setTextColor(Color.parseColor(mExtendedProps!!.storyz_label_color))
        }

        mTvCover.typeface = getFontFamily(
            this,
            if (mExtendedProps != null) mExtendedProps!!.font_family else null,
            if (mExtendedProps != null) mExtendedProps!!.custom_font_family_android else null
        )

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
        mCountdownEndGifView.visibility = View.GONE
        RequestHandler.createStoryImpressionClickRequest(applicationContext, mBannerActionData!!.report!!.impression)
        if (mStories!!.getItems()!!.size > mStoryItemPosition + 1) {
            setStoryItem(mStories!!.getItems()!![++mStoryItemPosition])
        }
    }

    override fun onPrev() {
        mCountdownEndGifView.visibility = View.GONE
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
        mCountdownEndGifView.visibility = View.GONE
        mStoryPosition++
        if (mStoryPosition < mBannerActionData!!.stories!!.size) {
            val nextStoryGroupFirstPosition = 0
            startStoryGroup(nextStoryGroupFirstPosition)
        } else {
            onBackPressed()
        }
    }

    private fun startStoryGroup(itemPosition: Int) {
        mCountdownEndGifView.visibility = View.GONE
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
        if(mTimerCountDown!=null){
            mTimerCountDown!!.cancel()
        }
        super.onDestroy()
    }

    private fun setStoryItem(item: StoryItems) {
        if(mTimerCountDown!=null){
            mTimerCountDown!!.cancel()
        }

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

            isCountDownTimer = mStories!!.getItems()!![mStoryItemPosition].countdown != null

            if (isCountDownTimer) {
                mCountDownContainer!!.visibility = View.VISIBLE
                mCountDownTimer!!.setBackgroundResource(R.drawable.rounded_corners_full)
                val gd = mCountDownTimer!!.background as GradientDrawable
                gd.setColor(ContextCompat.getColor(this, R.color.white))

                if(mStories!!.getItems()!![mStoryItemPosition].countdown!!.pagePosition == "top") {
                    mCountDownTopText!!.visibility = View.VISIBLE
                    mCountDownBotText!!.visibility = View.GONE

                    mCountDownTopText!!.text = mStories!!.getItems()!![mStoryItemPosition]
                        .countdown!!.messageText!!.replace("\\n", "\n")
                    mCountDownTopText!!.setTextColor(Color.parseColor(mStories!!.getItems()!![mStoryItemPosition]
                        .countdown!!.messageTextColor))
                    mCountDownTopText!!.textSize = mStories!!.getItems()!![mStoryItemPosition]
                        .countdown!!.messageTextSize!!.toFloat() + 16
                    mCountDownTopText!!.typeface = Typeface.DEFAULT
                } else {
                    mCountDownBotText!!.visibility = View.VISIBLE
                    mCountDownTopText!!.visibility = View.GONE

                    mCountDownBotText!!.text = mStories!!.getItems()!![mStoryItemPosition]
                        .countdown!!.messageText!!.replace("\\n", "\n")
                    mCountDownBotText!!.setTextColor(Color.parseColor(mStories!!.getItems()!![mStoryItemPosition]
                        .countdown!!.messageTextColor))
                    mCountDownBotText!!.textSize = mStories!!.getItems()!![mStoryItemPosition]
                        .countdown!!.messageTextSize!!.toFloat() + 16
                    mCountDownBotText!!.typeface = Typeface.DEFAULT
                }

                setTimerValues()

                if(mWeekNum!!.visibility != View.GONE) {
                    mWeekNum!!.setBackgroundResource(R.drawable.rounded_corners_full_small_edge)
                    val gdWeek = mWeekNum!!.background as GradientDrawable
                    gdWeek.setColor(Color.parseColor("#E5E4E2"))
                    mWeekNum!!.text = mWeekNumber.toString()
                    mWeekNum!!.setTextColor(ContextCompat.getColor(this, R.color.black))
                }

                if(mDayNum!!.visibility != View.GONE) {
                    mDayNum!!.setBackgroundResource(R.drawable.rounded_corners_full_small_edge)
                    val gdDay = mDayNum!!.background as GradientDrawable
                    gdDay.setColor(Color.parseColor("#E5E4E2"))
                    mDayNum!!.text = mDayNumber.toString()
                    mDayNum!!.setTextColor(ContextCompat.getColor(this, R.color.black))
                }

                if(mHourNum!!.visibility != View.GONE) {
                    mHourNum!!.setBackgroundResource(R.drawable.rounded_corners_full_small_edge)
                    val gdHour = mHourNum!!.background as GradientDrawable
                    gdHour.setColor(Color.parseColor("#E5E4E2"))
                    mHourNum!!.text = mHourNumber.toString()
                    mHourNum!!.setTextColor(ContextCompat.getColor(this, R.color.black))
                }

                if(mMinuteNum!!.visibility != View.GONE) {
                    mMinuteNum!!.setBackgroundResource(R.drawable.rounded_corners_full_small_edge)
                    val gdMinute = mMinuteNum!!.background as GradientDrawable
                    gdMinute.setColor(Color.parseColor("#E5E4E2"))
                    mMinuteNum!!.text = mMinuteNumber.toString()
                    mMinuteNum!!.setTextColor(ContextCompat.getColor(this, R.color.black))
                }

                if(mSecNum!!.visibility != View.GONE) {
                    mSecNum!!.setBackgroundResource(R.drawable.rounded_corners_full_small_edge)
                    val gdSec = mSecNum!!.background as GradientDrawable
                    gdSec.setColor(Color.parseColor("#E5E4E2"))
                    mSecNum!!.text = mSecondNumber.toString()
                    mSecNum!!.setTextColor(ContextCompat.getColor(this, R.color.black))
                }

                startTimer()
            } else {
                mCountDownContainer!!.visibility = View.GONE
            }
            mStoriesProgressView.startStories(mStoryItemPosition)
        } else if (item.fileType.equals(Constants.STORY_VIDEO_KEY)) {
            mVideoView.visibility = View.VISIBLE
            mIvStory.visibility = View.INVISIBLE
            if(mCountDownContainer != null) {
                mCountDownContainer!!.visibility = View.GONE
            }
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

    private fun setTimerValues() {
        var timeDifInSec: Int = AppUtils.calculateTimeDifferenceInSec(
            mStories!!.getItems()!![mStoryItemPosition]
                .countdown!!.endDateTime
        )

        if (timeDifInSec == 0) {
            mCountDownContainer!!.visibility = View.GONE
            Log.e(LOG_TAG, "Something went wrong when calculating the time difference!!")
            return
        } else if (timeDifInSec < 0) {
            mWeekNumber = 0
            mDayNumber = 0
            mHourNumber = 0
            mMinuteNumber = 0
            mSecondNumber = 0

            if(!mStories!!.getItems()!![mStoryItemPosition].countdown!!.endAnimationImageUrl.isNullOrEmpty()) {
                startCountdownEndAnimation()
            }
        }

        when (mStories!!.getItems()!![mStoryItemPosition].countdown!!.displayType) {

            "dhms" -> {
                mWeekNum!!.visibility = View.GONE
                mDayNum!!.visibility = View.VISIBLE
                mHourNum!!.visibility = View.VISIBLE
                mMinuteNum!!.visibility = View.VISIBLE
                mSecNum!!.visibility = View.VISIBLE
                mWeekStr!!.visibility = View.GONE
                mDayStr!!.visibility = View.VISIBLE
                mHourStr!!.visibility = View.VISIBLE
                mMinuteStr!!.visibility = View.VISIBLE
                mSecStr!!.visibility = View.VISIBLE
                mDivider1!!.visibility = View.GONE
                mDivider2!!.visibility = View.VISIBLE
                mDivider3!!.visibility = View.VISIBLE
                mDivider4!!.visibility = View.VISIBLE

                if (timeDifInSec > 0) {
                    mDayNumber = (timeDifInSec / (60 * 60 * 24)).toShort()
                    timeDifInSec -= mDayNumber * 60 * 60 * 24
                    mHourNumber = (timeDifInSec / (60 * 60)).toShort()
                    timeDifInSec -= mHourNumber * 60 * 60
                    mMinuteNumber = (timeDifInSec / 60).toShort()
                    timeDifInSec -= mMinuteNumber * 60
                    mSecondNumber = timeDifInSec.toShort()
                }
            }

            "dhm" -> {
                mWeekNum!!.visibility = View.GONE
                mDayNum!!.visibility = View.VISIBLE
                mHourNum!!.visibility = View.VISIBLE
                mMinuteNum!!.visibility = View.VISIBLE
                mSecNum!!.visibility = View.GONE
                mWeekStr!!.visibility = View.GONE
                mDayStr!!.visibility = View.VISIBLE
                mHourStr!!.visibility = View.VISIBLE
                mMinuteStr!!.visibility = View.VISIBLE
                mSecStr!!.visibility = View.GONE
                mDivider1!!.visibility = View.GONE
                mDivider2!!.visibility = View.VISIBLE
                mDivider3!!.visibility = View.VISIBLE
                mDivider4!!.visibility = View.GONE

                if (timeDifInSec > 0) {
                    mDayNumber = (timeDifInSec / (60 * 60 * 24)).toShort()
                    timeDifInSec -= mDayNumber * 60 * 60 * 24
                    mHourNumber = (timeDifInSec / (60 * 60)).toShort()
                    timeDifInSec -= mHourNumber * 60 * 60
                    mMinuteNumber = (timeDifInSec / 60).toShort()
                }
            }
            "d" -> {
                mWeekNum!!.visibility = View.GONE
                mDayNum!!.visibility = View.VISIBLE
                mHourNum!!.visibility = View.GONE
                mMinuteNum!!.visibility = View.GONE
                mSecNum!!.visibility = View.GONE
                mWeekStr!!.visibility = View.GONE
                mDayStr!!.visibility = View.VISIBLE
                mHourStr!!.visibility = View.GONE
                mMinuteStr!!.visibility = View.GONE
                mSecStr!!.visibility = View.GONE
                mDivider1!!.visibility = View.GONE
                mDivider2!!.visibility = View.GONE
                mDivider3!!.visibility = View.GONE
                mDivider4!!.visibility = View.GONE

                if (timeDifInSec > 0) {
                    mDayNumber = (timeDifInSec / (60 * 60 * 24)).toShort()
                }
            }
            else -> {
                mWeekNum!!.visibility = View.VISIBLE
                mDayNum!!.visibility = View.VISIBLE
                mHourNum!!.visibility = View.VISIBLE
                mMinuteNum!!.visibility = View.VISIBLE
                mSecNum!!.visibility = View.VISIBLE
                mWeekStr!!.visibility = View.VISIBLE
                mDayStr!!.visibility = View.VISIBLE
                mHourStr!!.visibility = View.VISIBLE
                mMinuteStr!!.visibility = View.VISIBLE
                mSecStr!!.visibility = View.VISIBLE
                mDivider1!!.visibility = View.VISIBLE
                mDivider2!!.visibility = View.VISIBLE
                mDivider3!!.visibility = View.VISIBLE
                mDivider4!!.visibility = View.VISIBLE

                if (timeDifInSec > 0) {
                    mWeekNumber = (timeDifInSec / (60 * 60 * 24 * 7)).toShort()
                    timeDifInSec -= mWeekNumber * 60 * 60 * 24 * 7
                    mDayNumber = (timeDifInSec / (60 * 60 * 24)).toShort()
                    timeDifInSec -= mDayNumber * 60 * 60 * 24
                    mHourNumber = (timeDifInSec / (60 * 60)).toShort()
                    timeDifInSec -= mHourNumber * 60 * 60
                    mMinuteNumber = (timeDifInSec / 60).toShort()
                    timeDifInSec -= mMinuteNumber * 60
                    mSecondNumber = timeDifInSec.toShort()
                }
            }
        }
    }

    private fun startTimer() {
        mTimerCountDown = Timer("CountDownTimer", false)
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                reAdjustTimerViews()
            }
        }
        mTimerCountDown!!.schedule(task, TIMER_SCHEDULE.toLong(), TIMER_PERIOD.toLong())
    }

    private fun reAdjustTimerViews() {
        calculateTimeFields()
        runOnUiThread {
            try {
                if (mWeekNum!!.visibility != View.GONE) {
                    mWeekNum!!.text = mWeekNumber.toString()
                }
                if (mDayNum!!.visibility != View.GONE) {
                    mDayNum!!.text = mDayNumber.toString()
                }
                if (mHourNum!!.visibility != View.GONE) {
                    mHourNum!!.text = mHourNumber.toString()
                }
                if (mMinuteNum!!.visibility != View.GONE) {
                    mMinuteNum!!.text = mMinuteNumber.toString()
                }
                if (mSecNum!!.visibility != View.GONE) {
                    mSecNum!!.text = mSecondNumber.toString()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.e(
                    LOG_TAG,
                    "The fields for countdown timer could not be set!"
                )
            }
        }
    }

    private fun calculateTimeFields() {
        when (mStories!!.getItems()!![mStoryItemPosition].countdown!!.displayType) {
            "dhms", "dhm", "d" -> {
                if (mSecondNumber > 0) {
                    mSecondNumber--
                } else {
                    mSecondNumber = 59
                    if (mMinuteNumber > 0) {
                        mMinuteNumber--
                    } else {
                        mMinuteNumber = 59
                        if (mHourNumber > 0) {
                            mHourNumber--
                        } else {
                            mHourNumber = 23
                            if (mDayNumber > 0) {
                                mDayNumber--
                            } else {
                                expireTime()
                            }
                        }
                    }
                }
            }
            else -> {
                if (mSecondNumber > 0) {
                    mSecondNumber--
                } else {
                    mSecondNumber = 59
                    if (mMinuteNumber > 0) {
                        mMinuteNumber--
                    } else {
                        mMinuteNumber = 59
                        if (mHourNumber > 0) {
                            mHourNumber--
                        } else {
                            mHourNumber = 23
                            if (mDayNumber > 0) {
                                mDayNumber--
                            } else {
                                mDayNumber = 6
                                if (mWeekNumber > 0) {
                                    mWeekNumber--
                                } else {
                                    expireTime()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun expireTime() {
        mSecondNumber = 0
        mMinuteNumber = 0
        mHourNumber = 0
        mDayNumber = 0
        mWeekNumber = 0
        if (mTimerCountDown != null) {
            mTimerCountDown!!.cancel()
        }

        if(!mStories!!.getItems()!![mStoryItemPosition].countdown!!.endAnimationImageUrl.isNullOrEmpty()) {
            startCountdownEndAnimation()
        }
    }

    private fun startCountdownEndAnimation() {
        runOnUiThread {
            mCountdownEndGifView.visibility = View.VISIBLE
            Glide.with(this)
                .load(mStories!!.getItems()!![mStoryItemPosition].countdown!!.endAnimationImageUrl)
                .into(mCountdownEndGifView)
        }
    }


    companion object {
        private const val LOG_TAG = "Story Activity"
        private const val VIDEO_DURATION_OFFSET = 1000
        private const val TIMER_SCHEDULE: Short = 1000
        private const val TIMER_PERIOD: Short = 1000
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