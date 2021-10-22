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
}