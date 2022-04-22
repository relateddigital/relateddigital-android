package com.relateddigital.androidexampleapp

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.relateddigital.relateddigital_android.inapp.halfscreen.HalfScreenFragment
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This class tests the HalfScreen template
 * If the test fails, check the position first. (must be at bottom)
 */
@RunWith(AndroidJUnit4::class)
class HalfScreenTest {
    @Before
    fun beforeTheTest() {

    }

    @Test
    fun runTheTest() {
        // Start the test application
        launchActivity<MainActivity>()

        // Click on IN-APP NOTIFICATION PAGE button
        onView(withId(R.id.inAppNotificationPage)).perform(click())

        // Click on HALF SCREEN button
        onView(withId(R.id.halfScreenButton)).perform(click())

        // Wait a bit for the request's response
        Thread.sleep(2000)

        // Check the image's position
        onView(withId(R.id.bot_image_view)).check(matches(isDisplayed()))
        onView(withId(R.id.top_image_view)).check(matches(not(isDisplayed())))

        // Check the close button's position
        onView(withId(R.id.bot_close_button)).check(matches(isDisplayed()))
        onView(withId(R.id.top_close_button)).check(matches(not(isDisplayed())))

        // Check if the user can interact with background views
        onView(withId(R.id.scratchToWinButton)).check(matches(isDisplayed()))
        onView(withId(R.id.scratchToWinButton)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.view_to_be_scratched)).check(matches(isDisplayed()))
        onView(withId(R.id.close_button)).perform(click())
        Thread.sleep(1000)
    }

    @After
    fun afterTheTest() {

    }
}