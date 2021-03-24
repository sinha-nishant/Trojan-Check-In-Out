package com.example.app.blackBox;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import org.hamcrest.Matcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.app.R;
import com.example.app.account_UI.ManagerProfile;
import com.example.app.account_UI.StudentProfile;
import com.example.app.account_UI.StudentProfileMenu;
import com.example.app.pre_login_UI.LogInPage;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.typeText;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class ManagerDeleteTest {
    Intent intent;
    SharedPreferences.Editor preferencesEditor;


    @Rule
    public ActivityTestRule<ManagerProfile> mActivityRule =
            new ActivityTestRule<ManagerProfile>(ManagerProfile.class) {
                    @Override
                protected Intent getActivityIntent() {
                    Context targetContext = getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, ManagerProfile.class);
                    result.putExtra("email", "ManagerTest6@usc.edu");
                    result.putExtra("uscID", "0");
                    return result;
                }
            };

    @Before
    public void setUp() {
        Context targetContext = getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("sharedPrefs",targetContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email","ManagerTest6@usc.edu");
        editor.putLong( "uscid",0L);
        editor.apply();
    }


    @Test
    public void deleteSuccess() throws InterruptedException {

        Thread.sleep(5000);// wait 5 seconds for Firebase to bring back account data
        onView(ViewMatchers.withId(R.id.button15))//click delete button
                .perform(click());
        Thread.sleep(5000);// wait 5 seconds for Firebase to bring back account data


        //check if you get success message from alert dialog
        onView(withText("Successful in deleting your account")).inRoot(isDialog()).withFailureHandler(new FailureHandler() {
            @Override
            public void handle(Throwable error, Matcher<View> viewMatcher){
                fail("Could not find alert");
            }
        }).check(matches(isDisplayed()));







    }
}
