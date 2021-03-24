package com.example.app.blackBox;

import android.util.Log;
import android.view.View;

import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.app.R;
import com.example.app.pre_login_UI.LogInPage;
import com.example.app.pre_login_UI.StudentSignUpStart;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import java.nio.charset.Charset;
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

public class CreateStudentTest {
    public static String email;
    public static String uscID;

    @Rule
    public ActivityScenarioRule<StudentSignUpStart> activityRule =
            new ActivityScenarioRule<>(StudentSignUpStart.class);

    @Test
    public void CreateSuccess() throws InterruptedException {
        byte[] array = new byte[20];
        new Random().nextBytes(array);

//        String generatedString = new String(array, Charset.forName("UTF-8"));
//        generatedString+="@usc.edu";
//        email=generatedString;

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
        email=generatedString;

        Log.d("email",email);


        onView(ViewMatchers.withId(R.id.studentSignUpEmailAddress))
                .perform(typeText(generatedString), closeSoftKeyboard());
        onView(withId(R.id.studentSignUpPassword))
                .perform(typeText("pass"), closeSoftKeyboard());

        onView(withId(R.id.studentEmailPassSubmitButton))
                .perform(click());

        onView(withId(R.id.studentFirstName))
                .perform(typeText("My"), closeSoftKeyboard());
        onView(withId(R.id.studentLastName))
                .perform(typeText("Test"), closeSoftKeyboard());

        onView(withId(R.id.nameButton))
                .perform(click());
//        array = new byte[10]; // length is bounded by 7
//        new Random().nextBytes(array);
//
//        generatedString = new String(array, Charset.forName("UTF-8"));
        long leftLimit = 1000000000L;
        long rightLimit = 9999999999L;
        long generatedLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
        String id= String.valueOf(generatedLong);
        uscID=id;
        onView(withId(R.id.studentID))
                .perform(typeText(id), closeSoftKeyboard());
//        onView(withId(R.id.studentMajor))//add password to textview
//                .perform(typeText("Test"), closeSoftKeyboard());
        onView(withId(R.id.signup))
                .perform(click());

        onView(withId(R.id.submit))
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
