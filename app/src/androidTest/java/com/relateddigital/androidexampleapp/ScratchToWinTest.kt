package com.relateddigital.androidexampleapp

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This class tests the ScratchToWin action
 * If this test fails, check if the template has an email form first!!
 */
@RunWith(AndroidJUnit4::class)
class ScratchToWinTest {

    companion object {
        private const val FAIL_EMAIL_ADDRESS = "test159"
        private const val SUCCESS_EMAIL_ADDRESS = "test159@g.com"
    }

    @Before
    fun beforeTheTest() {

    }

    @Test
    fun runTheTest() {
        // Start the test application
        launchActivity<MainActivity>()

        // Click on IN-APP NOTIFICATION PAGE button
        onView(withId(R.id.inAppNotificationPage)).perform(click())

        // Click on SCRATCH TO WIN button
        onView(withId(R.id.scratchToWinButton)).perform(click())

        // Wait a bit for the request's response
        Thread.sleep(2000)

        // Test invalid email address
        onView(withId(R.id.emailEdit)).check(matches(isDisplayed()))
        onView(withId(R.id.emailEdit)).perform(typeText(FAIL_EMAIL_ADDRESS))
            .perform(closeSoftKeyboard())
        onView(withId(R.id.save_mail)).perform(click())
        onView(withId(R.id.invalid_email_message)).check(matches(isDisplayed()))

        // Test valid email address
        onView(withId(R.id.emailEdit)).perform(clearText())
        onView(withId(R.id.emailEdit)).perform(typeText(SUCCESS_EMAIL_ADDRESS))
            .perform(closeSoftKeyboard())
        onView(withId(R.id.save_mail)).perform(click())
        onView(withId(R.id.invalid_email_message)).check(matches(not(isDisplayed())))

        // Test email consent checkboxes
        onView(withId(R.id.email_permit_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.consent_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.result_text)).check(matches(isDisplayed()))
        onView(withId(R.id.email_permit_checkbox)).perform(click())
        onView(withId(R.id.save_mail)).perform(click())
        onView(withId(R.id.result_text)).check(matches(isDisplayed()))
        onView(withId(R.id.consent_checkbox)).perform(click())
        onView(withId(R.id.save_mail)).perform(click())
        onView(withId(R.id.result_text)).check(matches(not(isDisplayed())))

        // Test if email part is gone
        onView(withId(R.id.emailEdit)).check(matches(not(isDisplayed())))
        onView(withId(R.id.save_mail)).check(matches(not(isDisplayed())))

        // Test if copy-to-clipboard button is not available before scratching
        onView(withId(R.id.copy_to_clipboard)).check(matches(not(isDisplayed())))

        // Test close button
        onView(withId(R.id.close_button)).check(matches(isDisplayed()))
        onView(withId(R.id.close_button)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.scratchToWinButton)).check(matches(isDisplayed()))
    }

    @After
    fun afterTheTest() {

    }
}