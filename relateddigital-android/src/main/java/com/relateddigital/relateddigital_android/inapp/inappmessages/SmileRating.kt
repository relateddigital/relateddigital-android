package com.relateddigital.relateddigital_android.inapp.inappmessages

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.relateddigital.relateddigital_android.R
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Created by sujith on 11/10/16.
 */
open class SmileRating : BaseRating {
    private var mPlaceHolderSmileColor = Color.WHITE
    private var mAngryColor = Color.parseColor("#f29a68")
    private var mNormalColor = Color.parseColor("#f2dd68")
    private var mDrawingColor = Color.parseColor("#353431")
    private var mTextSelectedColor = Color.BLACK
    private var mTextNonSelectedColor = Color.parseColor("#AEB3B5")
    private var mPlaceholderBackgroundColor = Color.parseColor("#161616")
    private val mNames: Array<String> = resources.getStringArray(R.array.names)
    private val mFaces = arrayOfNulls<Face>(SMILES_LIST.size)
    private val mTouchPoints: MutableMap<Int, Point> = HashMap<Int, Point>()
    private var mSmileGap = 0f
    var isShowingLine = true
        private set
    private var mMainSmileyTransformaFraction = 1f
    private val mPathPaint = Paint()
    private val mBackgroundPaint = Paint()
    private val mPointPaint1 = Paint()
    private val mPointPaint2 = Paint()
    private val mFaceCenter: Point = Point()
    private val mSmilePath = Path()
    private val mPlaceHolderFacePaint = Paint()
    private val mPlaceholderLinePaint = Paint()
    private val mPlaceHolderCirclePaint = Paint()
    private var divisions = 0f
    private val mValueAnimator = ValueAnimator()
    private val mFloatEvaluator = FloatEvaluator()
    private val mColorEvaluator = ArgbEvaluator()
    private val mInterpolator = OvershootInterpolator()
    private var mClickAnalyser: ClickAnalyser? = null
    private val mScaleMatrix = Matrix()
    private val mScaleRect = RectF()
    private val mTouchBounds = RectF()
    private val mDummyDrawPah = Path()
    private val mTextPaint = Paint()

    @Smiley
    private var mSelectedSmile: Int = GREAT

    @SuppressLint("WrongConstant")
    @Smiley
    private var mPreviousSmile: Int = GOOD

    @Smiley
    private var mNearestSmile: Int = GOOD

