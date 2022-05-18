package com.relateddigital.androidexampleapp

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This class tests the NPS with Feedback Form
 * If this test fails, check if the threshold value to show the feedback form is reasonable
 */
@RunWith(AndroidJUnit4::class)
class NpsFeedbackTest {

    @Before
    fun beforeTheTest() {

    }

    @Test
    fun runTheTest() {
        // Start the test application
        launchActivity<MainActivity>()

        // Click on IN-APP NOTIFICATION PAGE button
        Espresso.onView(ViewMatchers.withId(R.id.inAppNotificationPage))
            .perform(ViewActions.click())

        // Click on NPS3 button
        Espresso.onView(ViewMatchers.withId(R.id.nps3Button)).perform(ViewActions.click())

        // Wait a bit for the request's response
        Thread.sleep(2000)

        // Check if first NPS popup is shown
        Espresso.onView(ViewMatchers.withId(R.id.ratingBar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        val ratingAction = RatingViewAction()
        ratingAction.rating = 0.5f

        // Give rating : 0.5
        Espresso.onView(ViewMatchers.withId(R.id.ratingBar)).perform(ratingAction)
        Espresso.onView(ViewMatchers.withId(R.id.btn_template)).perform(ViewActions.click())

        // Check if the feedback form is shown
        Espresso.onView(ViewMatchers.withId(R.id.commentBox))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Click on the button and close
        Espresso.onView(ViewMatchers.withId(R.id.button)).perform(ViewActions.click())

        // Click on NPS3 button
        Espresso.onView(ViewMatchers.withId(R.id.nps3Button)).perform(ViewActions.click())

        // Wait a bit for the request's response
        Thread.sleep(2000)

        // Check if first NPS popup is shown
        Espresso.onView(ViewMatchers.withId(R.id.ratingBar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        ratingAction.rating = 5f

        // Give rating : 5
        Espresso.onView(ViewMatchers.withId(R.id.ratingBar)).perform(ratingAction)
        Espresso.onView(ViewMatchers.withId(R.id.btn_template)).perform(ViewActions.click())

        // Check if the feedback form is not shown
        Espresso.onView(ViewMatchers.withId(R.id.nps3Button))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @After
    fun afterTheTest() {

    }

}