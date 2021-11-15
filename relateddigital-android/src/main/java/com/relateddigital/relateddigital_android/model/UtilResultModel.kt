package com.relateddigital.relateddigital_android.model

import java.util.ArrayList

class UtilResultModel {
    val numbers = ArrayList<Int>()
    val startIdxs = ArrayList<Int>()
    val endIdxs = ArrayList<Int>()
    var message: String? = null
    var isTag = false

    fun addNumber(number: Int) {
        numbers.add(number)
    }

    fun addStartIdx(startIdx: Int) {
        startIdxs.add(startIdx)
    }

    fun addEndIdx(endIdx: Int) {
        endIdxs.add(endIdx)
    }
}