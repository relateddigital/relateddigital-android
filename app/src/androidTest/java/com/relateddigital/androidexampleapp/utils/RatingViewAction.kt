package com.relateddigital.androidexampleapp.utils

import android.view.View
import android.widget.RatingBar
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher


class RatingViewAction : ViewAction {

    var rating = 3f

    override fun getConstraints(): Matcher<View> {
        return ViewMatchers.isAssignableFrom(
            RatingBar::class.java
        )
    }

    override fun getDescription(): String {
        return "Rating View Action"
    }

    override fun perform(uiController: UiController?, view: View?) {
        val ratingBar = view as RatingBar
        ratingBar.rating = rating
    }
}