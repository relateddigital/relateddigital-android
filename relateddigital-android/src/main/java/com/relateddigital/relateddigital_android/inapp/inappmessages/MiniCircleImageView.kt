package com.relateddigital.relateddigital_android.inapp.inappmessages

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat

class MiniCircleImageView : AppCompatImageView {
    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {
        init()
    }

    private fun init() {
        mWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        if (Build.VERSION.SDK_INT >= 23) {
            mWhitePaint!!.color = resources.getColor(android.R.color.white, null)
        } else {
            mWhitePaint!!.color = ContextCompat.getColor(context, android.R.color.white)
        }
        mWhitePaint!!.style = Paint.Style.STROKE
        val r = resources
        val strokePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, r.displayMetrics)
        mWhitePaint!!.strokeWidth = strokePx
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = mCanvasWidth.toFloat() / 2.toFloat()
        val centerY = mCanvasHeight.toFloat() / 2.toFloat()
        val radius = 0.7f * centerX.coerceAtMost(centerY)
        canvas.drawCircle(centerX, centerY, radius, mWhitePaint!!)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasWidth = w
        mCanvasHeight = h
    }

    private var mWhitePaint: Paint? = null
    private var mCanvasWidth = 0
    private var mCanvasHeight = 0
}