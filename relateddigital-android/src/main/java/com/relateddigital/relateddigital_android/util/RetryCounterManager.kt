package com.relateddigital.relateddigital_android.util

import android.util.Log

object RetryCounterManager {
    private const val LOG_TAG = "RetryCounterManager"
    private var counter1 = -1
    private var counter2 = -1
    private var counter3 = -1
    private var counter4 = -1
    private var counter5 = -1
    val counterId: Int
        get() {
            val result: Int
            if (counter1 == -1) {
                counter1 = 0
                result = 1
            } else {
                if (counter2 == -1) {
                    counter2 = 0
                    result = 2
                } else {
                    if (counter3 == -1) {
                        counter3 = 0
                        result = 3
                    } else {
                        if (counter4 == -1) {
                            counter4 = 0
                            result = 4
                        } else {
                            if (counter5 == -1) {
                                counter5 = 0
                                result = 5
                            } else {
                                result = -1
                                Log.i(LOG_TAG, "No counter could be found for re-try!")
                            }
                        }
                    }
                }
            }
            return result
        }

    fun increaseCounter(id: Int) {
        when (id) {
            1 -> {
                counter1++
            }
            2 -> {
                counter2++
            }
            3 -> {
                counter3++
            }
            4 -> {
                counter4++
            }
            5 -> {
                counter5++
            }
            else -> {
                Log.i(LOG_TAG, "There is no counter whose id matches!")
            }
        }
    }

    fun getCounterValue(id: Int): Int {
        val result: Int
        result = when (id) {
            1 -> {
                counter1
            }
            2 -> {
                counter2
            }
            3 -> {
                counter3
            }
            4 -> {
                counter4
            }
            5 -> {
                counter5
            }
            else -> {
                Log.i(LOG_TAG, "There is no counter whose id matches!")
                -1
            }
        }
        return result
    }

    fun clearCounter(id: Int) {
        when (id) {
            1 -> {
                counter1 = -1
            }
            2 -> {
                counter2 = -1
            }
            3 -> {
                counter3 = -1
            }
            4 -> {
                counter4 = -1
            }
            5 -> {
                counter5 = -1
            }
            else -> {
                Log.i(LOG_TAG, "There is no counter whose id matches!")
            }
        }
    }
}