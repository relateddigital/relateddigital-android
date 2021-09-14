package com.relateddigital.relateddigital_android.util

import android.graphics.Color
import java.util.*
import kotlin.math.abs

object ColorUtils {
    fun calculateGradientColors(rateCount: Int, colors: IntArray): HashMap<Int, IntArray> {
        val cells = HashMap<Int, IntArray>()
        if (colors.size == 2) {
            val red1 = Color.red(colors[0])
            val red2 = Color.red(colors[1])
            val green1 = Color.green(colors[0])
            val green2 = Color.green(colors[1])
            val blue1 = Color.blue(colors[0])
            val blue2 = Color.blue(colors[1])
            val redInterval = abs(red1 - red2) / (rateCount + 1) * if (red1 < red2) 1 else -1
            val greenInterval = abs(green1 - green2) / (rateCount + 1) * if (green1 < green2) 1 else -1
            val blueInterval = abs(blue1 - blue2) / (rateCount + 1) * if (blue1 < blue2) 1 else -1
            for (i in 1 until rateCount + 1) {
                val firstColor = Color.argb(255,
                        red1 + (i - 1) * redInterval,
                        green1 + (i - 1) * greenInterval,
                        blue1 + (i - 1) * blueInterval)
                val secondColor = Color.argb(255,
                        red1 + i * redInterval,
                        green1 + i * greenInterval,
                        blue1 + i * blueInterval)
                cells[i] = intArrayOf(firstColor, secondColor)
            }
        } else if (colors.size == 3) {
            val red1 = Color.red(colors[0])
            val red2 = Color.red(colors[1])
            val red3 = Color.red(colors[2])
            val green1 = Color.green(colors[0])
            val green2 = Color.green(colors[1])
            val green3 = Color.green(colors[2])
            val blue1 = Color.blue(colors[0])
            val blue2 = Color.blue(colors[1])
            val blue3 = Color.blue(colors[2])
            val redInterval1 = abs(red1 - red2) / (rateCount / 2 + 1) * if (red1 < red2) 1 else -1
            val greenInterval1 = abs(green1 - green2) / (rateCount / 2 + 1) * if (green1 < green2) 1 else -1
            val blueInterval1 = abs(blue1 - blue2) / (rateCount / 2 + 1) * if (blue1 < blue2) 1 else -1
            val redInterval2 = abs(red2 - red3) / (rateCount / 2 + 1) * if (red2 < red3) 1 else -1
            val greenInterval2 = abs(green2 - green3) / (rateCount / 2 + 1) * if (green2 < green3) 1 else -1
            val blueInterval2 = abs(blue2 - blue3) / (rateCount / 2 + 1) * if (blue2 < blue3) 1 else -1
            for (i in 1 until rateCount / 2 + 1) {
                val firstColor = Color.argb(255,
                        red1 + (i - 1) * redInterval1,
                        green1 + (i - 1) * greenInterval1,
                        blue1 + (i - 1) * blueInterval1)
                val secondColor = Color.argb(255,
                        red1 + i * redInterval1,
                        green1 + i * greenInterval1,
                        blue1 + i * blueInterval1)
                cells[i] = intArrayOf(firstColor, secondColor)
            }
            for (i in rateCount / 2 + 1 until rateCount + 1) {
                val firstColor = Color.argb(255,
                        red2 + (i - rateCount / 2 - 1) * redInterval2,
                        green2 + (i - rateCount / 2 - 1) * greenInterval2,
                        blue2 + (i - rateCount / 2 - 1) * blueInterval2)
                val secondColor = Color.argb(255,
                        red2 + (i - rateCount / 2) * redInterval2,
                        green2 + (i - rateCount / 2) * greenInterval2,
                        blue2 + (i - rateCount / 2) * blueInterval2)
                cells[i] = intArrayOf(firstColor, secondColor)
            }
        } else {
            for (i in 1 until rateCount + 1) {
                cells[i] = intArrayOf(colors[0])
            }
        }
        return cells
    }
}