package com.example.app.blackBox;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.app.R;
import com.example.app.account_UI.StudentProfile;

import org.hamcrest.CustomMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasType;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static java.util.EnumSet.allOf;

import static java.util.regex.Pattern.matches;
import static org.hamcrest.EasyMock2Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


public class UploadNewStudentProfilePic {
    @Rule
    public IntentsTestRule<StudentProfile> mActivityRule =
            new IntentsTestRule<StudentProfile>(StudentProfile.class) {

            };
    @Before
    public void setUp() {
        Context targetContext = getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email","sancho@usc.edu");
        editor.putLong( "uscid",Long.parseLong("8869324031"));
        editor.apply();
    }


    @Test
    public void UploadPicSuccess() throws InterruptedException {


        onView(withId(R.id.buttonFragment1))
                .perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.SelectImg))
                .perform(click());
        Thread.sleep(5000);


//        //check if you get success message from alert dialog
//        onView(withText("Successful in deleting your account")).inRoot(isDialog()).withFailureHandler(new FailureHandler() {
//            @Override
//            public void handle(Throwable error, Matcher<View> viewMatcher){
//                fail("Could not find alert");
//            }
//        }).check(matches(isDisplayed()));


    }
}
