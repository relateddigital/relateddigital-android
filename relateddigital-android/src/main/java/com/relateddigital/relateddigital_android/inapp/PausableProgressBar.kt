package com.relateddigital.relateddigital_android.inapp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.Transformation
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.relateddigital.relateddigital_android.R

internal class PausableProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val mFrontProgressView: View
    private val mMaxProgressView: View
    private var mAnimation: PausableScaleAnimation? = null
    private var mDuration = DEFAULT_PROGRESS_DURATION.toLong()
    private var mCallback: Callback? = null

    internal interface Callback {
        fun onStartProgress()
        fun onFinishProgress()
    }

    constructor(context: Context) : this(context, null) {
        mAnimation = PausableScaleAnimation(
            0f, 1f, 1f, 1f,
            Animation.ABSOLUTE, 0f, Animation.RELATIVE_TO_SELF, 0f
        )
    }

    fun setDuration(duration: Long) {
        mDuration = duration
    }

    fun setCallback(callback: Callback) {
        mCallback = callback
    }

    fun setMax() {
        finishProgress(true)
    }

    fun setMin() {
        finishProgress(false)
    }

    fun setMinWithoutCallback() {
        mMaxProgressView.setBackgroundResource(R.color.progress_secondary)
        mMaxProgressView.visibility = VISIBLE
        if (mAnimation != null) {
            mAnimation!!.setAnimationListener(null)
            mAnimation!!.cancel()
        }
    }

    fun setMaxWithoutCallback() {
        mMaxProgressView.setBackgroundResource(R.color.progress_max_active)
        mMaxProgressView.visibility = VISIBLE
        if (mAnimation != null) {
            mAnimation!!.setAnimationListener(null)
            mAnimation!!.cancel()
        }
    }

    private fun finishProgress(isMax: Boolean) {
        if (isMax) mMaxProgressView.setBackgroundResource(R.color.progress_max_active)
        mMaxProgressView.visibility = if (isMax) VISIBLE else GONE
        if (mAnimation != null) {
            mAnimation!!.setAnimationListener(null)
            mAnimation!!.cancel()
            if (mCallback != null) {
                mCallback!!.onFinishProgress()
            }
        }
    }

    fun startProgress() {
        mMaxProgressView.visibility = GONE
        mAnimation!!.duration = mDuration
        mAnimation!!.interpolator = LinearInterpolator()
        mAnimation!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                mFrontProgressView.visibility = VISIBLE
                if (mCallback != null) mCallback!!.onStartProgress()
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (mCallback != null) mCallback!!.onFinishProgress()
            }
        })
        mAnimation!!.fillAfter = true
        mFrontProgressView.startAnimation(mAnimation)
    }

    fun pauseProgress() {
        if (mAnimation != null) {
            mAnimation!!.pause()
        }
    }

    fun resumeProgress() {
        if (mAnimation != null) {
            mAnimation!!.resume()
        }
    }

    fun clear() {
        if (mAnimation != null) {
            mAnimation!!.setAnimationListener(null)
            mAnimation!!.cancel()
            mAnimation = null
        }
    }

    private class PausableScaleAnimation internal constructor(
        fromX: Float, toX: Float, fromY: Float,
        toY: Float, pivotXType: Int, pivotXValue: Float, pivotYType: Int,
        pivotYValue: Float
    ) :
        ScaleAnimation(
            fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType,
            pivotYValue
        ) {
        private var mElapsedAtPause: Long = 0
        private var mPaused = false
        override fun getTransformation(
            currentTime: Long,
            outTransformation: Transformation,
            scale: Float
        ): Boolean {
            if (mPaused && mElapsedAtPause == 0L) {
                mElapsedAtPause = currentTime - startTime
            }
            if (mPaused) {
                startTime = currentTime - mElapsedAtPause
            }
            return super.getTransformation(currentTime, outTransformation, scale)
        }

        /***
         * pause animation
         */
        fun pause() {
            if (mPaused) return
            mElapsedAtPause = 0
            mPaused = true
        }

        /***
         * resume animation
         */
        fun resume() {
            mPaused = false
        }
    }

    companion object {
        private const val DEFAULT_PROGRESS_DURATION = 2000
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.pausable_progress, this)
        mFrontProgressView = findViewById(R.id.front_progress)
        mMaxProgressView = findViewById(R.id.max_progress) // work around
    }
}