package com.example.app.blackBox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.app.Credentials;
import com.example.app.R;
import com.example.app.account_UI.StudentProfile;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.fail;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ManualCheckOutTest {

    @Rule
    public ActivityTestRule<StudentProfile> mActivityRule =
            new ActivityTestRule<StudentProfile>(StudentProfile.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, StudentProfile.class);
                    result.putExtra("email", Credentials.email);
                    result.putExtra("uscID", Credentials.id);
                    return result;
                }
            };

    @Before
    public void setUp() {
//        Intents.init();
        Context targetContext = getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email",Credentials.email);
        editor.putLong( "uscid",Long.parseLong(Credentials.id));
        editor.apply();

    }

    @Test
    public void manualCheckOut() throws InterruptedException {

        onView(ViewMatchers.withId(R.id.buttonFragment2))
                .perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.checkOutBtn))
                .perform(click());
        Thread.sleep(5000);



        //check if you get alert dialog with success message from checking out
        onView(withText("Check Out Success")).inRoot(isDialog()).withFailureHandler(new FailureHandler() {
            @Override
            public void handle(Throwable error, Matcher<View> viewMatcher){
                fail("Could not find alert");
            }
        }).check(matches(isDisplayed()));

    }

    @After
    public void teardown(){

    }

}
