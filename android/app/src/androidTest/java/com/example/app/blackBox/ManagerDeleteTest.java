package com.example.app.blackBox;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.app.Credentials;
import com.example.app.R;
import com.example.app.account_UI.ManagerProfile;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class ManagerDeleteTest {




@Rule
public ActivityScenarioRule<ManagerProfile> activityRule =
        new ActivityScenarioRule<>(ManagerProfile.class);

    @Before
    public void setUp() throws InterruptedException {
        //adding shared preferences to imitate info present in normal use case
        Intents.init();
        Context targetContext = getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", Credentials.email);
        editor.apply();


    }


    @Test
    public void deleteSuccess() throws InterruptedException {


        Thread.sleep(10000);// wait 5 seconds for Firebase to bring back account data
        onView(ViewMatchers.withId(R.id.button15))//click delete button
                .perform(click());
        Thread.sleep(5000);// wait 5 seconds for Firebase to bring back account data
        onView(withText("Yes")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);
//        //check if you get success message from alert dialog
        onView(withText("Successful in deleting your account")).inRoot(isDialog()).withFailureHandler(new FailureHandler() {
            @Override
            public void handle(Throwable error, Matcher<View> viewMatcher){
                fail("Could not find alert");
            }
        }).check(matches(isDisplayed()));


    }
    @After
    public void teardown(){
        Intents.release();
    }
}
