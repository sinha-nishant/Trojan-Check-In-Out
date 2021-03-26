package com.example.app.blackBox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.app.R;
import com.example.app.account_UI.StudentProfile;
import com.example.app.pre_login_UI.StudentEnterID;
import com.example.app.pre_login_UI.StudentEnterName;
import com.example.app.pre_login_UI.StudentSignUpStart;

import junit.framework.TestCase;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.action.ViewActions.typeText;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class StudentSignUpStartTest extends TestCase {

    @Rule
    public ActivityScenarioRule<StudentSignUpStart> activityRule =
            new ActivityScenarioRule<>(StudentSignUpStart.class);

    private String blankEmail = "";
    private String blankPass = "";
    private String gmail = "johnpain@gmail.com";
    private String correct = "johnpain@usc.edu";
    private String password = "pass";

    @Test
    public void testEmptyEmail() throws InterruptedException
    {
        onView(withId(R.id.studentSignUpEmailAddress)).perform(typeText(blankEmail));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.studentSignUpPassword)).perform(typeText(blankPass));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.studentEmailPassSubmitButton)).perform(click());
        //Check for toast
        onView(withText("Email or Password is blank")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
    @Test
    public void testEmptyPass() throws InterruptedException
    {
        onView(withId(R.id.studentSignUpEmailAddress)).perform(typeText(correct));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.studentSignUpPassword)).perform(typeText(blankPass));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.studentEmailPassSubmitButton)).perform(click());
        //Check for toast
        onView(withText("Email or Password is blank")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
    @Test
    public void testIncorrectEmail() throws InterruptedException
    {
        onView(withId(R.id.studentSignUpEmailAddress)).perform(typeText(gmail));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.studentSignUpPassword)).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.studentEmailPassSubmitButton)).perform(click());
        //Check for toast
        onView(withText("Invalid Email")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
    @Test
    public void testCorrectValidation() throws InterruptedException
    {
        Intents.init();
        onView(withId(R.id.studentSignUpEmailAddress)).perform(typeText(correct));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.studentSignUpPassword)).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.studentEmailPassSubmitButton)).perform(click());
        //Check for new page
        intended(hasComponent(StudentEnterName.class.getName()));
    }
}