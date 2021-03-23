package com.example.app;

import android.view.View;

import com.example.app.pre_login_UI.LogInPage;


import org.hamcrest.Matcher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.FailureHandler;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.typeText;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.click;
import static org.junit.Assert.fail;



@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    @Rule
    public ActivityScenarioRule<LogInPage> activityRule =
            new ActivityScenarioRule<>(LogInPage.class);

    @Test
    public void loginSuccess() throws InterruptedException {

        onView(withId(R.id.editTextEmailLogin))// add email to textview
                .perform(typeText("sancho@usc.edu"), closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordLogin))//add password to textview
                .perform(typeText("pass"), closeSoftKeyboard());

        onView(withId(R.id.btnLogin2))//click login button
                .perform(click());

        Thread.sleep(5000);// wait 5 seconds for Firebase to bring back account data

        //check if you get success message from alert dialog
        onView(withText("Succeeded in Logging In")).inRoot(isDialog()).withFailureHandler(new FailureHandler() {
            @Override
            public void handle(Throwable error, Matcher<View> viewMatcher){
                fail("Could not find alert");
            }
        }).check(matches(isDisplayed()));



    }
}
