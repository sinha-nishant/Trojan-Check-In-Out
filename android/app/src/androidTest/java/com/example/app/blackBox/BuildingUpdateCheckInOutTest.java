package com.example.app.blackBox;

import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.example.app.Credentials;
import com.example.app.R;
import com.example.app.building.Building;
import com.example.app.building.BuildingsOccupancyList;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.services.CheckInOut;
import com.example.app.users.StudentActivity;
import com.google.firebase.FirebaseApp;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.fail;

public class BuildingUpdateCheckInOutTest {
    //create account, building view, delete account
    public  String randBuilding;
    public  List<Building> post_value;
    public static Date checkInDate;
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public ActivityTestRule<BuildingsOccupancyList> menuActivityTestRule = new ActivityTestRule<>(BuildingsOccupancyList.class);

    @Before
    public void setUp() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void CheckInUpdate() throws InterruptedException {
        //get buildings from firebase
        MutableLiveData<List<Building>> buildingsMLD = new MutableLiveData<>();
        final Observer<List<Building>> observer = new Observer<List<Building>>(){
            @Override
            public void onChanged(@Nullable final List<Building> buildings){
                post_value=buildings;
            }
        };
        buildingsMLD.observeForever(observer);
        FbQuery.getAllBuildings(buildingsMLD);
        Thread.sleep(5000);
        //pick a random building to check into and store occupancy for that building
        Random rand = new Random();
        int i = rand.nextInt(post_value.size());
        randBuilding = post_value.get(i).getName();
        Integer old_occ = post_value.get(i).getOccupancy();
        //scroll to rand building
        String occupancymessage = "Occupancy: "+(old_occ)+"/"+post_value.get(i).getCapacity();
        onView(ViewMatchers.withId(R.id.studentRView))
                .perform(RecyclerViewActions
                        .scrollToPosition(i))
                .check(matches(hasDescendant(allOf(withText(post_value.get(i).getName()), hasSibling(withText(occupancymessage))))));
        Log.d("Random Building",randBuilding);
        Thread.sleep(2000);
        //check in
        MutableLiveData<Boolean> checkInMLD = new MutableLiveData<>();
        Observer<Boolean> observerCheckIn = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean success) {
                if(!success){//if doesn't check in student test fails
                    fail();
                }
            }
        };
        checkInMLD.observeForever(observerCheckIn);
        CheckInOut.checkIn(checkInMLD,randBuilding, Long.parseLong(Credentials.id));
        Thread.sleep(5000);
        //make sure that the view for the manager shows that the building checked into has increased occupancy by 1
        occupancymessage = "Occupancy: "+(old_occ+1)+"/"+post_value.get(i).getCapacity();
        onView(ViewMatchers.withId(R.id.studentRView))
                .perform(RecyclerViewActions
                        .scrollToPosition(i))
                .check(matches(hasDescendant(allOf(withText(post_value.get(i).getName()), hasSibling(withText(occupancymessage))))));
        Thread.sleep(2000);
        //check out
        MutableLiveData<Boolean> checkOutMLD = new MutableLiveData<>();
        Observer<Boolean> observerCheckOut = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean success) {
                if(!success){//if doesn't check out student test fails
                    fail();
                }else{
                    checkInDate = new Date();
                }
            }
        };
        checkOutMLD.observeForever(observerCheckOut);
        StudentActivity sa = new StudentActivity(randBuilding,checkInDate,null);
        //check out student with the use of Services package
        CheckInOut.checkOut(checkOutMLD,sa,Long.parseLong(Credentials.id));
        Thread.sleep(5000);

        //make sure that the view for the manager shows that the building checked out has decreased occupancy by 1
        occupancymessage = "Occupancy: "+(old_occ)+"/"+post_value.get(i).getCapacity();
        onView(ViewMatchers.withId(R.id.studentRView))
                .perform(RecyclerViewActions
                        .scrollToPosition(i))
                .check(matches(hasDescendant(allOf(withText(post_value.get(i).getName()), hasSibling(withText(occupancymessage))))));
        Thread.sleep(1000);
    }

}