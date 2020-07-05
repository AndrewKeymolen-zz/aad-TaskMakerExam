package com.google.developer.taskmaker;

import org.junit.Test;

import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.withId;

//New code from Andrew Keymolen Testing Tasks 1
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void ensureFabWork() {
        //We perform a click on the fab
        onView(withId(R.id.fab)).perform(click());
        //if label_date - which is part of activity_add_task - is displayed,
        //it means we are on the right activity
        onView(withId(R.id.label_date)).check(matches(isDisplayed()));
    }

}