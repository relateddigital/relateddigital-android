package com.relateddigital.androidexampleapp.utils

import android.view.View
import android.widget.TextView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.Matcher


class TextViewAction : ViewAction {
    var textStr = ""

    override fun getConstraints(): Matcher<View> {
        return isAssignableFrom(TextView::class.java)
    }

    override fun getDescription(): String {
        return "Get text from a textview"
    }

    override fun perform(uiController: UiController?, view: View?) {
        val tv = view as TextView
        textStr = tv.text.toString()
    }
}