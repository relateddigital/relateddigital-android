package com.relateddigital.relateddigital_android_core.inapp.inappmessages

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.relateddigital.relateddigital_android_core.R

class NpsWithNumbersView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private var mRatingGrid: RatingGrid? = null
    private var mRateCount: Int
    private var mColors: IntArray
    override fun onFinishInflate() {
        super.onFinishInflate()
        mRatingGrid = findViewById<View>(R.id.rating_grid) as RatingGrid
    }

    fun setColors(colors: IntArray, isFromZero: Boolean) {
        mColors = colors
        mRateCount = if(isFromZero) {
            11
        } else {
            10
        }
        mRatingGrid!!.init(mRateCount, mColors)
    }

    val selectedRate: Int
        get() = mRatingGrid!!.selectedRate

    init {
        LayoutInflater.from(context).inflate(R.layout.nps_with_numbers, this)
        mRateCount = 10
        mColors = intArrayOf(Color.GREEN)
    }
}