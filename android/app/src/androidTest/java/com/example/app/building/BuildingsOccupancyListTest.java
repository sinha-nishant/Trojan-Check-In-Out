package com.example.app.building;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import androidx.annotation.ContentView;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;

import com.example.app.R;
import com.example.app.account_UI.QRScan;
import com.example.app.firebaseDB.FbQuery;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import kotlinx.coroutines.GlobalScope;

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
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();



    @Test
    public void onStart() {
        //get the card by finding buildingname using withChild(buildingname)
        //once we have card try withChild("occuapncy/cap")
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        MutableLiveData<List<Building>> builingMLD = new MutableLiveData<>();

        final Observer<List<Building>> observer = new Observer<List<Building>>(){

            @Override
            public void onChanged(@Nullable final List<Building> buildings){

               for(int i=0;i<buildings.size();i++){
                   String occupancymessage = "Occupancy: "+buildings.get(i).getOccupancy().toString()+"/100";
                   onView(ViewMatchers.withId(R.id.recyclerList))
                           .perform(RecyclerViewActions
                                   .scrollTo(hasDescendant(withText(buildings.get(i).getName()))))
                           .check(matches(hasDescendant(allOf(withText(buildings.get(i).getName()), hasSibling(withText(occupancymessage))))));

               }

            }
        };
        builingMLD.observeForever(observer);
        FbQuery.getAllBuildings(builingMLD);

        FbQuery.getAllBuildings(builingMLD);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //getBuildings
        //use
        /* onView(ViewMatchers.withId(R.id.recyclerList))
                .perform(RecyclerViewActions
                .scrollTo(hasDescendant(withText(building.GetName()))))
                .check(matches(hasDescendant(allOf(withText(building.GetName()), hasSibling(withText("Occupancy: "+building.GetOccupancy().toString()+"/100"))))));*/

//        onView(ViewMatchers.withId(R.id.recyclerList))
//                .perform(RecyclerViewActions
//                .scrollTo(hasDescendant(withText("Annenberg House"))))
//                .check(matches(hasDescendant(allOf(withText("Annenberg House"), hasSibling(withText("Occupancy: 89/100"))))));
//        try{Thread.sleep(2001);}
//
//        catch (Exception e){
//            e.printStackTrace();
//
//        }
    }
}