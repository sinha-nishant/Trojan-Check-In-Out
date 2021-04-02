package com.example.app.blackBox;

import android.view.View;

import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.app.Credentials;
import com.example.app.R;
import com.example.app.pre_login_UI.ManagerSignUpStart;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

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

public class CreateManagerTest {
    public static String email;
    @Rule
    public ActivityScenarioRule<ManagerSignUpStart> activityRule =
            new ActivityScenarioRule<>(ManagerSignUpStart.class);

    @Test
    public void ManagerCreateSuccess() throws InterruptedException {

        email= Credentials.email;
        // adding email a nd password into textviews
        onView(ViewMatchers.withId(R.id.managerSignUpEmailAddress))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.managerSignUpPassword))
                .perform(typeText("password"), closeSoftKeyboard());

        onView(withId(R.id.managerEmailPassSubmitButton))
                .perform(click());
        // adding first/lastname info
        onView(withId(R.id.managerFirstName))
                .perform(typeText("Manager"), closeSoftKeyboard());
        onView(withId(R.id.managerLastName))
                .perform(typeText("Create"), closeSoftKeyboard());
        //creating after button click
        onView(withId(R.id.nameButtonM))
                .perform(click());

        Thread.sleep(5000);

        onView(withText("Succeeded in creating your account")).inRoot(isDialog()).withFailureHandler(new FailureHandler() {
            @Override
            public void handle(Throwable error, Matcher<View> viewMatcher){
                fail("Could not find alert");
            }
        }).check(matches(isDisplayed()));


    }

}
