package com.relateddigital.androidexampleapp

import com.relateddigital.relateddigital_android.util.StringUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class StringUtilsTest {

    @Test
    fun testIsNullOrWhiteSpace() {
        val input1: String? = null
        val input2 = ""
        val input3 = " "
        val input4 = "  "
        val input5 = "test string"
        val input6 = "&"
        val result = StringUtils.isNullOrWhiteSpace(input1) &&
                StringUtils.isNullOrWhiteSpace(input2) &&
                StringUtils.isNullOrWhiteSpace(input3) &&
                StringUtils.isNullOrWhiteSpace(input4) &&
                !StringUtils.isNullOrWhiteSpace(input5) &&
                !StringUtils.isNullOrWhiteSpace(input6)
        assert(result)
    }

    @Test
    fun testSplitRGBA() {
        val input = "rgba(145),rgba(137),rgba(243)"
        val result: Boolean
        val res: Array<String> = StringUtils.splitRGBA(input)
        result = res.size == 3 && res[0] == "145" && res[1] == "137" && res[2] == "243"
        assert(result)
    }

    @Test
    fun testValidateHexColor() {
        val input1 = "#ffffff"
        val input2 = "test"
        val result: Boolean = StringUtils.validateHexColor(input1) && !StringUtils.validateHexColor(input2)
        assert(result)
    }
}