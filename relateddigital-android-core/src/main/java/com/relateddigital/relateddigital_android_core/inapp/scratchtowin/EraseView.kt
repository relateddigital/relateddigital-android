package com.relateddigital.relateddigital_android_core.inapp.scratchtowin

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.relateddigital.relateddigital_android_core.R

class EraseView(private val mContext: Context, attrs: AttributeSet?) : View(mContext, attrs) {
    private val mSourceBitmap: Bitmap
    private val mSourceCanvas = Canvas()
    private val mDestPaint = Paint()
    private val mDestPath = Path()
    private var mColor: Int
    private var isItEnabled = false
    private var isEmailEntered = false
    private var isConsent1Entered = false
    private var isConsent2Entered = false
    private var invalidEmailMessage: String? = null
    private var missingConsentMessage: String? = null
    private var mContainer: ContainerScrollView? = null
    private var mListener: ScratchToWinInterface? = null
    private var mCodeWidth = 0
    private var mCodeHeight = 0
    private var isCompleted = false
    override fun onDraw(canvas: Canvas) {
        //Draw path
        mSourceCanvas.drawPath(mDestPath, mDestPaint)

        //Draw bitmap
        canvas.drawBitmap(mSourceBitmap, 0f, 0f, null)
        super.onDraw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isItEnabled) {
            if (!isCompleted) {
                if (calculatePercentageErased() >= PERCENTAGE_THRESHOLD) {
                    isCompleted = true
                    mListener!!.onScratchingComplete()
                }
            }
            mContainer!!.setScrollingState(false)
            val xPos = event.x
            val yPos = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> mDestPath.moveTo(xPos, yPos)
                MotionEvent.ACTION_MOVE -> mDestPath.lineTo(xPos, yPos)
                else -> return false
            }
            invalidate()
            true
        } else {
            if (!isEmailEntered) {
                Toast.makeText(mContext, invalidEmailMessage, Toast.LENGTH_SHORT).show()
            } else if (!isConsent1Entered || !isConsent2Entered) {
                Toast.makeText(mContext, missingConsentMessage, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.missing_subs_email), Toast.LENGTH_SHORT).show()
            }
            false
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mCodeWidth = w
        mCodeHeight = h
        super.onSizeChanged(w, h, oldw, oldh)
    }

    fun setColor(color: Int) {
        mColor = color
        mSourceCanvas.drawColor(mColor)
    }

    fun setEmailStatus(value: Boolean) {
        isEmailEntered = value
    }

    fun setConsent1Status(value: Boolean) {
        isConsent1Entered = value
    }

    fun setConsent2Status(value: Boolean) {
        isConsent2Entered = value
    }

    fun setInvalidEmailMessage(message: String?) {
        invalidEmailMessage = message
    }

    fun setMissingConsentMessage(message: String?) {
        missingConsentMessage = message
    }

    fun setContainer(container: ContainerScrollView?) {
        mContainer = container
    }

    fun setListener(listener: ScratchToWinInterface?) {
        mListener = listener
    }

    fun enableScratching() {
        isItEnabled = true
    }

    private fun calculatePercentageErased(): Double {
        val width = mSourceBitmap.width
        val height = mSourceBitmap.height

        // size of sample rectangles
        val xStep = width / SCALE
        val yStep = height / SCALE

        // center of the first rectangle
        val xInit = xStep / 2
        val yInit = yStep / 2

        // center of the last rectangle
        val xEnd = mCodeWidth - xStep / 2
        val yEnd = mCodeHeight - yStep / 2
        var totalTransparent = 0
        var x = xInit
        while (x <= xEnd) {
            var y = yInit
            while (y <= yEnd) {
                if (mSourceBitmap.getPixel(x, y) == Color.TRANSPARENT) {
                    totalTransparent++
                }
                y += yStep
            }
            x += xStep
        }
        return (totalTransparent.toFloat() / ((mCodeWidth * mCodeHeight).toFloat() / (xStep * yStep).toFloat())).toDouble()
    }

    companion object {
        private const val SCALE = 100
        private const val PERCENTAGE_THRESHOLD = 0.6
    }

    init {
        mColor = Color.parseColor("#000000") //Default black

        //convert drawable file into bitmap
        val rawBitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)

        //convert bitmap into mutable bitmap
        mSourceBitmap = Bitmap.createBitmap(rawBitmap.width, rawBitmap.height, Bitmap.Config.ARGB_8888)
        mSourceCanvas.setBitmap(mSourceBitmap)
        mSourceCanvas.drawColor(mColor)
        mDestPaint.alpha = 0
        mDestPaint.isAntiAlias = true
        mDestPaint.style = Paint.Style.STROKE
        mDestPaint.strokeJoin = Paint.Join.ROUND
        mDestPaint.strokeCap = Paint.Cap.ROUND
        mDestPaint.strokeWidth = 50f
        mDestPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }
}