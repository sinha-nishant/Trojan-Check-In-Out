package com.example.app.blackBox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.app.R;
import com.example.app.pre_login_UI.ManagerName;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class ManagerNameTest extends TestCase {

    Intent intent;
    SharedPreferences.Editor preferencesEditor;

    public static String email;
    public static String password;

    @Rule
    public ActivityTestRule<ManagerName> activityRule =
            new ActivityTestRule<ManagerName>(ManagerName.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, ManagerName.class);
                    result.putExtra("email", ManagerNameTest.email);
                    result.putExtra("pass", ManagerNameTest.password);
                    return result;
                }
            };

    private String blankName = "";
    private String actualName = "name";

    @Before
    public void setUp() {
        Context targetContext = getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", ManagerNameTest.email);
        editor.putString("pass", ManagerNameTest.password);
        editor.apply();

    }

    @Test
    public void testEmptyBothNames() throws InterruptedException {
        onView(withId(R.id.nameButtonM)).perform(click());
        //Check for toast
        onView(withText("First and last name are blank")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyFirstName() throws InterruptedException {
        onView(withId(R.id.managerFirstName)).perform(typeText(blankName));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.managerLastName)).perform(typeText(actualName));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.nameButtonM)).perform(click());
        //Check for toast
        onView(withText("First name is blank")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyLastName() throws InterruptedException {
        onView(withId(R.id.managerFirstName)).perform(typeText(actualName));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.managerLastName)).perform(typeText(blankName));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.nameButtonM)).perform(click());
        //Check for toast
        onView(withText("Last name is blank")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
}