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
import com.example.app.account_UI.StudentHistory;
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
public class StudentEnterNameTest extends TestCase {

    Intent intent;
    SharedPreferences.Editor preferencesEditor;

    public static String email;
    public static String password;

    @Rule
    public ActivityTestRule<StudentEnterName> activityRule =
            new ActivityTestRule<StudentEnterName>(StudentEnterName.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, StudentEnterName.class);
                    result.putExtra("email", StudentEnterNameTest.email);
                    result.putExtra("pass", StudentEnterNameTest.password);
                    return result;
                }
            };

    private String blankName = "";
    private String actualName = "name";

    @Before
    public void setUp() {
        Intents.init();
        Context targetContext = getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", StudentEnterNameTest.email);
        editor.putString("pass", StudentEnterNameTest.password);
        editor.apply();

    }

    @Test
    public void testEmptyBothNames() throws InterruptedException {
        onView(withId(R.id.nameButton)).perform(click());
        //Check for toast
        onView(withText("First or last name is blank")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Intents.release();
    }
    @Test
    public void testEmptyFirstName() throws InterruptedException {
        onView(withId(R.id.studentFirstName)).perform(typeText(blankName));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.studentLastName)).perform(typeText(actualName));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.nameButton)).perform(click());
        //Check for toast
        onView(withText("First or last name is blank")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Intents.release();
    }
    @Test
    public void testEmptyLastName() throws InterruptedException {
        onView(withId(R.id.studentFirstName)).perform(typeText(blankName));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.studentLastName)).perform(typeText(actualName));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.nameButton)).perform(click());
        //Check for toast
        onView(withText("First or last name is blank")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Intents.release();
    }
    @Test
    public void testValidInput() throws InterruptedException {
        onView(withId(R.id.studentFirstName)).perform(typeText(actualName));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.studentLastName)).perform(typeText(actualName));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.nameButton)).perform(click());
        //Check for new page
        intended(hasComponent(StudentEnterID.class.getName()));
        Intents.release();
    }
}