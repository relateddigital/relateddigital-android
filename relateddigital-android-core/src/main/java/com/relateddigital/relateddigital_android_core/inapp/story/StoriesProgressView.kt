package com.relateddigital.relateddigital_android_core.inapp.story

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.relateddigital.relateddigital_android_core.R
import java.util.*

class StoriesProgressView : LinearLayout {
    private val mProgressBarLayoutParam = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
    private val mSpaceLayoutParam = LayoutParams(5, LayoutParams.WRAP_CONTENT)
    private val mProgressBars: MutableList<PausableProgressBar> = ArrayList<PausableProgressBar>()
    private var mStoriesCount = -1

    /**
     * pointer of running animation
     */
    private var mCurrent = -1
    private var mStoriesListener: StoriesListener? = null
    var mIsComplete = false
    private var mIsSkipStart = false
    private var mIsReverseStart = false

    interface StoriesListener {
        fun onNext()
        fun onPrev()
        fun onComplete()
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        orientation = HORIZONTAL
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StoriesProgressView)
        mStoriesCount = typedArray.getInt(R.styleable.StoriesProgressView_progressCount, 0)
        typedArray.recycle()
        bindViews()
    }

    private fun bindViews() {
        mProgressBars.clear()
        removeAllViews()
        for (i in 0 until mStoriesCount) {
            val p: PausableProgressBar = createProgressBar()
            mProgressBars.add(p)
            addView(p)
            if (i + 1 < mStoriesCount) {
                addView(createSpace())
            }
        }
    }

    private fun createProgressBar(): PausableProgressBar {
        val p = PausableProgressBar(context)
        p.layoutParams = mProgressBarLayoutParam
        return p
    }

    private fun createSpace(): View {
        val v = View(context)
        v.layoutParams = mSpaceLayoutParam
        return v
    }

    /**
     * Set story count and create views
     *
     * @param storiesCount story count
     */
    fun setStoriesCount(storiesCount: Int) {
        mStoriesCount = storiesCount
        bindViews()
    }

    /**
     * Set storiesListener
     *
     * @param storiesListener StoriesListener
     */
    fun setStoriesListener(storiesListener: StoriesListener?) {
        mStoriesListener = storiesListener
    }

    /**
     * Skip current story
     */
    fun skip() {
        if (mIsSkipStart || mIsReverseStart) return
        if (mIsComplete) return
        if (mCurrent < 0) return
        val p: PausableProgressBar = mProgressBars[mCurrent]
        mIsSkipStart = true
        p.setMax()
    }

    /**
     * Reverse current story
     */
    fun reverse() {
        if (mIsSkipStart || mIsReverseStart) return
        if (mIsComplete) return
        if (mCurrent < 0) return
        val p: PausableProgressBar = mProgressBars[mCurrent]
        mIsReverseStart = true
        p.setMin()
    }

    /**
     * Set a story's duration
     *
     * @param duration millisecond
     */
    fun setStoryDuration(duration: Long) {
        for (i in mProgressBars.indices) {
            mProgressBars[i].setDuration(duration)
            mProgressBars[i].setCallback(callback(i))
        }
    }

    /**
     * Set stories count and each story duration
     *
     * @param durations milli
     */
    fun setStoriesCountWithDurations(durations: LongArray) {
        mStoriesCount = durations.size
        bindViews()
        for (i in mProgressBars.indices) {
            mProgressBars[i].setDuration(durations[i])
            mProgressBars[i].setCallback(callback(i))
        }
    }

    private fun callback(index: Int): PausableProgressBar.Callback {
        return object : PausableProgressBar.Callback {
            override fun onStartProgress() {
                mCurrent = index
            }

            override fun onFinishProgress() {
                if (mIsReverseStart) {
                    if (mStoriesListener != null) mStoriesListener!!.onPrev()
                    mIsReverseStart = false
                    return
                }
                val next = mCurrent + 1
                if (next <= mProgressBars.size - 1) {
                    if (mStoriesListener != null) mStoriesListener!!.onNext()
                } else {
                    mIsComplete = true
                    if (mStoriesListener != null) mStoriesListener!!.onComplete()
                }
                mIsSkipStart = false
            }
        }
    }

    /**
     * Start progress animation
     */
    fun startStories() {
        mProgressBars[0].startProgress()
    }

    /**
     * Start progress animation from specific progress
     */
    fun startStories(from: Int) {
        for (i in 0 until from) {
            mProgressBars[i].setMaxWithoutCallback()
        }
        mProgressBars[from].startProgress()
    }

    /**
     * Need to call when Activity or Fragment destroy
     */
    fun destroy() {
        for (p in mProgressBars) {
            p.clear()
        }
    }

    /**
     * Pause story
     */
    fun pause() {
        if (mCurrent < 0) return
        mProgressBars[mCurrent].pauseProgress()
    }

    /**
     * Resume story
     */
    fun resume() {
        if (mCurrent < 0) return
        mProgressBars[mCurrent].resumeProgress()
    }

    companion object {
        private val TAG = StoriesProgressView::class.java.simpleName
    }
}