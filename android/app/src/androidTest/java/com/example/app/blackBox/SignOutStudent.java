package com.example.app.blackBox;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.app.R;
import com.example.app.account_UI.StudentProfile;
import com.example.app.pre_login_UI.StartPage;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;
import org.junit.Before;
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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignOutStudent {
    @Rule
    public IntentsTestRule<StudentProfile> mActivityRule =
            new IntentsTestRule<StudentProfile>(StudentProfile.class) {
            };
    @Before
    public void setUp() {
        // account info set up to imitate real use  case
        Context targetContext = getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email",CreateStudentTest.email);
        editor.putLong( "uscid",Long.parseLong(CreateStudentTest.uscID));
        editor.apply();

    }

    @Test
    public void SignOutStudentSuccess() throws InterruptedException {
        onView(ViewMatchers.withId(R.id.buttonFragment2))
                .perform(click());
        onView(withId(R.id.studentMenuLogOut))
                .perform(click());
        try{

            intended(hasComponent(StartPage.class.getName()));

        }catch (AssertionFailedError e){
            fail("Not right page");
        }

    }

}