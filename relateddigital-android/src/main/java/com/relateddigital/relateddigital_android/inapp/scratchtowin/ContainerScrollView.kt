package com.relateddigital.relateddigital_android.inapp.scratchtowin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import androidx.annotation.RequiresApi

class ContainerScrollView : ScrollView {
    private var mIsEnabled = true

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    fun setScrollingState(state: Boolean) {
        mIsEnabled = state
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (mIsEnabled) {
            super.onInterceptTouchEvent(ev)
        } else {
            mIsEnabled = true
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(ev)
    }
}