    @Smiley
    private var mPendingActionSmile: Int = NONE
    private var mSmileys: Smileys? = null
    private var mWidth = 0f
    private var mHeight = 0f
    private var mCenterY = 0f
    private var mFromRange = 0f
    private var mToRange = 0f
    private var mPrevX = 0f
    private var mFaceClickEngaged = false
    private var mOnRatingSelectedListener: OnRatingSelectedListener? = null
    private var mOnSmileySelectionListener: OnSmileySelectionListener? = null
    private var mPlaceHolderScale = 1f
    private var mSmileyNotSelectedPreviously = true
    private var isIndicator = false

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        parseAttrs(attrs)
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        parseAttrs(attrs)
        init()
    }

    private fun parseAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SmileRating)
            mAngryColor = a.getColor(R.styleable.SmileRating_angryColor, mAngryColor)
            mNormalColor = a.getColor(R.styleable.SmileRating_normalColor, mNormalColor)
            mDrawingColor = a.getColor(R.styleable.SmileRating_drawingColor, mDrawingColor)
            mPlaceHolderSmileColor = a.getColor(R.styleable.SmileRating_placeHolderSmileColor,
                    mPlaceHolderSmileColor)
            mPlaceholderBackgroundColor = a.getColor(R.styleable.SmileRating_placeHolderBackgroundColor,
                    mPlaceholderBackgroundColor)
            mTextSelectedColor = a.getColor(R.styleable.SmileRating_textSelectionColor,
                    mTextSelectedColor)
            mTextNonSelectedColor = a.getColor(R.styleable.SmileRating_textNonSelectionColor,
                    mTextNonSelectedColor)
            isShowingLine = a.getBoolean(R.styleable.SmileRating_showLine, true)
            isIndicator = a.getBoolean(R.styleable.SmileRating_isIndicator, false)
            a.recycle()
        }
    }

    private fun init() {
        mClickAnalyser = ClickAnalyser.newInstance(resources.displayMetrics.density)
        mTextPaint.isAntiAlias = true
        mTextPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        mPathPaint.isAntiAlias = true
        mPathPaint.strokeWidth = 3f
        mPathPaint.color = mDrawingColor
        mPathPaint.style = Paint.Style.FILL
        mPointPaint1.isAntiAlias = true
        mPointPaint1.color = Color.RED
        mPointPaint1.style = Paint.Style.FILL
        mPointPaint2.isAntiAlias = true
        mPointPaint2.color = Color.BLUE
        mPointPaint2.style = Paint.Style.STROKE
        mBackgroundPaint.isAntiAlias = true
        mBackgroundPaint.style = Paint.Style.FILL
        mPlaceHolderFacePaint.isAntiAlias = true
        mPlaceHolderFacePaint.color = mPlaceHolderSmileColor
        mPlaceHolderFacePaint.style = Paint.Style.FILL
        mPlaceHolderCirclePaint.isAntiAlias = true
        mPlaceHolderCirclePaint.color = mPlaceholderBackgroundColor
        mPlaceHolderCirclePaint.style = Paint.Style.FILL
        mPlaceholderLinePaint.isAntiAlias = true
        mPlaceholderLinePaint.color = mPlaceholderBackgroundColor
        mPlaceholderLinePaint.style = Paint.Style.STROKE
        mValueAnimator.duration = 250
        mValueAnimator.addListener(mAnimatorListener)
        mValueAnimator.addUpdateListener(mAnimatorUpdateListener)
        mValueAnimator.interpolator = AccelerateDecelerateInterpolator()
        selectedSmile = GREAT
    }

    private val mAnimatorUpdateListener = AnimatorUpdateListener { animation ->
        if (mSmileyNotSelectedPreviously) {
            mMainSmileyTransformaFraction = animation.animatedFraction
            if (NONE == mSelectedSmile) {
                mMainSmileyTransformaFraction = 1f - mMainSmileyTransformaFraction
            }
            invalidate()
        } else {
            val anim = animation.animatedValue as Float
            moveSmile(anim)
        }
    }
    private val mAnimatorListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            if (NONE != mSelectedSmile) {
                moveSmile(mTouchPoints[mSelectedSmile]!!.x)
            }
        }

        override fun onAnimationEnd(animation: Animator) {
            notifyListener()
        }

        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    }

    @SuppressLint("WrongConstant")
    private fun notifyListener() {
        @SuppressLint("WrongConstant") val reselected = mPreviousSmile == selectedSmile
        mPreviousSmile = mSelectedSmile
        mPendingActionSmile = mSelectedSmile
        if (mOnSmileySelectionListener != null) {
            mOnSmileySelectionListener!!.onSmileySelected(mSelectedSmile, reselected)
        }
        if (mOnRatingSelectedListener != null) {
            mOnRatingSelectedListener!!.onRatingSelected(rating, reselected)
        }
    }

    fun setOnSmileySelectionListener(l: OnSmileySelectionListener?) {
        mOnSmileySelectionListener = l
    }

    fun setOnRatingSelectedListener(l: OnRatingSelectedListener?) {
        mOnRatingSelectedListener = l
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth.toFloat()
        // mHeight = mWidth / 5.3f;
        mHeight = mWidth / (5.3f * 1.3f)
        mCenterY = mHeight / 2f
        mFaceCenter.y = mCenterY
        divisions = mHeight / 32f
        mTextPaint.textSize = mHeight / 4.5f
        mSmileys = Smileys.newInstance(mWidth.roundToInt(), mHeight.roundToInt())
        setMeasuredDimension(mWidth.roundToInt(), (mHeight + mHeight * 0.48).roundToInt())
        createTouchPoints()
        mPlaceholderLinePaint.strokeWidth = mHeight * 0.05f
        setSelectedSmile(mPendingActionSmile, mTouchPoints[mPendingActionSmile], check = false, animate = false)
        Log.i(TAG, "Selected smile:" + getSmileName(mPendingActionSmile))
    }

    private fun createTouchPoints() {
        mTouchPoints.clear()
        val divisions = mWidth / 5f
        val divCenter = divisions / 2f
        mSmileGap = (divisions - mHeight) / 2f
        mFromRange = mSmileGap + mHeight / 2
        mToRange = mWidth - mHeight / 2 - mSmileGap
        val count: Int = SMILES_LIST.size
        for (i in 0 until count) {
            mFaces[i] = createFace(i, mCenterY)
            mTouchPoints[SMILES_LIST[i]] = Point(divisions * i + divCenter, mCenterY)
        }
    }

    private fun createFace(index: Int, centerY: Float): Face {
        val face = Face()
        face.smileType = index
        getSmiley(mSmileys, index * 0.25f, divisions, mFromRange, mToRange, face.place,
                face.smile, centerY)
        face.place.y = centerY
        return face
    }

    @SuppressLint("WrongConstant")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val start: Point = mFaces[0]!!.place
        val end: Point = mFaces[mFaces.size - 1]!!.place
        if (isShowingLine) {
            canvas.drawLine(start.x, start.y, end.x, end.y, mPlaceholderLinePaint)
        }
        for (face in mFaces) {
            var scale = getScale(face!!.smileType)
            canvas.drawCircle(face.place.x, face.place.y,
                    scale * (mHeight / 2), mPlaceHolderCirclePaint)
            mScaleMatrix.reset()
            face.smile.computeBounds(mScaleRect, true)
            if (mSmileyNotSelectedPreviously) {
                val nonSelectedScale = getScale(NONE)
                mScaleMatrix.setScale(nonSelectedScale, nonSelectedScale,
                        mScaleRect.centerX(), mScaleRect.centerY())
                if (mSelectedSmile == face.smileType) {
                    scale = mFloatEvaluator.evaluate(1 - mMainSmileyTransformaFraction, 0, nonSelectedScale)
                }
            } else {
                mScaleMatrix.setScale(scale, scale,
                        mScaleRect.centerX(), mScaleRect.centerY())
            }
            mDummyDrawPah.reset()
            mDummyDrawPah.addPath(face.smile, mScaleMatrix)
            canvas.drawPath(mDummyDrawPah, mPlaceHolderFacePaint)
            val transY = 0.15f - scale * 0.15f
            mTextPaint.color = mColorEvaluator.evaluate((transY / 0.15f - 0.2f) / 0.8f,
                    mTextNonSelectedColor, mTextSelectedColor) as Int
            drawTextCentered(getSmileName(face.smileType)!!, face.place.x,
                    face.place.y + mHeight * (0.70f + transY), mTextPaint, canvas)
        }
        if (!mSmilePath.isEmpty) {
            if (mSmileyNotSelectedPreviously) {
                Log.i(TAG, "Non selection")
                /*mPathPaint.setAlpha(Math.round(255 * mMainSmileyTransformaFraction));
                mBackgroundPaint.setAlpha(Math.round(255 * mMainSmileyTransformaFraction));*/mPathPaint.color = (mColorEvaluator
                        .evaluate(mMainSmileyTransformaFraction, mPlaceHolderFacePaint.color, mDrawingColor) as Int)
                mBackgroundPaint.color = mColorEvaluator
                        .evaluate(mMainSmileyTransformaFraction, mPlaceHolderCirclePaint.color,
                                if (mSelectedSmile == TERRIBLE || mPreviousSmile == TERRIBLE) mAngryColor else mNormalColor) as Int
                mScaleMatrix.reset()
                mSmilePath.computeBounds(mScaleRect, true)
                val nonSelectedScale = mFloatEvaluator.evaluate(
                        mInterpolator.getInterpolation(mMainSmileyTransformaFraction), getScale(NONE), 1f)
                mScaleMatrix.setScale(nonSelectedScale, nonSelectedScale,
                        mScaleRect.centerX(), mScaleRect.centerY())
                mDummyDrawPah.reset()
                mDummyDrawPah.addPath(mSmilePath, mScaleMatrix)
                canvas.drawCircle(mFaceCenter.x, mFaceCenter.y,
                        nonSelectedScale * (mHeight / 2f), mBackgroundPaint)
                canvas.drawPath(mDummyDrawPah, mPathPaint)
            } else {
                canvas.drawCircle(mFaceCenter.x, mFaceCenter.y, mHeight / 2f, mBackgroundPaint)
                canvas.drawPath(mSmilePath, mPathPaint)
            }
        }
    }

    private fun drawTextCentered(text: String, x: Float, y: Float, paint: Paint, canvas: Canvas) {
        val xPos = x - paint.measureText(text) / 2
        val yPos = y - (paint.descent() + paint.ascent()) / 2
        canvas.drawText(text, xPos, yPos, paint)
    }

    private fun getScale(@Smiley smile: Int): Float {
        if (mSelectedSmile == NONE) {
            return 0.80f
        }
        return if (smile == mNearestSmile) {
            mPlaceHolderScale
        } else 0.80f
    }

    private fun getSmileName(smile: Int): String? {
        return if (smile >= mNames.size || smile < 0) {
            null
        } else mNames[smile]
    }

    fun setNameForSmile(@Smiley smile: Int, @StringRes stringRes: Int) {
        setNameForSmile(smile, resources.getString(stringRes))
    }

    private fun setNameForSmile(@Smiley smile: Int, title: String?) {
        mNames[smile] = title ?: ""
        invalidate()
    }

    fun setAngryColor(@ColorInt color: Int) {
        mAngryColor = color
        getSmiley(mSmileys, getFractionBySmiley(mSelectedSmile), divisions, mFromRange, mToRange,
                mFaceCenter, mSmilePath, mCenterY)
    }

    fun setNormalColor(@ColorInt color: Int) {
        mNormalColor = color
        getSmiley(mSmileys, getFractionBySmiley(mSelectedSmile), divisions, mFromRange, mToRange,
                mFaceCenter, mSmilePath, mCenterY)
    }

    fun setDrawingColor(@ColorInt color: Int) {
        mDrawingColor = color
        mPathPaint.color = mDrawingColor
        invalidate()
    }

    fun setTextSelectedColor(@ColorInt color: Int) {
        mTextSelectedColor = color
        invalidate()
    }

    fun setTextNonSelectedColor(@ColorInt color: Int) {
        mTextNonSelectedColor = color
        invalidate()
    }

    fun setPlaceHolderSmileColor(@ColorInt color: Int) {
        mPlaceHolderSmileColor = color
        mPlaceHolderFacePaint.color = mPlaceHolderSmileColor
        invalidate()
    }

    fun setPlaceholderBackgroundColor(@ColorInt color: Int) {
        mPlaceholderBackgroundColor = color
        mPlaceholderLinePaint.color = mPlaceholderBackgroundColor
        mPlaceHolderCirclePaint.color = mPlaceholderBackgroundColor
        invalidate()
    }

    fun setShowLine(showLine: Boolean) {
        isShowingLine = showLine
        invalidate()
    }

    fun setTypeface(typeface: Typeface?) {
        var typefaceLoc = typeface
        if (typefaceLoc == null) {
            typefaceLoc = Typeface.DEFAULT
        }
        mTextPaint.typeface = typefaceLoc
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isIndicator) {
            return super.onTouchEvent(event)
        }
        val action = event.action
        val x = event.x
        val y = event.y
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mClickAnalyser!!.start(x, y)
                mFaceClickEngaged = isSmileyBounds(mFaceCenter.x, mFaceCenter.y,
                        x, y, mCenterY)
                mPrevX = x
            }
            MotionEvent.ACTION_MOVE -> {
                mClickAnalyser!!.move(x, y)
                if (mClickAnalyser!!.isMoved && mFaceClickEngaged) {
                    moveSmile(mFaceCenter.x - (mPrevX - x))
                }
                mPrevX = x
            }
            MotionEvent.ACTION_UP -> {
                mFaceClickEngaged = false
                mClickAnalyser!!.stop(x, y)
                if (!mClickAnalyser!!.isMoved) {
                    onClickView(x, y)
                } else {
                    positionSmile()
                }
            }
        }
        return true
    }

    private fun positionSmile() {
        if (NONE == mSelectedSmile) {
            return
        }
        val currentPosition: Float = mFaceCenter.x
        var distance = Int.MAX_VALUE.toFloat()
        var point: Point? = null
        @Smiley var smile: Int = NONE
        for (s in mTouchPoints.keys) {
            val p: Point? = mTouchPoints[s]
            val d: Float = abs(p!!.x - currentPosition)
            if (distance > d) {
                point = p
                smile = s
                distance = d
            }
        }
        setSelectedSmile(smile, point, check = false, animate = true)
    }

    private fun moveSmile(position: Float) {
        val fraction = (position - mFromRange) / (mToRange - mFromRange)
        moveSmileByFraction(fraction)
    }

    private fun moveSmileByFraction(fraction: Float) {
        var fractionLoc = fraction
        fractionLoc = fractionLoc.coerceAtMost(1.0f).coerceAtLeast(0.0f)
        getSmiley(mSmileys, fractionLoc, divisions, mFromRange, mToRange,
                mFaceCenter, mSmilePath, mCenterY)
        invalidate()
    }

    private fun onClickView(x: Float, y: Float) {
        for (smile in mTouchPoints.keys) {
            val point: Point? = mTouchPoints[smile]
            val touched = isSmileyBounds(point!!.x, point.y, x, y, mCenterY)
            if (touched) {
                if (smile == selectedSmile) {
                    notifyListener()
                } else {
                    setSelectedSmile(smile, point, true, true)
                }
            }
        }
    }

    private fun isSmileyBounds(cx: Float, cy: Float, tx: Float, ty: Float, width: Float): Boolean {
        mTouchBounds[cx - width, 0f, cx + width] = measuredHeight.toFloat()
        return mTouchBounds.contains(tx, ty)
    }

    /**
     * Set the selected smiley
     *
     * @param smile   is the smiley type you want this rating bar to show as selected smile
     * @param animate true if you want to set the selected smiley and animate it,
     * false for no animation
     */
    private fun setSelectedSmile(@Smiley smile: Int, animate: Boolean) {
        mPendingActionSmile = smile
        setSelectedSmile(smile, mTouchPoints[smile], true, animate)
    }

    private fun setSelectedSmile(@Smiley smile: Int, point: Point?, check: Boolean, animate: Boolean) {
        if (mSelectedSmile == smile && check) {
            return
        }
        mSmileyNotSelectedPreviously = if (mSelectedSmile == NONE) {
            true
        } else smile == NONE
        mSelectedSmile = smile
        if (mFaceCenter == null) {
            return
        }

        mValueAnimator.setFloatValues(mFaceCenter.x, point?.x ?: 0f)
        when {
            animate -> {
                mValueAnimator.start()
            }
            mSelectedSmile == NONE -> {
                if (!mSmilePath.isEmpty) {
                    mSmilePath.reset()
                }
                invalidate()
            }
            point != null -> {
                moveSmile(point.x)
            }
        }
    }
    /**
     * @return the current selected smiley [.TERRIBLE] ,[.BAD],
     * [.OKAY],[.GOOD],[.GREAT]
     */
    /**
     * Set the selected smiley
     */
    @get:Smiley
    var selectedSmile: Int
        get() = mSelectedSmile
        set(smile) {
            setSelectedSmile(smile, false)
        }

    /**
     * @return the selected rating level from range of 1 to 5
     */
    val rating: Int
        get() = selectedSmile + 1

    /**
     * Evaluates click actions using touch events
     */
    protected class ClickAnalyser(private val mDensity: Float) {
        private var mPressX = 0f
        private var mPressY = 0f
        private var mPressStartTime: Long = 0
        var isMoved = false
            private set
        private var mClickEventOccured = true
        fun start(x: Float, y: Float) {
            mPressX = x
            mPressY = y
            isMoved = false
            mClickEventOccured = true
            mPressStartTime = System.currentTimeMillis()
        }

        /**
         * returns long press
         *
         * @param x
         * @param y
         * @return
         */
        fun move(x: Float, y: Float) {
            val dist = distance(mPressX, mPressY, x, y)
            val time = System.currentTimeMillis() - mPressStartTime
            if (!isMoved && dist > MAX_CLICK_DISTANCE) {
                isMoved = true
            }
            if (time > MAX_CLICK_DURATION || isMoved) {
                mClickEventOccured = false
            }
        }

        fun stop(x: Float, y: Float): Boolean {
            move(x, y)
            return mClickEventOccured
        }

        private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            val dx = x1 - x2
            val dy = y1 - y2
            val distanceInPx: Float = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
            return pxToDp(distanceInPx)
        }

        private fun pxToDp(px: Float): Float {
            return px / mDensity
        }

        companion object {
            private const val MAX_CLICK_DISTANCE = 20
            private const val MAX_CLICK_DURATION = 200
            fun newInstance(density: Float): ClickAnalyser {
                return ClickAnalyser(density)
            }
        }
    }

    private fun getFractionBySmiley(@Smiley smiley: Int): Float {
        when (smiley) {
            BAD -> return 1f
            GOOD -> return 0.75f
            GREAT -> return 0.5f
            OKAY -> return 0.25f
            TERRIBLE -> return 0f
            NONE -> {
                return  0f
            }
        }
        return 0f
    }

    private fun getSmiley(smileys: Smileys?, fraction: Float, divisions: Float, fromRange: Float,
                          toRange: Float, point: Point?, smilePath: Path,
                          centerY: Float) {
        var fractionLoc = fraction
        if (smileys == null) {
            return
        }
        val actualTranslation = mFloatEvaluator.evaluate(fractionLoc, fromRange, toRange)
        point!!.x = actualTranslation
        val trans = actualTranslation - centerY
        if (fractionLoc > 0.75f) {
            fractionLoc -= 0.75f
            fractionLoc *= 4f
            findNearestSmile(fractionLoc, GOOD, GREAT)
            mBackgroundPaint.color = mNormalColor
            transformSmile(trans, fractionLoc, smilePath,
                    smileys.getSmile(GOOD)!!, smileys.getSmile(GREAT)!!, mFloatEvaluator)
            createEyeLocation(smileys, divisions, fractionLoc, actualTranslation, GREAT, smilePath, smilePath, centerY)
        } else if (fraction > 0.50f) {
            fractionLoc -= 0.50f
            fractionLoc *= 4f
            findNearestSmile(fractionLoc, OKAY, GOOD)
            mBackgroundPaint.color = mNormalColor
            transformSmile(trans, fractionLoc, smilePath,
                    smileys.getSmile(OKAY)!!, smileys.getSmile(GOOD)!!, mFloatEvaluator)
            createEyeLocation(smileys, divisions, fractionLoc, actualTranslation, GOOD, smilePath, smilePath, centerY)
        } else if (fraction > 0.25f) {
            fractionLoc -= 0.25f
            fractionLoc *= 4f
            findNearestSmile(fractionLoc, BAD, OKAY)
            mBackgroundPaint.color = mNormalColor
            transformSmile(trans, fractionLoc, smilePath,
                    smileys.getSmile(BAD)!!, smileys.getSmile(OKAY)!!, mFloatEvaluator)
            createEyeLocation(smileys, divisions, fractionLoc, actualTranslation, BAD, smilePath, smilePath, centerY)
        } else if (fractionLoc >= 0) {
            fractionLoc *= 4f
            findNearestSmile(fractionLoc, TERRIBLE, BAD)
            mBackgroundPaint.color = mColorEvaluator.evaluate(fractionLoc, mAngryColor, mNormalColor) as Int
            transformSmile(trans, fractionLoc, smilePath,
                    smileys.getSmile(TERRIBLE)!!, smileys.getSmile(BAD)!!, mFloatEvaluator)
            createEyeLocation(smileys, divisions, fractionLoc, actualTranslation, TERRIBLE, smilePath, smilePath, centerY)
        } else {
            if (!mSmilePath.isEmpty) {
                mSmilePath.reset()
            }
        }
    }

    private fun findNearestSmile(fraction: Float, @Smiley leftSmile: Int, @Smiley rightSmile: Int) {
        if (fraction < 0.5f) {
            mPlaceHolderScale = limitNumberInRange(fraction * 2)
            mNearestSmile = leftSmile
        } else {
            mPlaceHolderScale = limitNumberInRange(1f - (fraction - 0.5f) * 2)
            mNearestSmile = rightSmile
        }
    }

    private fun limitNumberInRange(num: Float): Float {
        // The range is going to be in between 0 to 0.80
        var numLoc = num
        numLoc *= 0.80f
        return numLoc
    }

    private fun createEyeLocation(smileys: Smileys, divisions: Float, fraction: Float, actualTranslation: Float, @Smiley smile: Int, leftEye: Path, rightEye: Path, centerY: Float) {
        val eyeLeft: Eye = EyeEmotion.prepareEye(smileys.getEye(Eye.LEFT), mFloatEvaluator, fraction, smile)
        val eyeRight: Eye = EyeEmotion.prepareEye(smileys.getEye(Eye.RIGHT), mFloatEvaluator, fraction, smile)
        eyeLeft.radius = divisions * 2.5f
        eyeRight.radius = divisions * 2.5f
        eyeLeft.center!!.x = divisions * 11f + actualTranslation - centerY
        eyeLeft.center!!.y = centerY * 0.70f
        eyeRight.center!!.x = divisions * 21f + actualTranslation - centerY
        eyeRight.center!!.y = centerY * 0.70f
        eyeLeft.fillPath(leftEye)
        eyeRight.fillPath(rightEye)
    }

    private class Face {
        var place: Point = Point()
        var smile = Path()

        @Smiley
        var smileType = 0 /*Path leftEye = new Path();
        Path rightEye = new Path();*/
    }

    interface OnSmileySelectionListener {
        /**
         * Called when a smiley is selected
         *
         * @param smiley     is the type of smiley the user selected ([.GREAT], [.BAD],
         * [.OKAY],[.GOOD],[.GREAT])
         * @param reselected is false when user selects different smiley that previously selected
         * one true when the same smiley is selected. Except if it first time,
         * then the value will be false.
         */
        fun onSmileySelected(@Smiley smiley: Int, reselected: Boolean)
    }

    interface OnRatingSelectedListener {
        /**
         * Called when a smiley is selected
         *
         * @param level      is the level of the rating (0 to 4)
         * @param reselected is false when user selects different smiley that previously selected
         * one true when the same smiley is selected. Except if it first time,
         * then the value will be false.
         */
        fun onRatingSelected(level: Int, reselected: Boolean)
    }

    companion object {
        private const val TAG = "RatingView"
    }
}