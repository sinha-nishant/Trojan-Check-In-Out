package com.example.app.blackBox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.FailureHandler;
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
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.action.ViewActions.typeText;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class StudentEnterIDTest extends TestCase {

    Intent intent;
    SharedPreferences.Editor preferencesEditor;

    public static String email;
    public static String password;
    public static String fname;
    public static String lname;

    @Rule
    public ActivityTestRule<StudentEnterID> activityRule =
            new ActivityTestRule<StudentEnterID>(StudentEnterID.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, StudentEnterID.class);
                    result.putExtra("email", StudentEnterIDTest.email);
                    result.putExtra("pass", StudentEnterIDTest.password);
                    result.putExtra("fname", StudentEnterIDTest.fname);
                    result.putExtra("lname", StudentEnterIDTest.lname);
                    return result;
                }
            };

    private String validID = "1234554321";
    private String longID = "123456123456";
    private String shortID = "123456";
    private String blankID = "";
    private String alphabetID = "identification";
    private String alphabetIDCorrectLength = "123456789t";

    @Before
    public void setUp() {
        Context targetContext = getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", StudentEnterIDTest.email);
        editor.putString("pass", StudentEnterIDTest.password);
        editor.putString("fname", StudentEnterIDTest.fname);
        editor.putString("lname", StudentEnterIDTest.lname);
        editor.apply();

    }
    @Test
    public void testShortID() throws InterruptedException {
        onView(withId(R.id.studentID)).perform(typeText(shortID));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.signup)).perform(click());
        //Check for toast
        onView(withText("Invalid USC ID")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
    @Test
    public void testLongID() throws InterruptedException {
        onView(withId(R.id.studentID)).perform(typeText(longID));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.signup)).perform(click());
        //Check for toast
        onView(withText("Invalid USC ID")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
    @Test
    public void testBlankID() throws InterruptedException {
        onView(withId(R.id.studentID)).perform(typeText(blankID));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.signup)).perform(click());
        //Check for toast
        onView(withText("Invalid USC ID")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
    @Test
    public void testNonNumericID() throws InterruptedException {
        onView(withId(R.id.studentID)).perform(typeText(alphabetID));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.signup)).perform(click());
        //Check for toast
        onView(withText("USC ID must only contain numbers")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
    @Test
    public void testCorrectLengthNonNumericID() throws InterruptedException {
        onView(withId(R.id.studentID)).perform(typeText(alphabetIDCorrectLength));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.signup)).perform(click());
        //Check for toast
        onView(withText("USC ID must only contain numbers")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
}