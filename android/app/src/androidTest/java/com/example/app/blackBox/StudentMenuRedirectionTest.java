package com.example.app.blackBox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.app.R;
import com.example.app.account_UI.StudentHistory;
import com.example.app.account_UI.StudentProfile;
import com.example.app.account_UI.StudentProfileMenu;
import com.example.app.pre_login_UI.StartPage;
import com.example.app.pre_login_UI.StudentEnterID;
import com.example.app.pre_login_UI.StudentEnterName;
import com.example.app.pre_login_UI.StudentSignUpStart;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class StudentMenuRedirectionTest extends TestCase {

    public static String uscID;

    @Rule
    public ActivityTestRule<StudentProfile> activityRule =
            new ActivityTestRule<StudentProfile>(StudentProfile.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, StudentProfile.class);
                    result.putExtra("0000011111", StudentMenuRedirectionTest.uscID);
                    return result;
                }};

    @Before
    public void setUp() {
        Intents.init();
        Context targetContext = getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("0000011111", StudentMenuRedirectionTest.uscID);
        editor.apply();
    }
    @Test
    public void testHistoryRedirect() throws InterruptedException {
        onView(withId(R.id.buttonFragment2)).perform(click());
        onView(withId(R.id.studentMenuHistory)).perform(click());
        //Check for new page
        intended(hasComponent(StudentHistory.class.getName()));
        Intents.release();
    }
    @Test
    public void testSwitchingFragmentsRedirect() throws InterruptedException {
        //just a bunch of fragment switching
        onView(withId(R.id.buttonFragment1)).perform(click());
        onView(withId(R.id.buttonFragment1)).perform(click());
        onView(withId(R.id.buttonFragment2)).perform(click());
        onView(withId(R.id.buttonFragment2)).perform(click());
        onView(withId(R.id.buttonFragment1)).perform(click());
        onView(withId(R.id.buttonFragment1)).perform(click());
        onView(withId(R.id.buttonFragment1)).perform(click());
        onView(withId(R.id.buttonFragment1)).perform(click());
        onView(withId(R.id.buttonFragment1)).perform(click());
        onView(withId(R.id.buttonFragment2)).perform(click());
        onView(withId(R.id.buttonFragment2)).perform(click());

        onView(withId(R.id.studentMenuHistory)).perform(click());
        //Check for new page
        intended(hasComponent(StudentHistory.class.getName()));
        Intents.release();
     }

}