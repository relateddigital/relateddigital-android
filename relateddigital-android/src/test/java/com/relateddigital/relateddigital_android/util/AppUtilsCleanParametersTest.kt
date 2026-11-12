package com.relateddigital.relateddigital_android.util

import com.relateddigital.relateddigital_android.constants.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUtilsCleanParametersTest {

    @Test
    fun removesReservedKeysWithoutConcurrentModification() {
        val map = linkedMapOf(
            Constants.EXVISITOR_ID_REQUEST_KEY to "visitor",
            "OM.inc" to "x",
            Constants.COOKIE_ID_REQUEST_KEY to "cookie",
            Constants.ORGANIZATION_ID_REQUEST_KEY to "org",
            "custom" to "value",
            Constants.SITE_ID_REQUEST_KEY to "site",
            Constants.ZONE_ID_KEY to "zone",
            Constants.BODY_KEY to "body",
            Constants.TOKEN_ID_REQUEST_KEY to "token",
            Constants.APP_ID_REQUEST_KEY to "app",
            Constants.API_VERSION_REQUEST_KEY to "api",
            Constants.FILTER_KEY to "filter"
        )
        val parameters = HashMap(map)

        AppUtils.cleanParameters(parameters)

        assertEquals(mapOf("OM.inc" to "x", "custom" to "value"), parameters)
    }

    @Test
    fun removesBlankKeys() {
        val parameters = hashMapOf(
            "" to "empty",
            "   " to "blank",
            "first" to "1",
            "second" to "2"
        )

        AppUtils.cleanParameters(parameters)

        assertEquals(mapOf("first" to "1", "second" to "2"), parameters)
    }

    @Test
    fun keepsUtmKeys() {
        val parameters = hashMapOf(
            Constants.UTM_CAMPAIGN to "campaign",
            Constants.UTM_CONTENT to "content",
            Constants.UTM_MEDIUM to "medium",
            Constants.UTM_SOURCE to "source",
            Constants.UTM_TERM to "term",
            Constants.EXVISITOR_ID_REQUEST_KEY to "visitor"
        )

        AppUtils.cleanParameters(parameters)

        assertEquals(5, parameters.size)
        assertTrue(parameters.keys.none { it == Constants.EXVISITOR_ID_REQUEST_KEY })
    }

    @Test
    fun handlesEmptyMap() {
        val parameters = HashMap<String, String>()

        AppUtils.cleanParameters(parameters)

        assertTrue(parameters.isEmpty())
    }
}
