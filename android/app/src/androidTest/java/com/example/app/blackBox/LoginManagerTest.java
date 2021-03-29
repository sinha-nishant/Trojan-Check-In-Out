package com.example.app.blackBox;

import android.view.View;

import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.app.Credentials;
import com.example.app.R;
import com.example.app.pre_login_UI.LogInPage;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginManagerTest {

    @Rule
    public ActivityScenarioRule<LogInPage> activityRule =
            new ActivityScenarioRule<>(LogInPage.class);

    @Test
    public void loginManagerSuccess() throws InterruptedException {

        onView(ViewMatchers.withId(R.id.editTextEmailLogin))// add email to textview
                .perform(typeText(Credentials.email), closeSoftKeyboard());
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
