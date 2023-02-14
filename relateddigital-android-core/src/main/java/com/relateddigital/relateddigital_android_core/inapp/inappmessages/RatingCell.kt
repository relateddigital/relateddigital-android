package com.relateddigital.relateddigital_android_core.inapp.inappmessages

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.relateddigital.relateddigital_android_core.R

class RatingCell : FrameLayout {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {}

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = this.measuredWidth
        val height = this.measuredHeight
        val size = width.coerceAtLeast(height)
        val widthSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        val heightSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        super.onMeasure(widthSpec, heightSpec)
        setMeasuredDimension(widthSpec, heightSpec)
    }

    companion object {
        fun create(parent: ViewGroup?, inflater: LayoutInflater, rating: Int, cellColors: IntArray?): RatingCell {
            val layoutId: Int = R.layout.nps_with_numbers_cell
            val view = inflater.inflate(layoutId, parent, false) as RatingCell
            val rateTextView = view.findViewById<View>(R.id.rate_text) as TextView
            rateTextView.text = rating.toString()
            return view
        }
    }
}