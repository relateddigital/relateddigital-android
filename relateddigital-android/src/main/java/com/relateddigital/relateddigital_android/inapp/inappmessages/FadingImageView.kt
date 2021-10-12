package com.relateddigital.relateddigital_android.inapp.inappmessages

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import com.relateddigital.relateddigital_android.R

class FadingImageView : AppCompatImageView {
    constructor(context: Context?) : super(context!!) {
        initFadingImageView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        initFadingImageView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        initFadingImageView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHeight = height
        mWidth = width
        val parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        val container = parent as LinearLayout
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // For Portrait takeover notifications, we have to fade out into the notification text
            // at the bottom of the screen.
            val root = this.rootView
            val bottomWrapperView = root.findViewById<View>(R.id.ll_in_app_container)

            // bottomWrapperView should have been measured already, so it's height should exist
            // Still, guard against potential weird black magic rendering issues.
            var bottomWrapperHeight = 0
            if (null != bottomWrapperView && bottomWrapperView.height != 0) {
                bottomWrapperHeight = bottomWrapperView.height
            }

            // We don't want the fade out to end right at the beginning of the text, so we give it
            // give it a few extra dp's of room.
            val r = resources
            val extraPx =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, r.displayMetrics)
            mGradientMatrix!!.setScale(
                1f,
                parentHeight + container.paddingBottom - bottomWrapperHeight + extraPx
            )
        } else {
            mGradientMatrix!!.setScale(1f, parentHeight.toFloat())
        }
        mAlphaGradientShader!!.setLocalMatrix(mGradientMatrix)
        mDarkenGradientShader!!.setLocalMatrix(mGradientMatrix)
    }

    override fun draw(canvas: Canvas) {
        // We have to override this low level draw method instead of onDraw, because by the time
        // onDraw is called, the Canvas with the background has already been saved, so we can't
        // actually clear it with our opacity gradient.
        val clip = canvas.clipBounds
        val restoreTo = canvas.saveLayer(
            0f,
            0f,
            clip.width().toFloat(),
            clip.height().toFloat(),
            null,
            Canvas.ALL_SAVE_FLAG
        )
        super.draw(canvas)

        // Only apply the gradient when we're in portrait view
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            canvas.drawRect(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), mAlphaGradientPaint!!)
        }
        canvas.restoreToCount(restoreTo)
    }

    private fun initFadingImageView() {
        // Approach modeled after View.ScrollabilityCache from the framework
        mGradientMatrix = Matrix()
        mAlphaGradientPaint = Paint()
        mAlphaGradientShader = LinearGradient(
            0f,
            0f,
            0f,
            1f,
            intArrayOf(-0x1000000, -0x1000000, -0x1b000000, 0x00000000),
            floatArrayOf(0.0f, 0.7f, 0.8f, 1.0f),
            Shader.TileMode.CLAMP
        )
        mAlphaGradientPaint!!.shader = mAlphaGradientShader
        mAlphaGradientPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        val mDarkenGradientPaint = Paint()
        mDarkenGradientShader = LinearGradient(
            0f,
            0f,
            0f,
            1f,
            intArrayOf(0x00000000, 0x00000000, -0x1000000, -0x1000000),
            floatArrayOf(0.0f, 0.85f, 0.98f, 1.0f),
            Shader.TileMode.CLAMP
        )
        mDarkenGradientPaint.shader = mDarkenGradientShader
        mAlphaGradientPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    private var mGradientMatrix: Matrix? = null
    private var mAlphaGradientPaint: Paint? = null
    private var mAlphaGradientShader: Shader? = null
    private var mDarkenGradientShader: Shader? = null
    private var mHeight = 0
    private var mWidth = 0
}