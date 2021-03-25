package com.example.app.account_UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;

import com.example.app.R;
import com.example.app.blackBox.CreateStudentTest;
import com.example.app.pre_login_UI.LogInPage;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class QRScanTest {
    @Rule
    public ActivityScenarioRule<QRScan> activityRule = new ActivityScenarioRule<>(QRScan.class);
    Intent intent;
    SharedPreferences.Editor preferencesEditor;

    @Rule
    public ActivityTestRule<QRScan> mActivityRule =
            new ActivityTestRule<QRScan>(QRScan.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, QRScan.class);
                    result.putExtra("email", CreateStudentTest.email);
                    result.putExtra("uscID", CreateStudentTest.uscID);
                    return result;
                }
            };

    @Before
    public void setUp() throws Exception {
        Context targetContext = getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email",CreateStudentTest.email);
        editor.putLong( "uscid",Long.parseLong(CreateStudentTest.uscID));
        editor.apply();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void onCreate() throws InterruptedException{
        Thread.sleep(5000);

    }
}