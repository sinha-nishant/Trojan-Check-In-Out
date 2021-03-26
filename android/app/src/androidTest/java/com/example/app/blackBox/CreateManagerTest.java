package com.example.app.blackBox;

import android.view.View;

import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;


import com.example.app.R;
import com.example.app.pre_login_UI.ManagerSignUpStart;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.fail;

public class CreateManagerTest {
    public static String email;
    @Rule
    public ActivityScenarioRule<ManagerSignUpStart> activityRule =
            new ActivityScenarioRule<>(ManagerSignUpStart.class);

    @Test
    public void ManagerCreateSuccess() throws InterruptedException {
        byte[] array = new byte[20];
        new Random().nextBytes(array);
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(20);

        for (int i = 0; i < 20; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        String generatedString = sb.toString();
        generatedString+="@usc.edu";
        email=generatedString;//created random  email
        // adding email a nd password into textviews
        onView(ViewMatchers.withId(R.id.managerSignUpEmailAddress))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.managerSignUpPassword))
                .perform(typeText("pass"), closeSoftKeyboard());

        onView(withId(R.id.managerEmailPassSubmitButton))
                .perform(click());
        // adding first/lastname info
        onView(withId(R.id.managerFirstName))
                .perform(typeText("Manager"), closeSoftKeyboard());
        onView(withId(R.id.managerLastName))
                .perform(typeText("Create"), closeSoftKeyboard());
        //creating after button click
        onView(withId(R.id.nameButtonM))
                .perform(click());

        Thread.sleep(5000);

        onView(withText("Succeeded in creating your account")).inRoot(isDialog()).withFailureHandler(new FailureHandler() {
            @Override
            public void handle(Throwable error, Matcher<View> viewMatcher){
                fail("Could not find alert");
            }
        }).check(matches(isDisplayed()));


    }

}
