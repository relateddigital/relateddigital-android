package com.relateddigital.relateddigital_android.inapp

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import com.relateddigital.relateddigital_android.util.ColorUtils
import java.util.*

class RatingGrid : GridView {
    private val mAdapter: CellAdapter = CellAdapter()
    private var mRateCount = 0
    private lateinit var mColors: IntArray
    var selectedRate = 0
        private set
    private var mCells = HashMap<Int, IntArray>()

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    fun init(rateCount: Int, colors: IntArray) {
        mRateCount = rateCount
        mColors = colors
        mCells = ColorUtils.calculateGradientColors(mRateCount, mColors)
        numColumns = mCells.size
        stretchMode = STRETCH_COLUMN_WIDTH
        horizontalSpacing = 8
        validateAndUpdate()
    }

    private fun validateAndUpdate() {
        if (adapter == null) {
            adapter = mAdapter
        }
        mAdapter.notifyDataSetChanged()
    }

    private inner class CellAdapter() : BaseAdapter() {
        private val inflater: LayoutInflater = LayoutInflater.from(context)
        override fun isEnabled(position: Int): Boolean {
            return true
        }

        override fun getCount(): Int {
            return mCells.size
        }

        override fun getItem(position: Int): Any {
            return position + 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val ratingCell: RatingCell
            val cellValue = position + 1
            val cellColors = mCells[cellValue]
            ratingCell = if (convertView == null) {
                RatingCell.create(parent, inflater, cellValue, cellColors)
            } else {
                convertView as RatingCell
            }
            val drawable: GradientDrawable?
            if (cellColors!!.size == 2) {
                drawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, cellColors)
            } else {
                drawable = GradientDrawable()
                drawable.setColor(cellColors[0])
            }
            drawable.shape = GradientDrawable.OVAL
            if (cellValue == selectedRate) {
                drawable.setStroke(5, Color.BLACK)
                ratingCell.background = drawable
            } else {
                ratingCell.background = drawable
            }
            ratingCell.setOnClickListener {
                selectedRate = cellValue
                validateAndUpdate()
                invalidateViews()
            }
            return ratingCell
        }

    }
}