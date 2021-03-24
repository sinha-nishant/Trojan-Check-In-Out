package com.example.app.building;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import androidx.annotation.ContentView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;

import com.example.app.R;
import com.example.app.account_UI.QRScan;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.material.card.MaterialCardView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.assertion.ViewAssertions.selectedDescendantsMatch;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.JMock1Matchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
public class BuildingsOccupancyListTest {

    @Rule
    public ActivityTestRule<BuildingsOccupancyList> activityRule =
            new ActivityTestRule(BuildingsOccupancyList.class);



    @Test
    public void onStart() {
        //get the card by finding buildingname using withChild(buildingname)
        //once we have card try withChild("occuapncy/cap")

        try{Thread.sleep(2001);}

        catch (Exception e){
            e.printStackTrace();

        }

//        onView(ViewMatchers.withId(R.id.recyclerList))
//                // scrollTo will fail the test if no item matches.
//                .perform(RecyclerViewActions.scrollTo(
//                        hasDescendant(allOf(withText("Annenberg House"),
//                                hasSibling(withText(containsString("Occupancy: 88/100"))),
//                                isDisplayed()))
//        ));

        onView(ViewMatchers.withId(R.id.recyclerList))
                .perform(RecyclerViewActions
                .scrollTo(hasDescendant(withText("Annenberg House"))))
                .check(matches(hasDescendant(allOf(withText("Annenberg House"), hasSibling(withText("Occupancy: 89/100"))))));
    }
}