package com.relateddigital.androidexampleapp

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.relateddigital.relateddigital_android.inapp.story.StorySkinBasedAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This class tests the StoryManager action
 * If this test fails, check if the template with ID of 305 exists and is active!!
 */
@RunWith(AndroidJUnit4::class)
class StoryTest {

    @Before
    fun beforeTheTest() {

    }

    @Test
    fun runTheTest() {
        // Start the test application
        launchActivity<MainActivity>()

        // Click on STORY PAGE button
        onView(withId(R.id.storyPage))
            .perform(click())

        // Type the ID : 305 and send the request
        onView(withId(R.id.et_story_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.et_story_id))
            .perform(ViewActions.typeText("305"))
            .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.btn_show_story)).perform(click())

        // Wait a bit for the request's response
        Thread.sleep(2000)

        // Click on the first story
        onView(withId(R.id.vrv_story))
            .perform(RecyclerViewActions.actionOnItemAtPosition<StorySkinBasedAdapter.StoryHolder>(0, click()))

        // Wait a bit for loading the story
        Thread.sleep(1000)

        // Check if each element is on the screen
        onView(withId(R.id.ivClose)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.civ_cover)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.tv_cover)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.stories)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.btn_story)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Close the story
        onView(withId(R.id.ivClose)).perform(click())

    }

    @After
    fun afterTheTest() {

    }

}