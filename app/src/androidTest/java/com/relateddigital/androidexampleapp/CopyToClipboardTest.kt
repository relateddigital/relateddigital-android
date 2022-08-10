package com.relateddigital.androidexampleapp

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.relateddigital.androidexampleapp.utils.TextViewAction
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * This class tests the CopyToClipboard button action
 */
@RunWith(AndroidJUnit4::class)
class CopyToClipboardTest {

    @Before
    fun beforeTheTest() {

    }

    @Test
    fun runTheTest() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Start the test application
        launchActivity<MainActivity>()

        // Click on IN-APP NOTIFICATION PAGE button
        Espresso.onView(ViewMatchers.withId(R.id.inAppNotificationPage))
            .perform(ViewActions.click())

        // Click on IMAGE TEXT BUTTON button
        Espresso.onView(ViewMatchers.withId(R.id.imageTextButtonButton)).perform(ViewActions.click())

        // Wait a bit for the request's response
        Thread.sleep(2000)

        // Get the coupon code from textView
        val textViewAction = TextViewAction()

        Espresso.onView(withId(R.id.tv_coupon_code))
            .perform(textViewAction)

        Thread.sleep(100)

        val couponCode = textViewAction.textStr

        // Click on the copy-to-clipboard button
        Espresso.onView(withId(R.id.ll_coupon_container)).perform(ViewActions.click())
        Thread.sleep(300)

        // Check if the text is copied to the clipboard correctly
        val clipboard: ClipboardManager = appContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        assert(clipboard.primaryClip!!.getItemAt(0).text.toString() == couponCode)

        // Close the action
        Espresso.onView(withId(R.id.ib_close))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.ib_close)).perform(ViewActions.click())
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.imageTextButtonButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @After
    fun afterTheTest() {

    }
}