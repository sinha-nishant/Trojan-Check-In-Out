package com.example.app.blackBox;

import android.view.View;

import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.app.Credentials;
import com.example.app.R;
import com.example.app.pre_login_UI.StudentSignUpStart;

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
public class CreateStudentTest {
    public static String email;
    public static String uscID;

    @Rule
    public ActivityScenarioRule<StudentSignUpStart> activityRule =
            new ActivityScenarioRule<>(StudentSignUpStart.class);

    @Test
    public void CreateStudentSuccess() throws InterruptedException {

        email= Credentials.email;
        onView(ViewMatchers.withId(R.id.studentSignUpEmailAddress))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.studentSignUpPassword))
                .perform(typeText("pass"), closeSoftKeyboard());

        onView(withId(R.id.studentEmailPassSubmitButton))
                .perform(click());
        // adding name info
        onView(withId(R.id.studentFirstName))
                .perform(typeText("My"), closeSoftKeyboard());
        onView(withId(R.id.studentLastName))
                .perform(typeText("Test"), closeSoftKeyboard());

        onView(withId(R.id.nameButton))
                .perform(click());

        uscID=Credentials.id;
        onView(withId(R.id.studentID))
                .perform(typeText(uscID), closeSoftKeyboard());

        onView(withId(R.id.signup))
                .perform(click());

        onView(withId(R.id.submit))
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
