package com.relateddigital.relateddigital_android

import com.relateddigital.relateddigital_android.model.UtilResultModel
import com.relateddigital.relateddigital_android.util.AppUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.text.SimpleDateFormat
import java.util.*

@RunWith(JUnit4::class)
class AppUtilsTest {
    @Test
    fun testGetNumberFromText() {
        val result: Boolean
        val input1 = ""
        val input2: String? = null
        val input3 = "<COUNT>150</COUNT> people are viewing this product."
        val input4 = "People : <COUNT>150</COUNT> are viewing this product."
        val input5 = "The number of people who are viewing this product is <COUNT>150</COUNT>"
        val input6 = "<COUNT>150</COUNT> people have viewed this product in 2021"
        val input7 =
            "<COUNT>150</COUNT> people (<COUNT>150</COUNT>) have viewed this product in the last 24 hours."

        val res1: Boolean = AppUtils.getNumberFromText(input1) == null
        val res2: Boolean = AppUtils.getNumberFromText(input2) == null
        val res3: Boolean
        val res4: Boolean
        val res5: Boolean
        val res6: Boolean
        val res7: Boolean

        val model3: UtilResultModel? = AppUtils.getNumberFromText(input3)
        res3 = model3!!.isTag && model3.numbers.size == 1 && model3.numbers[0] == 150 &&
                model3.message =="150 people are viewing this product."

        val model4: UtilResultModel? = AppUtils.getNumberFromText(input4)
        res4 = model4!!.isTag && model4.numbers.size == 1 && model4.numbers[0] == 150 &&
                model4.message == "People : 150 are viewing this product."

        val model5: UtilResultModel? = AppUtils.getNumberFromText(input5)
        res5 = model5!!.isTag && model5.numbers.size == 1 && model5.numbers[0] == 150 &&
                model5.message == "The number of people who are viewing this product is 150"

        val model6: UtilResultModel? = AppUtils.getNumberFromText(input6)
        res6 = model6!!.isTag && model6.numbers.size == 1 && model6.numbers[0] == 150 &&
                model6.message == "150 people have viewed this product in 2021"

        val model7: UtilResultModel? = AppUtils.getNumberFromText(input7)
        res7 = model7!!.isTag && model7.numbers.size == 2 && model7.numbers[0] == 150 &&
                model7.numbers[1] == 150 &&
                model7.message == "150 people (150) have viewed this product in the last 24 hours."

        result = res1 && res2 && res3 && res4 && res5 && res6 && res7

        assert(result)
    }

    @Test
    fun testGetCurrentDateString() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val result1 = dateFormat.format(Date())
        val result2 = AppUtils.getCurrentDateString()
        assert(result1 == result2)
    }

    @Test
    fun testIsDateDifferenceGreaterThan() {
        val result: Boolean
        val date11 = "2021-11-30 11:21:26"
        val date12 = "2021-11-24 11:21:26"
        val thresholdDay1 = 4
        val date21 = "2021-11-30 11:21:26"
        val date22 = "2021-11-27 11:21:26"
        val thresholdDay2 = 4
        val date31 = "2021-11-29 11:21:26"
        val date32 = "2021-11-30 11:21:26"
        val thresholdDay3 = 4
        val date41 = "2021-11-30 11:21:26"
        val date42 = "2021.11.22 11:21:26"
        val thresholdDay4 = 4
        result = AppUtils.isDateDifferenceGreaterThan(date11, date12, thresholdDay1) &&
                !AppUtils.isDateDifferenceGreaterThan(date21, date22, thresholdDay2) &&
                !AppUtils.isDateDifferenceGreaterThan(date31, date32, thresholdDay3) &&
                AppUtils.isDateDifferenceGreaterThan(date41, date42, thresholdDay4)
        assert(result)
    }
}