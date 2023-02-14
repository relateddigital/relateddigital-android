package com.relateddigital.relateddigital_android_core.util

import android.net.Uri
import android.util.Log
import java.util.regex.Matcher
import java.util.regex.Pattern

object StringUtils {
    private const val HEX_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"
    private var mPattern: Pattern? = null
    private var mMatcher: Matcher? = null
    fun isNullOrWhiteSpace(value: String?): Boolean {
        return value == null || value.trim { it <= ' ' }.isEmpty()
    }

    fun getURIfromUrlString(uriString: String?): Uri? {
        var uri: Uri? = null
        try {
            uri = Uri.parse(uriString)
        } catch (e: IllegalArgumentException) {
            Log.i("Visilabs", "Can't parse notification URI, will not take any action", e)
        }
        return uri
    }

    fun splitRGBA(rgba: String): Array<String> {
        val parenthesis = rgba.replace("rgba", "")
        val test = parenthesis.replace("[\\[\\](){}]".toRegex(), "")
        return test.split(",").toTypedArray()
    }

    fun validateHexColor(hexColorCode: String?): Boolean {
        mPattern = Pattern.compile(HEX_PATTERN)
        mMatcher = mPattern!!.matcher(hexColorCode)
        return mMatcher!!.matches()
    }
}