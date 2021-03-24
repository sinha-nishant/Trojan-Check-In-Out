package com.example.app.blackBox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.example.app.R;
import com.example.app.account_UI.StudentProfile;
import com.example.app.blackBox.CreateStudentTest;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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

public class StudentDeleteTest {

        Intent intent;
        SharedPreferences.Editor preferencesEditor;

        @Rule
        public ActivityTestRule<StudentProfile> mActivityRule =
                new ActivityTestRule<StudentProfile>(StudentProfile.class) {
                    @Override
                    protected Intent getActivityIntent() {
                        Context targetContext = getInstrumentation().getTargetContext();
                        Intent result = new Intent(targetContext, StudentProfile.class);
                        result.putExtra("email", CreateStudentTest.email);
                        result.putExtra("uscID", CreateStudentTest.uscID);
                        return result;
                    }
                };

        @Before
        public void setUp() {
            Context targetContext = getInstrumentation().getTargetContext();
            SharedPreferences sharedPreferences = targetContext.getSharedPreferences("sharedPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email",CreateStudentTest.email);
            editor.putLong( "uscid",Long.parseLong(CreateStudentTest.uscID));
            editor.apply();

        }

        @Test
        public void deleteSuccess() throws InterruptedException {

            onView(ViewMatchers.withId(R.id.buttonFragment2))
                    .perform(click());
            Thread.sleep(5000);
            onView(withId(R.id.studentMenuDelete))
                    .perform(click());
            Thread.sleep(5000);



            //check if you get success message from alert dialog
            onView(withText("Successful in deleting your account")).inRoot(isDialog()).withFailureHandler(new FailureHandler() {
                @Override
                public void handle(Throwable error, Matcher<View> viewMatcher){
                    fail("Could not find alert");
                }
            }).check(matches(isDisplayed()));







        }

    }
