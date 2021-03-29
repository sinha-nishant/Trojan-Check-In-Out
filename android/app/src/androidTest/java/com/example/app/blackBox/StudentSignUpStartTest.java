package com.example.app.blackBox;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.app.R;
import com.example.app.pre_login_UI.StudentEnterName;
import com.example.app.pre_login_UI.StudentSignUpStart;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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
        onView(withText("Email and Password are blank")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
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
        onView(withText("Password is blank")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
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